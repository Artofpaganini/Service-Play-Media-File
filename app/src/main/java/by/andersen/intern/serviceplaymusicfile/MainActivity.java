package by.andersen.intern.serviceplaymusicfile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import by.andersen.intern.serviceplaymusicfile.service.MyService;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private boolean isServiceBound;
    private Intent serviceIntent;
    private MyService myService;

    private TextView name;
    private TextView singer;
    private TextView styleOfMusic;

    private Button play;
    private Button pause;
    private Button stop;

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

            MyService.MyServiceBinder binder = (MyService.MyServiceBinder) service;
            myService = binder.getService();
            isServiceBound = true;
            Log.d(TAG, "onServiceConnected: BINDER connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isServiceBound = false;
            Log.d(TAG, "onServiceDisconnected: BINDER disconnected");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.music_name);
        singer = findViewById(R.id.music_singer);
        styleOfMusic = findViewById(R.id.music_style);

        play = findViewById(R.id.play);
        pause = findViewById(R.id.pause);
        stop = findViewById(R.id.stop);

        serviceIntent = new Intent(this, MyService.class);

        play.setOnClickListener(view -> {
            bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
            play();


        });

        pause.setOnClickListener(view -> {
            pause();
        });

        stop.setOnClickListener(view -> stop());

    }

    private void play() {
        if (isServiceBound) {
            startService(serviceIntent);

        }

        Log.d(TAG, "play: LOAD and START and SAVE");
    }

    private void pause() {
        if (isServiceBound) {
            myService.pauseMusic();

        }
        Log.d(TAG, "pause: SAVE DATA and PAUSE");
    }

    private void stop() {
        if (isServiceBound) {
            unbindService(serviceConnection);
            stopService(serviceIntent);
            isServiceBound = false;

            Log.d(TAG, "stop: SAVE  DATA and STOP");
        }
    }
}

