package com.example.musicbox;

import android.annotation.SuppressLint;
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
    private Button playPauseButton;
    //private Button pauseButton;
    private Button exitButton;
    private Button nextButton;
    private Button lastButton;

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
        playPauseButton = (Button) findViewById(R.id.play_or_pause);
        //pauseButton = (Button) findViewById(R.id.pause);
        exitButton = (Button) findViewById(R.id.exit);
        nextButton = (Button) findViewById(R.id.next);
        lastButton = (Button) findViewById(R.id.last);

        seekBar = (SeekBar) findViewById(R.id.sb);

        progressTextView = (TextView) findViewById(R.id.tv_progress);
        durationTextView = (TextView) findViewById(R.id.tv_duration);
        positionTextView1 = (TextView) findViewById(R.id.tv_position1);
        positionTextView2 = (TextView) findViewById(R.id.tv_position2);

        conn = new MusicServiceConn();
        playPauseButton.setOnClickListener(this);
        //pauseButton.setOnClickListener(this);
        exitButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        lastButton.setOnClickListener(this);

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

    public static String showTime(int time) {
        int minute = time / 1000 / 60;
        int second = time / 1000 % 60;

        String strMinute = numToString(minute);
        String strSecond = numToString(second);

        return strMinute + ":" + strSecond;
    }

    public static String numToString(int num) {
        if(num < 10) {
            return "0" + num;
        }
        else {
            return num + "";
        }
    }

    @SuppressLint("HandlerLeak")
    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            int currentPosition = data.getInt("currentPosition");
            int duration = data.getInt("duration");
            seekBar.setMax(duration);
            seekBar.setProgress(currentPosition);

            //Show Total Length of Music
            durationTextView.setText(showTime(duration));

            //Show Current Position
            progressTextView.setText(showTime(currentPosition));
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_or_pause:
                if(mi.isPlaying()) {
                    mi.pause();
                }
                else {
                    mi.play();
                }
                break;
//            case R.id.pause:
//                mi.pause();
//                break;
            case R.id.next:
                mi.next();
                break;
            case R.id.last:
                mi.last();
                break;
            case R.id.exit:
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
