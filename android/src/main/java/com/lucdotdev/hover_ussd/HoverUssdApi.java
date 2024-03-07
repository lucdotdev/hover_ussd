package com.lucdotdev.hover_ussd;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hover.sdk.actions.HoverAction;
import com.hover.sdk.api.Hover;
import com.hover.sdk.api.HoverParameters;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.EventChannel;

public class HoverUssdApi {


    private final Activity activity;
    private final EventChannel.EventSink eventSink;

    public HoverUssdApi(Activity activity, EventChannel.EventSink eventSink) {
        this.activity = activity;
        this.eventSink = eventSink;
    }

    public void initialize(String branding, String logo, String notificationLogo, Hover.DownloadListener downloadListener) {
        Hover.initialize(activity.getApplicationContext(), false, downloadListener);
        if (branding != null && logo != null && notificationLogo != null) {
            Context context = activity.getApplicationContext();
            int logoResourceId = getResourceId(context, logo);
            int notificationLogoResourceId = getResourceId(context, notificationLogo);
            if (logoResourceId == 0) {
                Log.e("HOVER_USSD_PLUGIN", ":LOGO NOT FOUND");
            } else {
                Hover.setBranding(branding, logoResourceId, notificationLogoResourceId, activity.getApplicationContext());
            }
        }
    }

    public boolean hasAllPerms() {
        return Hover.hasAllPerms(activity.getApplicationContext());
    }


    // get all actions from hover and convert to an list of array map


    public void getAllActions(Hover.DownloadListener actionsDownloadListener) {
        Hover.updateActionConfigs(actionsDownloadListener, activity.getApplicationContext());
    }

    @Deprecated()
    public void getAllTransaction() {
    }

    private int getResourceId(Context context, String resourceName) {
        try {
            Class<?> res = R.drawable.class;
            Field field = res.getField(resourceName);
            return field.getInt(null);
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // Return 0 if resource ID is not found
        }
    }

    public void sendUssd(String action_id,
                         HashMap<String, String> extra,
                         String theme,
                         String header,
                         String initialProcessingMessage,
                         boolean showUserStepDescriptions,
                         int finalMsgDisplayTime

    ) {


        final HoverParameters.Builder builder = new HoverParameters.Builder(activity.getApplicationContext()).request(action_id);

        if (extra != null) {
            if (!extra.isEmpty()) {
                for (Map.Entry<String, String> entry : extra.entrySet()) {
                    builder.extra(entry.getKey(), entry.getValue());
                }
            }
        }


        if (theme != null) {
            int id = activity.getBaseContext().getResources().getIdentifier(theme, "style", activity.getApplicationContext().getPackageName());
            builder.style(id);

        }
        if (header != null) {
            builder.setHeader(header);
        }
        if (initialProcessingMessage != null) {
            builder.initialProcessingMessage(initialProcessingMessage);
        }


        builder.showUserStepDescriptions(showUserStepDescriptions);
        if (finalMsgDisplayTime != 0) {
            builder.finalMsgDisplayTime(finalMsgDisplayTime);
        }

        try {
            Intent buildIntent = builder.buildIntent();

            activity.startActivityForResult(buildIntent, 0);
        } catch (Exception e) {

            Map<String, Object> result = new HashMap<>();

            result.put("state", "ussdFailed");
            result.put("errorMessage", e);

            eventSink.success(result);

        }

    }
}
