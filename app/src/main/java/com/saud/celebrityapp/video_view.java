
package com.saud.celebrityapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

public class video_view extends AppCompatActivity{
    private ProgressBar progressBar;
    MediaController mediaController;
    VideoView videoView;
    private int mCurrentPosition = 0;
    private static final String PLAYBACK_TIME = "play_time";
    Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);
        progressBar=findViewById(R.id.progressBar2);
        videoView =  findViewById(R.id.videoView);

        Intent intent=getIntent();
        if(intent.getStringExtra("uri")!=null){
            uri=Uri.parse(intent.getStringExtra("uri"));

            progressBar.setVisibility(View.VISIBLE);
            if (savedInstanceState != null) {
                mCurrentPosition = savedInstanceState.getInt(PLAYBACK_TIME);
            }

            mediaController = new MediaController(this);
            mediaController.setMediaPlayer(videoView);
            videoView.setMediaController(mediaController);
        }
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                if (videoView.isPlaying()) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 0);
    }


    @Override
    protected void onStart() {
        super.onStart();
        initializePlayer(uri);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            videoView.pause();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(PLAYBACK_TIME, videoView.getCurrentPosition());
    }
    private void initializePlayer(Uri videoUri) {
        videoView.setVideoURI(videoUri);

        videoView.setOnPreparedListener(
                new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        if (mCurrentPosition > 0) {
                            videoView.seekTo(mCurrentPosition);
                        } else {
                            videoView.seekTo(1);
                        }

                        videoView.start();
                    }
                });
        videoView.setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        videoView.seekTo(0);
                        finish();
                    }
                });
    }
    private void releasePlayer() {
        videoView.stopPlayback();
    }

}