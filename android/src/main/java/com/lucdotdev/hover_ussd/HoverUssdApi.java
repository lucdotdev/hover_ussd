package com.lucdotdev.hover_ussd;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;



import com.hover.sdk.api.HoverParameters;

import java.util.HashMap;
import java.util.Map;



public class HoverUssdApi {


    private Activity activity;
    private Context context;



    public HoverUssdApi(Activity activity, Context context) {
        this.activity = activity;

        this.context = context;
    }

    public void sendUssd(String action_id,
                         HashMap<String, String> extra,
                         String theme,
                         String header,
                         String initialProcessingMessage,
                         int finalMsgDisplayTime,
                         boolean showUserStepDescriptions) {
        
        final HoverParameters.Builder builder = new HoverParameters.Builder(context).request(action_id);

        if (extra != null) {
            if (!extra.isEmpty()) {
                for (Map.Entry<String, String> entry : extra.entrySet()) {
                    builder.extra(entry.getKey(), entry.getValue());
                }
            }
        }


        if (theme != null) {
            int id = context.getResources().getIdentifier(theme, "style", context.getPackageName());
            builder.style(id);

        }
        if (header != null) {
            builder.setHeader(header);
        }
        if (initialProcessingMessage != null) {
            builder.initialProcessingMessage(initialProcessingMessage);
        }


        builder.showUserStepDescriptions(showUserStepDescriptions);
        builder.finalMsgDisplayTime(finalMsgDisplayTime);

        Intent buildIntent = builder.buildIntent();

        activity.startActivityForResult(buildIntent, 0);


    }
}