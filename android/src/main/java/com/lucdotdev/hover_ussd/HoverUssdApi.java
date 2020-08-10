package com.lucdotdev.hover_ussd;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.hover.sdk.api.HoverParameters;

import java.util.HashMap;
import java.util.Map;

public class HoverUssdApi {

    private Activity activity;

    public HoverUssdApi(Activity activity) {
        this.activity = activity;
    }

    ///This Start Listennig SMS before the begin of Transaction
    ///
    ///


    private final BroadcastReceiver smsReceiver = new BroadcastReceiver() {
        final String TAG = "SMS";
        @Override
        public void onReceive(final Context context, final Intent i) {
            Log.i(TAG, "Recieved SMS miss broadcast");
            Log.i(TAG, "message: " + i.getStringExtra("msg"));
            Log.i(TAG, "sender: " + i.getStringExtra("sender"));
            Log.i(TAG, "transaction_uuid: " + i.getStringExtra("transaction_uuid"));
            Log.i(TAG, "action_id: " + i.getStringExtra("action_id"));

        }
    };

    ///The method sendUssd begin the ussd Transaction
    ///
    ///

    public void sendUssd(String action_id, HashMap<String, String> extra) {

        LocalBroadcastManager.getInstance(activity).registerReceiver(smsReceiver, new IntentFilter("MY-PACKAGE-NAME.SMS_MISS"));

        ///Initialize @HoverBuilder
        final HoverParameters.Builder builder = new HoverParameters.Builder(activity).request(action_id);

        ///If there are action with variables
        ///
        if(!extra.isEmpty()){
           for (Map.Entry<String, String> entry: extra.entrySet()){
               builder.extra(entry.getKey(), entry.getValue());
           }
        }

        Intent buildIntent = builder.buildIntent();
        activity.startActivityForResult(buildIntent, 0);
    }




    ///This help us to unregister a the @sms receiver
    public void destroySmsReceiver(){
        activity.unregisterReceiver(smsReceiver);
    }


}
