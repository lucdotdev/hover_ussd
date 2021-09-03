package com.lucdotdev.hover_ussd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.hover.sdk.actions.HoverAction;
import com.hover.sdk.api.Hover;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import io.flutter.Log;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.plugin.common.PluginRegistry;

/**
 * HoverUssdPlugin
 */
public class HoverUssdPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware, PluginRegistry.ActivityResultListener, EventChannel.StreamHandler {


    private MethodChannel channel;
    private Activity activity;


    private EventChannel eventChannel;
    private EventChannel.EventSink eventSink;



    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {


        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "HoverUssdChannel");
        eventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "TransactionEvent");
        eventChannel.setStreamHandler(this);
        channel.setMethodCallHandler(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        HoverUssdApi hoverUssdApi = new HoverUssdApi(activity);
        switch (call.method) {
            case "HoverStartATransaction":
                hoverUssdApi.sendUssd((String) call.argument("actionId"),
                        (HashMap<String, String>) Objects.requireNonNull(call.argument("extras")),
                        (String) call.argument("theme"));
                break;
            case "Initialize":

                Hover.initialize(activity.getApplicationContext());
                if (call.argument("branding") != null && call.argument("logo") != null) {
                    Context context = activity.getApplicationContext();
                    int id = context.getResources().getIdentifier((String) call.argument("logo"), "drawable", context.getPackageName());
                    Hover.setBranding((String) call.argument("branding"), id, activity);
                }
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
            Log.d("HOVER_MSG:", "The ussd action called succefully");
            eventSink.success("success");

            return true;

        } else if (requestCode == 0 && resultCode == Activity.RESULT_CANCELED) {

            Log.e("HOVER_ERROR:", "Hover ussd transaction error");
            eventSink.success("failed");

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