package com.enja.videostreamingapp.Models;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class single_msg implements Serializable {
    @Expose
    public String video;

    public single_msg(String video) {
        this.video = video;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
}