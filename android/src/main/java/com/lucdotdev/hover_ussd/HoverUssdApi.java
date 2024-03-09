package com.lucdotdev.hover_ussd;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hover.sdk.actions.HoverAction;
import com.hover.sdk.api.Hover;
import com.hover.sdk.api.HoverParameters;
import com.hover.sdk.database.HoverRoomDatabase;
import com.hover.sdk.transactions.Transaction;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.EventChannel;

public class HoverUssdApi {


    private final Activity activity;
    private final EventChannel.EventSink eventSink;

    private final Context context;

    public HoverUssdApi(Activity activity, EventChannel.EventSink eventSink) {
        this.activity = activity;
        this.eventSink = eventSink;
        this.context = activity.getApplicationContext();
    }

    public void initialize(String apiKey, String branding, String logo, String notificationLogo, Hover.DownloadListener downloadListener) {
        Hover.initialize(context, apiKey, true, downloadListener);
        if (branding != null && logo != null && notificationLogo != null) {
            int logoResourceId = getResourceId(context, logo);
            int notificationLogoResourceId = getResourceId(context, notificationLogo);
            if (logoResourceId == 0) {
                Log.e("HOVER_USSD_PLUGIN", ":LOGO NOT FOUND");
            } else {
                Hover.setBranding(branding, logoResourceId, notificationLogoResourceId, context);
            }
        }
    }

    public boolean hasAllPerms() {
        return Hover.hasAllPerms(activity.getApplicationContext());
    }


    // get all actions from hover and convert to an list of array map


    public ArrayList<Map<String, Object>> getAllActions() {
        List<HoverAction> actions = HoverRoomDatabase.getInstance(context).actionDao().getAll();
        ArrayList<Map<String, Object>> mapActions = new ArrayList<>();

        for (HoverAction action : actions) {
            Map<String, Object> mapAction = HoverUssdObjectToMap.convertHoverActionToMap(action);
            mapActions.add(mapAction);
        }

        return mapActions;
    }

    public void updateActionConfigs(Hover.DownloadListener actionDownloadListener) {
        Hover.updateActionConfigs(actionDownloadListener, context);
    }

    public void refreshActions(Hover.DownloadListener actionDownloadListener) {
        Hover.updateActionConfigs(actionDownloadListener, context);
    }


    public ArrayList<Map<String, Object>> getAllTransaction() {

        List<Transaction> transactions = HoverRoomDatabase.getInstance(context).transactionDao().getAll();
        ArrayList<Map<String, Object>> mapTransactions = new ArrayList<>();

        for (Transaction transaction : transactions) {

            Map<String, Object> mapTransaction = HoverUssdObjectToMap.convertTransactionToMap(transaction);
            mapTransactions.add(mapTransaction);
        }

        return mapTransactions;

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


        final HoverParameters.Builder builder = new HoverParameters.Builder(context);

        builder.request(action_id);

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
