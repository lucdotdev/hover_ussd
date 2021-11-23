package com.lucdotdev.hover_ussd;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Build;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.hover.sdk.api.Hover;
import com.hover.sdk.transactions.Transaction;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
public class HoverUssdPlugin implements FlutterPlugin, ActivityAware, MethodChannel.MethodCallHandler, PluginRegistry.ActivityResultListener, EventChannel.StreamHandler {



    private MethodChannel channel;
    private Activity activity;


    private EventChannel eventChannel;
    private EventChannel.EventSink eventSink;
    private BroadcastReceiver smsReceiver;



    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {


        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "HoverUssdChannel");
        eventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "TransactionEvent");

        eventChannel.setStreamHandler(this);
        channel.setMethodCallHandler(this);
    }

    private String intentNullAwareString(Intent intent, String name) {
        return intent.hasExtra(name) ? intent.getStringExtra(name) : "";
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        HoverUssdApi hoverUssdApi = new HoverUssdApi(activity, activity.getApplicationContext());
        switch (call.method) {
            case "Initialize":

                Hover.initialize(activity);
                if (call.argument("branding") != null && call.argument("logo") != null) {
                    String[] parts = ((String) call.argument("logo")).split("/");
                    String resourceType = parts[0];
                    String resourceName = parts[1];

                    int id =activity.getApplicationContext().getResources().getIdentifier(resourceName, resourceType, activity.getApplicationContext().getPackageName());
                    Hover.setBranding((String) call.argument("branding"), id, activity.getApplicationContext());
                }
                break;
            case "HoverStartATransaction":
                Map<String, Object> resultJson = new HashMap<>();
                resultJson.put("state", "ussdLoading");
                eventSink.success(resultJson);
                hoverUssdApi.sendUssd(
                        (String) call.argument("actionId"),
                        call.hasArgument("extras") ?
                                (HashMap<String, String>) Objects.requireNonNull(call.argument("extras"))
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
                        call.hasArgument("finalMsgDisplayTime") ?
                                (int) call.argument("finalMsgDisplayTime")
                                : 5000,
                        false


                );
                break;
            default:
                result.notImplemented();

        }

    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
        eventChannel.setStreamHandler(null);

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


                eventSink.success(result);
            }
        };


        activity.registerReceiver(smsReceiver
                , new IntentFilter("com.lucdotdev.hover_ussd.CONFIRMED_TRANSACTION"));

    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        activity = null;
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivity() {
        activity = null;
    }


    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            String uuid = data.hasExtra("uuid") ? data.getStringExtra("uuid") : "";

            Map<String, Object> result = new HashMap<>();
            result.put("state", "ussdSucceded");
            result.put("uuid", uuid);
            if (data.hasExtra("session_messages")) {
                String[] sessionMessages = data.getStringArrayExtra("session_messages");
                result.put("ussdSessionMessages", sessionMessages);
            }

            eventSink.success(result);

            return true;

        }
        else if (requestCode == 0 && resultCode == Activity.RESULT_CANCELED) {
            Map<String, Object> result = new HashMap<>();
            result.put("state", "ussdFailed");
            if (data != null) {
                result.put("errorMessage", data.getStringExtra("error"));
            }
            eventSink.success(result);

            return true;
        }
        return false;
    }

    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
        eventSink = events;
    }

    @Override
    public void onCancel(Object arguments) {
        eventSink = null;
    }

}