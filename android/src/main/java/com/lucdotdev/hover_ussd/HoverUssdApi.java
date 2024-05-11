package com.lucdotdev.hover_ussd;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.content.ContextCompat;

import com.hover.sdk.actions.HoverAction;
import com.hover.sdk.api.Hover;
import com.hover.sdk.api.HoverParameters;
import com.hover.sdk.database.HoverRoomDatabase;
import com.hover.sdk.transactions.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HoverUssdApi {

    private final Activity activity;
    private final Context context;

    public HoverUssdApi(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
    }

    public void initialize(String apiKey, String branding, String logo, String notificationLogo, Hover.DownloadListener downloadListener) {
        Hover.initialize(context, apiKey, true, downloadListener);

        int logoResourceId = getResourceId(logo == null ? "ic_launcher" : logo);
        int notificationLogoResourceId = getResourceId(notificationLogo == null ? "ic_launcher" : notificationLogo);
        Hover.setBranding(branding == null ? "Hover Ussd Plugin" : branding, logoResourceId, notificationLogoResourceId, context);
    }

    public boolean hasAllPerms() {
        return Hover.hasAllPerms(context);
    }

    public boolean hasAccessibilityPermission() {
        return Hover.isAccessibilityEnabled(context);
    }

    public boolean hasOverlayPermission() {
        return Hover.isOverlayEnabled(context);
    }

    public boolean hasContactPermission() {
        return Build.VERSION.SDK_INT < 23 || hasPermission(new String[]{Manifest.permission.READ_CONTACTS}, context);
    }

    public  boolean hasWritePermission() {
        return Build.VERSION.SDK_INT < 23 || hasPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, context);
    }

    public boolean hasSmsPermission() {
        return Build.VERSION.SDK_INT < 23 || hasPermission(new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS}, context);
    }

    public  boolean hasPhonePermission() {
        return Build.VERSION.SDK_INT < 23 || hasPermission(new String[]{Manifest.permission.READ_PHONE_STATE}, context);
    }

    private static boolean hasPermission(String[] permissions, Context context) {
        if (context == null) return false;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
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
    public  void setPermissionsActivity(String activityString){
        Hover.setPermissionActivity(activityString, context);
    }

    public void sendUssd(String action_id,
                         HashMap<String, String> extra,
                         String theme,
                         String header,
                         String initialProcessingMessage,
                         boolean showUserStepDescriptions,
                         int finalMsgDisplayTime,
                         BroadcastReceiver transactionStateReceiver) {

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
        activity.startActivityForResult(buildIntent, 0);
    }
}
