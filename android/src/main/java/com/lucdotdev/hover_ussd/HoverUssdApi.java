package com.lucdotdev.hover_ussd;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;


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




        public void sendUssd(String action_id, HashMap<String, String> extra, BroadcastReceiver smsReceiver) {

            LocalBroadcastManager.getInstance(activity).registerReceiver(smsReceiver, new IntentFilter("com.lucdotdev.hover_ussd.SMS_MISS"));

            ///Initialize @HoverBuilder
            final HoverParameters.Builder builder = new HoverParameters.Builder(activity).request(action_id);

            ///If there are action with variables
            ///
            if (!extra.isEmpty()) {
                for (Map.Entry<String, String> entry : extra.entrySet()) {
                    builder.extra(entry.getKey(), entry.getValue());
                }
            }

            Intent buildIntent = builder.buildIntent();
            activity.startActivityForResult(buildIntent, 0);
        }


    }
