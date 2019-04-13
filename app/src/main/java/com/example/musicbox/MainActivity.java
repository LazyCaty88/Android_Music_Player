package com.example.musicbox;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button playButton;
    private Button pauseButton;
    private Button exitButton;

    private MusicInterface mi;
    private MusicServiceConn conn;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playButton = (Button) findViewById(R.id.play_service);
        pauseButton = (Button) findViewById(R.id.pause_service);
        exitButton = (Button) findViewById(R.id.exit_service);
        conn = new MusicServiceConn();
        playButton.setOnClickListener(this);
        pauseButton.setOnClickListener(this);
        exitButton.setOnClickListener(this);

        //Initialization: bind service and call init();
        intent = new Intent(MainActivity.this, MusicService.class);
        startService(intent);
        bindService(intent, conn, BIND_AUTO_CREATE);
        //mi.init();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_service:
                mi.play();
                break;
            case R.id.pause_service:
                mi.pause();
                break;
//            case R.id.init_service:
//                mi.init();
//                break;
            case R.id.exit_service:
                unbindService(conn);
                stopService(intent);
                finish();
                break;
            default:
                break;
        }
    }


    class MusicServiceConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mi = (MusicInterface) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }
}