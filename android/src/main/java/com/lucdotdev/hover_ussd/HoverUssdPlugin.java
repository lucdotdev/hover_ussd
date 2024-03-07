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


public class HoverUssdPlugin implements FlutterPlugin, ActivityAware, MethodChannel.MethodCallHandler, PluginRegistry.ActivityResultListener, EventChannel.StreamHandler {


    private Activity activity;


    private EventChannel.EventSink eventSink;
    private BroadcastReceiver smsReceiver;

    private HoverUssdApi hoverUssdApi;


    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {


        MethodChannel channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "HoverUssdChannel");
        EventChannel eventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "TransactionEvent");

        eventChannel.setStreamHandler(this);
        channel.setMethodCallHandler(this);


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


    }

    private String intentNullAwareString(Intent intent, String name) {
        return intent.hasExtra(name) ? intent.getStringExtra(name) : "";
    }

    private Map<String, Object> actionToMap(HoverAction hoverAction) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", hoverAction.id);
        map.put("public_id", hoverAction.public_id);
        map.put("name", hoverAction.name);
        map.put("channel_id", hoverAction.channel_id);
        map.put("network_name", hoverAction.network_name);
        map.put("hni_list", hoverAction.hni_list); // Convert JSONArray to String
        map.put("country_alpha2", hoverAction.country_alpha2);
        map.put("root_code", hoverAction.root_code);
        map.put("transport_type", hoverAction.transport_type);
        map.put("transaction_type", hoverAction.transaction_type);
        map.put("custom_steps", hoverAction.custom_steps); // Convert JSONArray to String
        map.put("from_institution_id", hoverAction.from_institution_id);
        map.put("from_institution_name", hoverAction.from_institution_name);
        map.put("from_institution_logo", hoverAction.from_institution_logo);
        map.put("to_institution_id", hoverAction.to_institution_id);
        map.put("to_institution_name", hoverAction.to_institution_name);
        map.put("to_institution_logo", hoverAction.to_institution_logo);
        map.put("to_country_alpha2", hoverAction.to_country_alpha2);
        map.put("tags_list", hoverAction.tags_list); // Convert JSONArray to String
        map.put("created_timestamp", hoverAction.created_timestamp);
        map.put("updated_timestamp", hoverAction.updated_timestamp);
        map.put("bounty_amount", hoverAction.bounty_amount);
        map.put("bounty_is_open", hoverAction.bounty_is_open);
        map.put("is_ready", hoverAction.is_ready);
        map.put("required_params", hoverAction.required_params); // Convert JSONObject to String
        map.put("output_params", hoverAction.output_params); // Convert JSONObject to String
        map.put("bonus_percent", hoverAction.bonus_percent);
        map.put("bonus_message", hoverAction.bonus_message);

        return map;

    }


    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        hoverUssdApi = new HoverUssdApi(activity, eventSink);

        switch (call.method) {
            case "Initialize":
                hoverUssdApi.initialize(call.argument("branding"), call.argument("logo"), call.argument("notificationLogo"), new Hover.DownloadListener() {
                            @Override
                            public void onError(String s) {
                                result.success(null);

                            }

                            @Override
                            public void onSuccess(ArrayList<HoverAction> arrayList) {
                                ArrayList<Map<String, Object>> actionsList = new ArrayList<>();

                                for (HoverAction hoverAction : arrayList) {

                                    actionsList.add(actionToMap(hoverAction));
                                }

                                result.success(actionsList);
                            }
                        }
                );
                break;

            case "HasAllPermissions":
                result.success(hoverUssdApi.hasAllPerms());
                break;
            case "getAllActions":
                hoverUssdApi.getAllActions(new Hover.DownloadListener() {
                    @Override
                    public void onError(String s) {
                        result.success(null);

                    }

                    @Override
                    public void onSuccess(ArrayList<HoverAction> arrayList) {
                        ArrayList<Map<String, Object>> actionsList = new ArrayList<>();

                        for (HoverAction hoverAction : arrayList) {

                            actionsList.add(actionToMap(hoverAction));
                        }

                        result.success(actionsList);
                    }
                });

            case "HoverStartATransaction":
                Map<String, Object> resultJson = new HashMap<>();
                resultJson.put("state", "ussdLoading");
                eventSink.success(resultJson);
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
                        call.hasArgument("finalMsgDisplayTime") ? (int) call.argument("finalMsgDisplayTime") : 5000


                );

                LocalBroadcastManager.getInstance(activity.getBaseContext()).registerReceiver(smsReceiver, new IntentFilter(activity.getPackageName() + ".SMS_MISS"));


                break;
            default:
                result.notImplemented();

        }

    }


    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        activity = binding.getActivity();
        binding.addActivityResultListener(this);

    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        activity = null;
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {

        eventSink.endOfStream();


    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivity() {
        LocalBroadcastManager.getInstance(activity.getBaseContext()).unregisterReceiver(smsReceiver);
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

        } else if (requestCode == 0 && resultCode == Activity.RESULT_CANCELED) {
            Map<String, Object> result = new HashMap<>();
            result.put("state", "ussdFailed");
            if (data != null) {
                result.put("errorMessage", data.getStringExtra("error"));
            }
            eventSink.success(result);

            return false;
        } else {
            Map<String, Object> result = new HashMap<>();


            result.put("state", "ussdFailed");
            if (data != null) {
                result.put("errorMessage", data.getStringExtra("error"));
            }
            eventSink.success(result);
            return false;
        }

    }



    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
        if (eventSink == null) {
            eventSink = events;
        }
    }

    @Override
    public void onCancel(Object arguments) {
        eventSink = null;
    }

}