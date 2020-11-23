package com.spy.spydemo;

import android.content.Context;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AudioRecorder {
    private Context context;

    public AudioRecorder(Context context) {
        this.context = context;
    }

    void recordAudio(String file, final int time) {
        final MediaRecorder recorder = new MediaRecorder();

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(file);

        try {
            recorder.prepare();
        } catch (IOException e) {}

        recorder.start();

        Thread timer = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(time * 1000);
                } catch (InterruptedException e) {
                    Log.d("TAG", "timer interrupted");
                } finally {
                    recorder.stop();
                    recorder.release();
                }
            }
        });

        timer.start();
    }

    //Данный код сделает 15-секундную запись и поместит ее в файл audio-ДАТА-И-ВРЕМЯ.3gp.
    public void saveAudio(){
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US);
        Date date = new Date();

        String filePrefix = context.getApplicationInfo().dataDir + "/audio-";

        recordAudio(filePrefix + formatter.format(date) + ".3gp", 15);
    }
}
