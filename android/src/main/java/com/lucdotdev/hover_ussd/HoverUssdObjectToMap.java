package com.lucdotdev.hover_ussd;

import com.hover.sdk.actions.HoverAction;
import com.hover.sdk.transactions.Transaction;

import java.util.HashMap;
import java.util.Map;

public class HoverUssdObjectToMap {

    public static Map<String, Object> convertHoverActionToMap(HoverAction hoverAction) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", hoverAction.id);
        map.put("public_id", hoverAction.public_id);
        map.put("name", hoverAction.name);
        map.put("channel_id", hoverAction.channel_id);
        map.put("network_name", hoverAction.network_name);
        map.put("country_alpha2", hoverAction.country_alpha2);
        map.put("root_code", hoverAction.root_code);
        map.put("transport_type", hoverAction.transport_type);
        map.put("transaction_type", hoverAction.transaction_type);
        map.put("from_institution_id", hoverAction.from_institution_id);
        map.put("from_institution_name", hoverAction.from_institution_name);
        map.put("from_institution_logo", hoverAction.from_institution_logo);
        map.put("to_institution_id", hoverAction.to_institution_id);
        map.put("to_institution_name", hoverAction.to_institution_name);
        map.put("to_institution_logo", hoverAction.to_institution_logo);
        map.put("to_country_alpha2", hoverAction.to_country_alpha2);
        map.put("created_timestamp", hoverAction.created_timestamp);
        map.put("updated_timestamp", hoverAction.updated_timestamp);
        map.put("bounty_amount", hoverAction.bounty_amount);
        map.put("bounty_is_open", hoverAction.bounty_is_open);
        map.put("is_ready", hoverAction.is_ready);
        map.put("bonus_percent", hoverAction.bonus_percent);
        map.put("bonus_message", hoverAction.bonus_message);

        // Ignoring 'parsers' field as it's not included in the map

        return map;
    }

    public static Map<String, Object> convertTransactionToMap(Transaction transaction) {
        Map<String, Object> map = new HashMap<>();

        map.put("id", transaction.id);
        map.put("channelId", transaction.channelId);
        map.put("statusId", transaction.statusId);
        map.put("env", transaction.env);
        map.put("actionId", transaction.actionId);
        map.put("actionName", transaction.actionName);
        map.put("uuid", transaction.uuid);
        map.put("status", transaction.status);
        map.put("category", transaction.category);
        map.put("userMessage", transaction.userMessage);
        map.put("networkHni", transaction.networkHni);
        map.put("myType", transaction.myType);
        map.put("toInstitutionName", transaction.toInstitutionName);
        map.put("fromInstitutionName", transaction.fromInstitutionName);
        map.put("configVersion", transaction.configVersion);
        map.put("sdkVersion", transaction.sdkVersion);
        map.put("reqTimestamp", transaction.reqTimestamp);
        map.put("updatedTimestamp", transaction.updatedTimestamp);



        return map;
    }

}

