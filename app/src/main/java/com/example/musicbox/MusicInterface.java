package com.example.musicbox;

public interface MusicInterface {
    void pause();
    void play();
    void seekTo(int progress);
    void next();
    void last();
    boolean isPlaying();

}
