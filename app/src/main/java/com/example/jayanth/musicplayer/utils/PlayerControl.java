//package com.example.jayanth.musicplayer.utils;
//
//import android.widget.MediaController;
//
//import com.google.android.exoplayer2.ExoPlayer;
//import com.google.android.exoplayer2.SimpleExoPlayer;
//
///**
// * Created by jayanth on 21/12/17.
// */
//
//public class PlayerControl implements MediaController.MediaPlayerControl {
//
//    private final SimpleExoPlayer exoPlayer;
//
//    public PlayerControl(SimpleExoPlayer exoPlayer) {
//        this.exoPlayer = exoPlayer;
//    }
//
//    @Override
//    public boolean canPause() {
//        return true;
//    }
//
//    @Override
//    public boolean canSeekBackward() {
//        return true;
//    }
//
//    @Override
//    public boolean canSeekForward() {
//        return true;
//    }
//
//    @Override
//    public int getAudioSessionId() {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public int getBufferPercentage() {
//        return exoPlayer.getBufferedPercentage();
//    }
//
//    @Override
//    public int getCurrentPosition() {
//        return exoPlayer.getDuration() == ExoPlayer.UNKNOWN_TIME ? 0
//                : (int) exoPlayer.getCurrentPosition();
//    }
//
//    @Override
//    public int getDuration() {
//        return exoPlayer.getDuration() == ExoPlayer.UNKNOWN_TIME? 0
//                : (int) exoPlayer.getDuration();
//    }
//
//    @Override
//    public boolean isPlaying() {
//        return exoPlayer.getPlayWhenReady();
//    }
//
//    @Override
//    public void start() {
//        exoPlayer.setPlayWhenReady(true);
//    }
//
//    @Override
//    public void pause() {
//        exoPlayer.setPlayWhenReady(false);
//    }
//
//    @Override
//    public void seekTo(int timeMillis) {
//        long seekPosition = exoPlayer.getDuration() == ExoPlayer.UNKNOWN_TIME ? 0
//                : Math.min(Math.max(0, timeMillis), getDuration());
//        exoPlayer.seekTo(seekPosition);
//    }
//
//}
