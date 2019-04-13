package com.example.musicbox;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

public class MusicService extends Service {

    //private MusicControl musicControl;
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null; //Is this statement necessary?
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MusicControl();
    }

    class MusicControl extends Binder implements MusicInterface {

        @Override
        public void seekTo(int progress) {


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

                }
                Uri uri = Uri.parse("android.resource://com.example.musicbox/"+R.raw.abc);
                mediaPlayer.reset();
                mediaPlayer.setDataSource(getApplicationContext(), uri);
                mediaPlayer.prepare();
                mediaPlayer.start();
                Log.d("Message", "Start playing music!");
            }catch (IOException e) {
                e.printStackTrace();
            }
//            MediaPlayer.create(getApplicationContext(), R.raw.abc);
//            Log.d("Message", "Start playing music!");
        }
    }
}
