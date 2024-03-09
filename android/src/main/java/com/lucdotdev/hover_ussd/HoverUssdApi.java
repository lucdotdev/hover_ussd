package com.lucdotdev.hover_ussd;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

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
    private final Context context;


    public HoverUssdApi(Activity activity, EventChannel.EventSink eventSink) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
    }

    public void initialize(String apiKey, String branding, String logo, String notificationLogo, Hover.DownloadListener downloadListener) {
        Hover.initialize(context, apiKey, true, downloadListener);

        int logoResourceId = getResourceId(logo == null ? "ic_launcher" : logo);
        int notificationLogoResourceId = getResourceId(notificationLogo == null ? "ic_launcher" : notificationLogo);
        Hover.setBranding(branding == null ? "Hover Ussd Plugin" : branding, logoResourceId, notificationLogoResourceId, context);

    }

    public boolean hasAllPerms() {
        return Hover.hasAllPerms(activity.getApplicationContext());
    }

    public ArrayList<Map<String, Object>> getAllActions() {
        List<HoverAction> actions = HoverRoomDatabase.getInstance(context).actionDao().getAll();
        ArrayList<Map<String, Object>> mapActions = new ArrayList<>();

        for (HoverAction action : actions) {
            Map<String, Object> mapAction = HoverUssdObjectToMap.convertHoverActionToMap(action);
            mapActions.add(mapAction);
        }

        return mapActions;
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

    private int getResourceId(String resourceName) {
        try {
            return context.getResources().getIdentifier(resourceName, "mipmap", activity.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
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


        Intent buildIntent = builder.buildIntent();
        activity.startActivityForResult(buildIntent,4000 );


    }
}
