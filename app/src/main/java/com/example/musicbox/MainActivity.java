package com.example.musicbox;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button playButton;
    private Button pauseButton;
    private Button exitButton;

    private static SeekBar seekBar;
    private static TextView progressTextView;
    private static TextView durationTextView;
    private TextView positionTextView1;
    private TextView positionTextView2;

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
        seekBar = (SeekBar) findViewById(R.id.sb);

        progressTextView = (TextView) findViewById(R.id.tv_progress);
        durationTextView = (TextView) findViewById(R.id.tv_duration);
        positionTextView1 = (TextView) findViewById(R.id.tv_position1);
        positionTextView2 = (TextView) findViewById(R.id.tv_position2);

        conn = new MusicServiceConn();
        playButton.setOnClickListener(this);
        pauseButton.setOnClickListener(this);
        exitButton.setOnClickListener(this);

        //Initialization: bind service and call init();
        intent = new Intent(MainActivity.this, MusicService.class);
        startService(intent);
        bindService(intent, conn, BIND_AUTO_CREATE);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                positionTextView1.setText("The current position of seekBar：" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                positionTextView2.setText("The current progress of seekBar：" + progress);
                mi.seekTo(progress);
            }
        });


    }

    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            int currentPosition = data.getInt("currentPosition");
            int duration = data.getInt("duration");
            seekBar.setMax(duration);
            seekBar.setProgress(currentPosition);
            String strMinute = null;
            String strSecond = null;


            int minute = duration / 1000 / 60;
            int second = duration / 1000 % 60;

            if(minute < 10) {


                strMinute = "0" + minute;
            } else {

                strMinute = minute + "";
            }

            if(second < 10)
            {
                strSecond = "0" + second;
            } else {

                strSecond = second + "";
            }

            durationTextView.setText(strMinute + ":" + strSecond);

            //Show Current Position
            minute = currentPosition / 1000 / 60;
            second = currentPosition / 1000 % 60;

            if(minute < 10) {

                strMinute = "0" + minute;
            } else {

                strMinute = minute + "";
            }

            if(second < 10) {

                strSecond = "0" + second;
            } else {

                strSecond = second + "";
            }

            progressTextView.setText(strMinute + ":" + strSecond);
        }
    };


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
