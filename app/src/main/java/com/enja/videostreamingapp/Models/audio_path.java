package com.enja.videostreamingapp.Models;

public class audio_path {
    public String mp3;
    public String acc;

    public audio_path(String mp3, String acc) {
        this.mp3 = mp3;
        this.acc = acc;
    }

    public String getMp3() {
        return mp3;
    }

    public void setMp3(String mp3) {
        this.mp3 = mp3;
    }

    public String getAcc() {
        return acc;
    }

    public void setAcc(String acc) {
        this.acc = acc;
    }
}
