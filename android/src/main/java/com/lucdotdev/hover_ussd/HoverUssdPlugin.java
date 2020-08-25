package com.lucdotdev.hover_ussd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.hover.sdk.api.Hover;

import java.util.HashMap;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.plugin.common.PluginRegistry;

/** HoverUssdPlugin */
public class HoverUssdPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware ,PluginRegistry.ActivityResultListener, EventChannel.StreamHandler, HoverUssdSmsReceiver.HoverUssdReceiverInterface {


  private MethodChannel channel;
  private Activity activity;


  private  HoverUssdApi hoverUssdApi;
  private EventChannel eventChannel;
  private EventChannel.EventSink eventSink;
 


  private final BroadcastReceiver smsReceiver = new BroadcastReceiver(){
    @Override
    public void onReceive(final Context context, final Intent i){
      Toast.makeText(activity, "Error: " + i.getStringExtra("status"), Toast.LENGTH_LONG).show();
      eventSink.success("failed");
    }
  };


  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {

    new HoverUssdSmsReceiver(this);
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "hover_ussd");
    eventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "transaction_event");
    eventChannel.setStreamHandler(this);
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("hoverStartTransaction")) {

      hoverUssdApi = new HoverUssdApi(activity);
      hoverUssdApi.sendUssd((String) call.argument("action_id"), (HashMap<String, String>) call.argument("extras"),smsReceiver);


    } else if(call.method.equals("hoverInitial")) {
      Hover.initialize(activity.getApplicationContext());
    } else {
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
    ///this help us to destroy the smsReceiver
   // hoverUssdApi.destroySmsReceiver();

  }


  @Override
  public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == 0 && resultCode == Activity.RESULT_OK) {

      Toast.makeText(activity, "Please wait for confirmation", Toast.LENGTH_LONG).show();
      eventSink.success("pending");

      return true;

    } else if (requestCode == 0 && resultCode == Activity.RESULT_CANCELED) {

      Toast.makeText(activity, "Error: ", Toast.LENGTH_LONG).show();

      eventSink.success("failed");

      return true;
    }
    return false;
  }

  @Override
  public  void onListen(Object arguments, EventChannel.EventSink events) {
    eventSink = events;
  }

  @Override
  public void onCancel(Object arguments) {

  }

  @Override
  public void onRecevedData(String msg) {
    Toast.makeText(activity, "Error: " +msg, Toast.LENGTH_LONG).show();
    eventSink.success(msg);
  }
}
