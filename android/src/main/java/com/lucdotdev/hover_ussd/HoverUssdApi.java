package com.lucdotdev.hover_ussd;

import android.app.Activity;
import android.content.Intent;

import com.hover.sdk.api.HoverParameters;

import java.util.HashMap;
import java.util.Map;

public class HoverUssdApi {

    private Activity activity;

    public HoverUssdApi(Activity activity) {
        this.activity = activity;
    }

    public  void sendUssd(String action_id, HashMap<String, String> extra) {

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


}
