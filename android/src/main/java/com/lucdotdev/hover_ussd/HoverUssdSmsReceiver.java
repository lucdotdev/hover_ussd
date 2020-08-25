package com.lucdotdev.hover_ussd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public final class HoverUssdSmsReceiver  extends BroadcastReceiver{
    private  HoverUssdReceiverInterface hoverUssdReceiverInterface;

    public HoverUssdSmsReceiver(HoverUssdReceiverInterface hoverUssdReceiverInterface){
        this.hoverUssdReceiverInterface = hoverUssdReceiverInterface;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String t = intent.getStringExtra("status");
        assert t != null;
        hoverUssdReceiverInterface.onRecevedData(t.toLowerCase());
    }

    public interface HoverUssdReceiverInterface{
        void onRecevedData(String msg) ;
    }
}
