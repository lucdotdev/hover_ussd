package com.lucdotdev.hover_ussd;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.hover.sdk.api.Hover;

import java.util.HashMap;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.plugin.common.PluginRegistry;

/** HoverUssdPlugin */
public class HoverUssdPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware ,PluginRegistry.ActivityResultListener {


  private MethodChannel channel;
  private Activity activity;

  private  HoverUssdApi hoverUssdApi;




  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "hover_ussd");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("hoverStartTransaction")) {


      hoverUssdApi.sendUssd((String) call.argument("action_id"), (HashMap<String, String>) call.argument("extras"));

    } else if(call.method.equals("hoverInitial")) {
      Hover.initialize(activity);
    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {

    activity = binding.getActivity();

    ///Instanciate hoverApi
    hoverUssdApi = new HoverUssdApi(activity);


    binding.addActivityResultListener(this);

  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {


  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
  }

  @Override
  public void onDetachedFromActivity() {
    activity = null;
    ///this help us to destroy the smsReceiver
    hoverUssdApi.destroySmsReceiver();
  }


  @Override
  public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == 0 && resultCode == Activity.RESULT_OK) {

      String[] sessionTextArr = data.getStringArrayExtra("session_messages");
      String uuid = data.getStringExtra("uuid");

      return true;

    } else if (requestCode == 0 && resultCode == Activity.RESULT_CANCELED) {
      Toast.makeText(activity, "Error: " + data.getStringExtra("error"), Toast.LENGTH_LONG).show();
      return true;
    }
    return false;
  }
}
