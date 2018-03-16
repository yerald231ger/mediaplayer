package com.terra.player.exoplayer;

import android.content.Context;
import android.net.Uri;
import android.view.SurfaceView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

/**
 * Created by Gerardo.Sanchez on 13/03/2018.
 */

public class VideoHandler implements Player.EventListener {
    private static VideoHandler instance;
    private Uri mVideoUri;
    private ExtractorMediaSource mVideoSource;
    private Context mContext;
    private SurfaceView mPlayerView;
    private SimpleExoPlayer player;
    private boolean wasPlaying;
    private VideoHandler.EventListener eventListener;

    public static VideoHandler getInstance() {
        if (instance == null) {
            instance = new VideoHandler();
        }
        return instance;
    }

    private VideoHandler() {
    }

    public void setPlayerForUri(Context context, Uri uri, SurfaceView playerView, VideoHandler.EventListener eventListener) {
        if (context != null && uri != null) {
            mContext = context;
            addListener(eventListener);
            if (player == null) {
                mPlayerView = playerView;
                mVideoUri = uri;

                DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                TrackSelection.Factory videoTrackSelectionFactory =
                        new AdaptiveTrackSelection.Factory(bandwidthMeter);
                TrackSelector trackSelector =
                        new DefaultTrackSelector(videoTrackSelectionFactory);

                // 2. Create the player
                player =
                        ExoPlayerFactory.newSimpleInstance(context, trackSelector);
                player.addListener(this);

                // Bind the player to the view.
                player.getVideoComponent().setVideoSurfaceView(mPlayerView);

                // Produces DataSource instances through which media data is loaded.
                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "yourApplicationName"), bandwidthMeter);

                // This is the MediaSource representing the media to be played.
                mVideoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(mVideoUri);
                player.prepare(mVideoSource);
            } else {
                //is in full screen mode
                player.getVideoComponent().clearVideoSurfaceView(mPlayerView);
                player.getVideoComponent().setVideoSurfaceView(playerView);
                player.seekTo(player.getCurrentPosition() + 1);
            }

        }
    }

    public void Start() {
        player.setPlayWhenReady(true);
    }

    public void Pause() {
        player.setPlayWhenReady(false);
    }

    public void Forward() {
        long nextPosition = (player.getCurrentPosition() + 15000);

        if (nextPosition >= player.getDuration())
            player.seekTo(player.getDuration());
        else
            player.seekTo(nextPosition);
    }

    public void Rewind() {
        long previewPosition = (player.getCurrentPosition() - 15000);

        if (previewPosition <= 0)
            player.seekToDefaultPosition();
        else
            player.seekTo(previewPosition);
    }

    public void releaseVideoPlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    public boolean isPlaying() {
        return player.getPlayWhenReady();
    }

    public void goToBackground() {
        if (player != null) {
            wasPlaying = player.getPlayWhenReady();
            player.setPlayWhenReady(false);
        }
    }

    public void goToForeground() {
        if (player != null) {
            player.setPlayWhenReady(wasPlaying);
        }
    }

    public void addListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    //region events

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    /**
     * Called when the player starts or stops loading the source.
     */
    @Override
    public void onLoadingChanged(boolean isLoading) {
        eventListener.onLoadingChange(isLoading);
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        eventListener.onPlayerStateChanged(playWhenReady, playbackState);
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }
    //endregion events

    public interface EventListener {
        void onLoadingChange(boolean isLoading);

        void onPlayerStateChanged(boolean playWhenReady, int playbackState);
    }
}