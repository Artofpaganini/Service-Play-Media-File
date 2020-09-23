package by.andersen.intern.serviceplaymusicfile.activies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import by.andersen.intern.serviceplaymusicfile.broadcast.MyReceiver;
import by.andersen.intern.serviceplaymusicfile.R;
import by.andersen.intern.serviceplaymusicfile.service.MyService;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private boolean isServiceBound;
    private Intent serviceIntent;
    private MyService myService;

    public static final String SEND_MESSAGE = "set song";

    private TextView name;
    private TextView artist;
    private TextView styleOfMusic;

    private TextView musicName;
    private TextView musicArtist;
    private TextView musicStyle;

    private Button play;
    private Button stop;
    private Button chooseTheArtist;

    private MyReceiver myReceiver;
    private String songName;
    private String songArtist;
    private String songStyle;
    private String songUriAddress;


    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(@NonNull ComponentName className,
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

        name = findViewById(R.id.name);
        artist = findViewById(R.id.artist);
        styleOfMusic = findViewById(R.id.style);

        musicName = findViewById(R.id.music_name);
        musicArtist = findViewById(R.id.music_artist);
        musicStyle = findViewById(R.id.music_style);

        play = findViewById(R.id.play);
        stop = findViewById(R.id.stop);
        chooseTheArtist = findViewById(R.id.choose_artist);

        serviceIntent = new Intent(this, MyService.class);
        myReceiver = new MyReceiver();

        refillFieldsInfoAboutSong();

        sendMediaFileToService();

        play.setOnClickListener(view -> {
            bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
            play();
        });

        stop.setOnClickListener(view -> {
            stop();
        });

        chooseTheArtist.setOnClickListener(v -> MainActivity.this.openActivityFromBroadcast(v));

    }

    private void play() {

        startService(serviceIntent);

        Log.d(TAG, "play: START ");
    }



    private void stop() {
        if (isServiceBound) {
            unbindService(serviceConnection);
            stopService(serviceIntent);
            isServiceBound = false;

            Log.d(TAG, "stop: STOP");
        }
    }

    @NonNull
    public void openActivityFromBroadcast(View v) {
        Intent intent = new Intent("by.andersen.intern.contentprovider.MAKE_A_CHOISE");

        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> infos = packageManager.queryBroadcastReceivers(intent, 0);
        for (ResolveInfo info : infos) {
            ComponentName cn = new ComponentName(info.activityInfo.packageName,
                    info.activityInfo.name);
            intent.setComponent(cn);
            sendBroadcast(intent);

            Log.d(TAG, "openActivityFromBroadcast: SEND BROADCAST");
        }

    }

    @NonNull
    public void refillFieldsInfoAboutSong() {

        Intent intent = getIntent();
        songName = intent.getStringExtra("songName");
        songArtist = intent.getStringExtra("songArtist");
        songStyle = intent.getStringExtra("songStyle");
        songUriAddress = intent.getStringExtra("songUri");

        musicName.setText(songName);
        musicArtist.setText(songArtist);
        musicStyle.setText(songStyle);

        Log.d(TAG, "refillFieldsInfoAboutSong: DATA REFILL" + songName + " " + songUriAddress);
    }


    public void sendMediaFileToService() {

        serviceIntent.putExtra("uriSong", getSongUriAddress());

        Log.d(TAG, "sendMediaFileToService: SEND MEDIA FILE TO SERVICE");
    }

    @NonNull
    public String getSongUriAddress() {

        return songUriAddress;
    }

}

