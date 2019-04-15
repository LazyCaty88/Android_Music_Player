package com.example.musicbox;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service {

    //private MusicControl musicControl;
    private MediaPlayer mediaPlayer;
    private Timer timer;
    @Override
    public void onCreate() {
        super.onCreate();
        //mediaPlayer = new MediaPlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MusicControl();
    }

    class MusicControl extends Binder implements MusicInterface {

        @Override
        public void seekTo(int progress) {
            mediaPlayer.seekTo(progress);
        }

        @Override
        public void pause() {
            mediaPlayer.pause();
        }

        @Override
        public void play() {
            try {
                if(mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                    //Uri uri = Uri.parse("android.resource://com.example.musicbox/"+R.raw.abc);
                    mediaPlayer.reset();
                    //mediaPlayer.setDataSource(getApplicationContext(), uri);
                    mediaPlayer.setDataSource("/mnt/sdcard/Music/New York.mp3");
                    mediaPlayer.prepare();
                    addTimer();

                }

                mediaPlayer.start();
                addTimer();
                //Log.d("Message", "Start playing music!");
            }catch (IOException e) {
                e.printStackTrace();
            }
//            MediaPlayer.create(getApplicationContext(), R.raw.abc);
//            Log.d("Message", "Start playing music!");
        }

        public void addTimer() {
            if(timer == null) {
                timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        int currentPosition = mediaPlayer.getCurrentPosition();
                        int duration = mediaPlayer.getDuration();
                        Message msg = MainActivity.handler.obtainMessage();
                        Bundle data = new Bundle();
                        data.putInt("currentPosition", currentPosition);
                        data.putInt("duration", duration);
                        msg.setData(data);
                        //Send message to main thread
                        MainActivity.handler.sendMessage(msg);
                    }
                };
                timer.schedule(timerTask, 5, 500);
            }

        }
    }
}
