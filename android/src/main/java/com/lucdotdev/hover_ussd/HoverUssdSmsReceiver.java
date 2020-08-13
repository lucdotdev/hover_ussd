package com.lucdotdev.hover_ussd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


import io.flutter.plugin.common.EventChannel;

public final class HoverUssdSmsReceiver  extends BroadcastReceiver{
    private  HoverUssdReceiverInterface hoverUssdReceiverInterface;

    public HoverUssdSmsReceiver(HoverUssdReceiverInterface hoverUssdReceiverInterface){
        this.hoverUssdReceiverInterface = hoverUssdReceiverInterface;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        hoverUssdReceiverInterface.onRecevedData(intent.getStringExtra("status"));
    }
    public interface HoverUssdReceiverInterface{
        void onRecevedData(String msg) ;
    }
}
