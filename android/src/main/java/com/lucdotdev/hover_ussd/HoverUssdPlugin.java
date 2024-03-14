package com.lucdotdev.hover_ussd;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.hover.sdk.actions.HoverAction;
import com.hover.sdk.api.Hover;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.flutter.Log;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;

/**
 * HoverUssdPlugin
 */


public class HoverUssdPlugin implements FlutterPlugin, ActivityAware, MethodChannel.MethodCallHandler, PluginRegistry.ActivityResultListener {

    private Activity activity;
    private EventChannel.EventSink transactionEventSink;
    private EventChannel.EventSink actionDownloadEventSink;
    private BroadcastReceiver smsReceiver;
    private BroadcastReceiver transactionStateReceiver;
    private HoverUssdApi hoverUssdApi;
    private Context context;
    private static final String TAG = "HOVER_USSD_PLUGIN";

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        MethodChannel channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "HoverUssdChannel");
        EventChannel transactionEventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "TransactionEvent");
        EventChannel actionDownloadEventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "ActionDownloadEvent");

        context = flutterPluginBinding.getApplicationContext();

        actionDownloadEventChannel.setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object arguments, EventChannel.EventSink events) {
                actionDownloadEventSink = events;
            }

            @Override
            public void onCancel(Object arguments) {
            }
        });

        transactionEventChannel.setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object arguments, EventChannel.EventSink events) {
                transactionEventSink = events;
            }

            @Override
            public void onCancel(Object arguments) {
            }
        });

        channel.setMethodCallHandler(this);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        activity = binding.getActivity();
        binding.addActivityResultListener(this);

        smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleSmsReceived(intent);
            }
        };
        LocalBroadcastManager.getInstance(activity).registerReceiver(smsReceiver, new IntentFilter(activity.getPackageName() + ".SMS_MISS"));
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        activity = null;
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        activity = binding.getActivity();
        binding.addActivityResultListener(this);
    }

    @Override
    public void onDetachedFromActivity() {
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(smsReceiver);
        if (transactionStateReceiver != null) {
            LocalBroadcastManager.getInstance(activity.getApplication()).unregisterReceiver(transactionStateReceiver);
        }
        activity = null;
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                handleUssdSuccess(data);
            } else if (resultCode == Activity.RESULT_CANCELED && data != null) {
                handleUssdFailed(data);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        hoverUssdApi = new HoverUssdApi(activity, context);
        switch (call.method) {
            case "Initialize":
                initializeHover(call);
                break;
            case "HasAllPermissions":
                result.success(hoverUssdApi.hasAllPerms());
                break;
            case "IsAccessibilityEnabled":
                result.success(hoverUssdApi.isAccessibilityEnabled());
                break;
            case "IsOverlayEnabled":
                result.success(hoverUssdApi.isOverlayEnabled());
                break;
            case "getAllActions":
                result.success(hoverUssdApi.getAllActions());
                break;
            case "getAllTransactions":
                result.success(hoverUssdApi.getAllTransaction());
                break;
            case "refreshActions":
                refreshActions();
                break;
            case "HoverStartATransaction":
                startHoverTransaction(call, result);
                break;
            default:
                result.notImplemented();
        }
        hoverUssdApi = null;
    }

    private void initializeHover(MethodCall call) {
        hoverUssdApi.initialize(call.argument("apiKey"), call.argument("branding"), call.argument("logo"), call.argument("notificationLogo"), new Hover.DownloadListener() {
            @Override
            public void onError(String s) {
                Map<String, Object> result = new HashMap<>();
                result.put("state", "actionDownloadFailed");
                result.put("error", s);
                actionDownloadEventSink.success(result);
            }

            @Override
            public void onSuccess(ArrayList<HoverAction> arrayList) {
                Map<String, Object> result = new HashMap<>();
                result.put("state", "actionDownloaded");
                result.put("isDownloaded", true);
                actionDownloadEventSink.success(result);
            }
        });
    }

    private void refreshActions() {
        hoverUssdApi.refreshActions(new Hover.DownloadListener() {
            @Override
            public void onError(String s) {
                Map<String, Object> result = new HashMap<>();
                result.put("state", "actionDownloadFailed");
                result.put("error", s);
                actionDownloadEventSink.success(result);
            }

            @Override
            public void onSuccess(ArrayList<HoverAction> arrayList) {
                Map<String, Object> result = new HashMap<>();
                result.put("state", "actionDownloaded");
                result.put("isDownloaded", true);
                actionDownloadEventSink.success(result);
            }
        });
    }

    private void startHoverTransaction(MethodCall call, MethodChannel.Result result) {
        Map<String, Object> resultJson = new HashMap<>();
        resultJson.put("state", "ussdLoading");
        transactionEventSink.success(resultJson);

        transactionStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent i) {
                transactionEventSink.success(i.getExtras());
                Log.i(TAG, "Received pending transaction created broadcast");
                Log.i(TAG, "uuid: " + i.getStringExtra("uuid"));
            }
        };

        try {
            hoverUssdApi.sendUssd(
                    Objects.requireNonNull(call.argument("actionId")),
                    call.argument("extras") != null ? call.argument("extras") : new HashMap<String, String>(),
                    call.argument("theme"),
                    call.argument("header"),
                    call.argument("initialProcessingMessage"),
                    false,
                    call.argument("finalMsgDisplayTime"),
                    transactionStateReceiver
            );
            result.success(true);
        } catch (Exception e) {
            Map<String, Object> resultError = new HashMap<>();
            resultError.put("state", "ussdFailed");
            resultError.put("errorMessage", e.getMessage());
            transactionEventSink.success(resultError);
        }
    }

    private void handleSmsReceived(Intent intent) {
        Map<String, Object> result = new HashMap<>();
        result.put("state", "smsParsed");
        result.put("action_id", intentNullAwareString(intent, "action_id"));
        result.put("response_message", intentNullAwareString(intent, "response_message"));
        result.put("status", intentNullAwareString(intent, "status"));
        result.put("status_meaning", intentNullAwareString(intent, "status_meaning"));
        result.put("status_description", intentNullAwareString(intent, "status_description"));
        result.put("uuid", intentNullAwareString(intent, "uuid"));
        result.put("im_hni", intentNullAwareString(intent, "im_hni"));
        result.put("environment", intent.getIntExtra("environment", 0));
        result.put("request_timestamp", intent.getIntExtra("request_timestamp", 0));
        result.put("response_timestamp", intent.getIntExtra("response_timestamp", 0));
        result.put("matched_parser_id", intentNullAwareString(intent, "matched_parser_id"));
        result.put("messagetype", intentNullAwareString(intent, "messagetype"));
        result.put("message_sender", intentNullAwareString(intent, "message_sender"));
        result.put("regex", intentNullAwareString(intent, "regex"));
        transactionEventSink.success(result);
    }

    private void handleUssdSuccess(Intent data) {
        String uuid = data.hasExtra("uuid") ? data.getStringExtra("uuid") : "";
        Map<String, Object> result = new HashMap<>();
        result.put("state", "ussdSucceeded");
        result.put("uuid", uuid);
        if (data.hasExtra("session_messages")) {
            String[] sessionMessages = data.getStringArrayExtra("session_messages");
            result.put("ussdSessionMessages", sessionMessages);
        }
        transactionEventSink.success(result);
    }

    private void handleUssdFailed(Intent data) {
        Map<String, Object> result = new HashMap<>();
        result.put("state", "ussdFailed");
        result.put("errorMessage", data.getStringExtra("error"));
        transactionEventSink.success(result);
    }

    private String intentNullAwareString(Intent intent, String name) {
        return intent.hasExtra(name) ? intent.getStringExtra(name) : "";
    }
}