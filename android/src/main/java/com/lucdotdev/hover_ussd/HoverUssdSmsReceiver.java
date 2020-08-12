package com.lucdotdev.hover_ussd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import io.flutter.plugin.common.EventChannel;

public class HoverUssdSmsReceiver  extends BroadcastReceiver{
    private EventChannel.EventSink events;

    public HoverUssdSmsReceiver(EventChannel.EventSink events) {
        this.events = events;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        events.success( intent.getStringExtra("status"));
    }

}
