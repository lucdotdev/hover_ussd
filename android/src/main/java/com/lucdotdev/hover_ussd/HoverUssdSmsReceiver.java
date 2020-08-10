package com.lucdotdev.hover_ussd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.HashMap;

public class HoverUssdSmsReceiver  extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        String uuid = intent.getStringExtra("uuid");
        String confirmationCode, balance;
        if (intent.hasExtra("parsed_variables")) {
            HashMap<String, String> parsed_variables = (HashMap<String, String>) intent.getSerializableExtra("parsed_variables");
            if (parsed_variables.containsKey("confirmCode"))
                confirmationCode = parsed_variables.get("confirmCode");
            if (parsed_variables.containsKey("balance"))
                balance = parsed_variables.get("balance");
        }
    }
}
