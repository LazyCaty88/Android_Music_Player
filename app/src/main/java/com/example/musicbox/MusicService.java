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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class MusicService extends Service {
    public static final int PLAY = 0;
    public static final int PAUSE = 1;
    public static final int PRE = 2;
    //private MusicControl musicControl;
    private MediaPlayer mediaPlayer;
    private Timer timer;
    private static final String PATH = "mnt/sdcard";
    private List<String> musicList;
    private int musicSeqNum; //Current playing music
    private int status;


    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        musicList = new ArrayList<>();
        File musicDir = new File(PATH, "Music");
        MusicNameFilter musicNameFilter = new MusicNameFilter();
        File[] fileArr = musicDir.listFiles(musicNameFilter);
        Log.d("Number of Songs", fileArr.length + "");
        if(fileArr.length > 0) {
            for (File file : fileArr) {
                Log.d("File Path", file.getAbsolutePath());
                musicList.add(file.getAbsolutePath());
            }
        }
        //isPlaying = FALSE;
        status = PRE;
        musicSeqNum = 0; //Initialize to play the first piece of music
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


    class MusicNameFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".mp3");
        }
    }
    class MusicControl extends Binder implements MusicInterface {


        @Override
        public boolean isPlaying() {
            if(status == PLAY) {
                return TRUE;
            }
            return FALSE;
        }

        @Override
        public void seekTo(int progress) {
            mediaPlayer.seekTo(progress);
        }

        @Override
        public void pause() {
            mediaPlayer.pause();
            status = PAUSE;
        }

        @Override
        public void play() {
            try {
                if(mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                }
                    //Uri uri = Uri.parse("android.resource://com.example.musicbox/"+R.raw.abc);
                if(status == PRE) {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(musicList.get(musicSeqNum));
                    mediaPlayer.prepare();
                    addTimer();
                    //isPlaying = TRUE;
                }

                //mediaPlayer.setDataSource(getApplicationContext(), uri);



                mediaPlayer.start();
                status = PLAY;
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        next();
                    }
                });
                //Log.d("Message", "Start playing music!");
            }catch (IOException e) {
                e.printStackTrace();
            }
//            MediaPlayer.create(getApplicationContext(), R.raw.abc);
//            Log.d("Message", "Start playing music!");
        }

        @Override
        public void next() {
            if(musicSeqNum == musicList.size() - 1) { //Now playing last piece of music
                //jump to the first one
                musicSeqNum = 0;
            }
            else {
                musicSeqNum++;
            }
            status = PRE;
            play();
        }

        @Override
        public void last() {
            if(musicSeqNum == 0) {//Now playing first piece of music
                //jump to the last one
                musicSeqNum = musicList.size() - 1;
            }
            else {
                musicSeqNum--;
            }
            status = PRE;
            play();
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
