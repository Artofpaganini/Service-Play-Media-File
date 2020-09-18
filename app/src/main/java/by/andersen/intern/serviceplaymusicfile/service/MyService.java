package by.andersen.intern.serviceplaymusicfile.service;

import android.app.Service;
import android.content.Intent;

import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import by.andersen.intern.serviceplaymusicfile.R;


public class MyService extends Service {

    private static final String TAG = "MyService";

    private MediaPlayer mediaPlayer;
    private IBinder binder = new MyServiceBinder();
    private int pausePosition;

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
    public void onCreate() {
        super.onCreate();

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.media_file);
        }
        playMusic();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                mediaPlayer.seekTo(0);
                onDestroy();

                Log.d(TAG, "onCompletion: FINISH media file ");
            }

        });
        Log.d(TAG, "onCreate: service on created");

    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mediaPlayer == null) {
            onCreate();
        }
        playMusic();
        Log.d(TAG, "onStartCommand: service started");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopMusic();


        Log.d(TAG, "onDestroy: service destroyed");
    }

    public void playMusic() {
        if (mediaPlayer != null && getPausePosition() != 0) {
            mediaPlayer.seekTo(getPausePosition());
        } else if (isMusicStopped) {
            mediaPlayer.start();
            isMusicStopped = false;

        }
        Log.d(TAG, "playMusic: PLAY");
    }

    public void pauseMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            isMusicStopped = true;
        }
        Log.d(TAG, "pauseMusic: PAUSE");
    }

    public void stopMusic() {

        mediaPlayer.stop();
        isMusicStopped = true;
        mediaPlayer = null;

        Log.d(TAG, "stopMusic: STOP");
    }


    public int getPausePosition() {
        return pausePosition;
    }
}
