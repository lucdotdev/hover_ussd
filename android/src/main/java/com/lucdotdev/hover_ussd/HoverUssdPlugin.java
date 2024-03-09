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


public class HoverUssdPlugin implements FlutterPlugin, ActivityAware, MethodChannel.MethodCallHandler, PluginRegistry.ActivityResultListener{


    private Activity activity;


    private EventChannel.EventSink transactionEventSink;
    private EventChannel.EventSink actionDownloadEventSink;
    private BroadcastReceiver smsReceiver;

    private HoverUssdApi hoverUssdApi;


    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {

        MethodChannel channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "HoverUssdChannel");
        EventChannel transactionEventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "TransactionEvent");
        EventChannel actionDownloadEventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "ActionDownloadEvent");

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

    private String intentNullAwareString(Intent intent, String name) {
        return intent.hasExtra(name) ? intent.getStringExtra(name) : "";
    }


    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        hoverUssdApi = new HoverUssdApi(activity, transactionEventSink);
        switch (call.method) {
            case "Initialize":
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
             
                break;
            case "HasAllPermissions":
                result.success(hoverUssdApi.hasAllPerms());
                break;
            case "getAllActions":
                result.success(hoverUssdApi.getAllActions());
                break;
            case "getAllTransactions":
                result.success(hoverUssdApi.getAllTransaction());
                break;
            case "refreshActions":
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
                break;

            case "HoverStartATransaction":
                Map<String, Object> resultJson = new HashMap<>();
                resultJson.put("state", "ussdLoading");
                transactionEventSink.success(resultJson);

                try {
                    hoverUssdApi.sendUssd(
                            (String) call.argument("actionId"),
                            call.hasArgument("extras") ?
                                    Objects.requireNonNull(call.argument("extras"))
                                    : new HashMap<String, String>(),
                            call.hasArgument("theme") ?
                                    (String) call.argument("theme") :
                                    "",
                            call.hasArgument("header") ?
                                    (String) call.argument("header")
                                    : "",
                            call.hasArgument("initialProcessingMessage") ?
                                    (String) call.argument("initialProcessingMessage")
                                    : "",
                            false,
                            call.hasArgument("finalMsgDisplayTime") ? (int) call.argument("finalMsgDisplayTime") : 5000);
                } catch (Exception e) {

                    Map<String, Object> resultError = new HashMap<>();
                    resultError.put("state", "ussdFailed");

                    resultError.put("errorMessage", e.getMessage());

                    transactionEventSink.success(resultError);

                }

                result.success(true);

                break;
            default:
                result.notImplemented();

        }
        hoverUssdApi = null;

    }


    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        activity = binding.getActivity();
        binding.addActivityResultListener(this);
        smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
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
        };
        LocalBroadcastManager.getInstance(activity.getBaseContext()).registerReceiver(smsReceiver, new IntentFilter(activity.getPackageName() + ".SMS_MISS"));

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
        LocalBroadcastManager.getInstance(activity.getBaseContext()).unregisterReceiver(smsReceiver);
        activity = null;
    }


    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 4000 && resultCode == Activity.RESULT_OK) {
            String uuid = data.hasExtra("uuid") ? data.getStringExtra("uuid") : "";

            Map<String, Object> result = new HashMap<>();
            result.put("state", "ussdSucceeded");
            result.put("uuid", uuid);
            if (data.hasExtra("session_messages")) {
                String[] sessionMessages = data.getStringArrayExtra("session_messages");
                result.put("ussdSessionMessages", sessionMessages);
            }

            transactionEventSink.success(result);

            return true;

        } else if (requestCode == 4000 && data.hasExtra("error")) {
            Map<String, Object> result = new HashMap<>();
            result.put("state", "ussdFailed");
            result.put("errorMessage", data.getStringExtra("error"));
            transactionEventSink.success(result);

            return false;
        }

        return false;

    }



}