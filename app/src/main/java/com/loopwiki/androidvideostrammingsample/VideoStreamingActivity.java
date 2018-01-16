package com.loopwiki.androidvideostrammingsample;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader;
import com.google.android.exoplayer2.ext.ima.ImaAdsMediaSource;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class VideoStreamingActivity extends AppCompatActivity {
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    // SimpleExoPlayer player;
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_streaming);
        mContentView = findViewById(R.id.fullscreen_content);
        mProgressBar = findViewById(R.id.progressBar);
        //initialize Player
        initializePlayer();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(100);
    }


    private void hide() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }


    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private MediaSource createMediaSource(String videoUrl) {
        //Getting UserAgent
        String UserAgent = Util.getUserAgent(this, getString(R.string.app_name));

        //Creating Media Source
        MediaSource contentMediaSource = new ExtractorMediaSource(Uri.parse(videoUrl),
                new DefaultHttpDataSourceFactory(UserAgent),
                new DefaultExtractorsFactory(),
                null, null);
        //return media source
        return contentMediaSource;
    }

    private ImaAdsMediaSource createMediaSourceWithAds(String videoUrl, SimpleExoPlayerView exoPlayerView) {
        //Getting UserAgent
        String UserAgent = Util.getUserAgent(this, getString(R.string.app_name));
        // Creating  Video Content Media Source
        MediaSource contentMediaSource = new ExtractorMediaSource(Uri.parse(videoUrl),
                new DefaultHttpDataSourceFactory(UserAgent),
                new DefaultExtractorsFactory(), null, null);
        //Creating  Ima Ads Loader
        ImaAdsLoader imaAdsLoader = new ImaAdsLoader(this, Uri.parse(getResources().getString(R.string.ad_tag_url)));

        //Creating Video Content Media Source With Ads
        ImaAdsMediaSource contentMediaSourceWithAds = new ImaAdsMediaSource(
                contentMediaSource,//Video Content Media Source
                new DefaultDataSourceFactory(this, UserAgent),
                imaAdsLoader,
                exoPlayerView.getOverlayFrameLayout());//Overlay During Ads Playback
        //return media source with ads
        return contentMediaSourceWithAds;
    }

    //Define Field for SimpleExoPlayer
    SimpleExoPlayer player;

    private void initializePlayer() {
        //Biding xml view to exoPlayerView object
        SimpleExoPlayerView exoPlayerView = findViewById(R.id.exoPlayerView);
        //Creating Load Control
        LoadControl loadControl = new DefaultLoadControl(
                new DefaultAllocator(true, 16),
                VideoPlayerConfig.MIN_BUFFER_DURATION,
                VideoPlayerConfig.MAX_BUFFER_DURATION,
                VideoPlayerConfig.MIN_PLAYBACK_START_BUFFER,
                VideoPlayerConfig.MIN_PLAYBACK_RESUME_BUFFER);

        //Initializing ExoPlayer
        player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(this),
                new DefaultTrackSelector(),
                loadControl);
        //binding exoPlayerView to SimpleExoPlayer
        exoPlayerView.setPlayer(player);
        //preparing player with media Source
//        player.prepare(createMediaSource(VideoPlayerConfig.VIDEO_URL));
        //Uncomment following line remove above line if you want to play Ads between Video
        player.prepare(createMediaSourceWithAds(VideoPlayerConfig.VIDEO_URL,exoPlayerView));
        //adding Listener to SimpleExoPlayer
        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {
                    case Player.STATE_BUFFERING:
                        //Player is in state State buffering show some loading progress
                        showProgress();
                        break;
                    case Player.STATE_READY:
                        //Player is ready to Play. Remove loading progress
                        hideProgress();
                        break;
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
            }

            @Override
            public void onPositionDiscontinuity() {
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            }
        });

    }

    private void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        mProgressBar.setVisibility(View.GONE);
    }


    /*   Activity Life Cycle*/
    @Override
    protected void onResume() {
        player.setPlayWhenReady(true);
        super.onResume();
    }

    @Override
    protected void onPause() {
        player.setPlayWhenReady(false);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        player.release();
        super.onDestroy();
    }
}
