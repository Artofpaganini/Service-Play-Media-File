package by.andersen.intern.serviceplaymusicfile.service;

import android.app.Service;
import android.content.Intent;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;

public class MyService extends Service implements Serializable {

    private static final String TAG = "MyService";

    private MediaPlayer mediaPlayer;
    private IBinder binder = new MyServiceBinder();
    private boolean isMusicStopped = true;


    public class MyServiceBinder extends Binder {

        @NonNull
        public MyService getService() {
            return MyService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        if (binder == null) {
            binder = new MyServiceBinder();
        }

        Log.d(TAG, "onBind: BIND");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: UNBIND");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d(TAG, "onRebind: REBIND");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        playMusic(intent);
        return START_REDELIVER_INTENT;
    }

    public void playMusic(@NonNull Intent intent) {
        Log.d(TAG, "playMusic: TRY TO PLAY");

        Uri uri = Uri.parse(intent.getStringExtra("uriSong"));

        if (mediaPlayer == null) {

            mediaPlayer = MediaPlayer.create(this, uri);

            Log.d(TAG, "playMusic: PLAYER NULL");
        }
        if (mediaPlayer != null) {

            mediaPlayer.stop();
            mediaPlayer = MediaPlayer.create(this, uri);

            Log.d(TAG, "playMusic: PLAYER NOT NULL");
        }


        mediaPlayer.start();

        mediaPlayer.setOnCompletionListener(mediaPlayer -> {

            mediaPlayer.seekTo(0);
            onDestroy();

            Log.d(TAG, "onCompletion: FINISH media file ");
        });
        Log.d(TAG, "onCreate: service on STARTED");

    }

    public void stopMusic() {

        mediaPlayer.stop();
        isMusicStopped = true;
        mediaPlayer = null;

        Log.d(TAG, "stopMusic: STOP");
    }

    @Override
    public void onDestroy() {
        stopMusic();

        Log.d(TAG, "onDestroy: service destroyed");
    }






}
