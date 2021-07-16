package com.enja.videostreamingapp.Models;

public class count {
    public int like_count;
    public int video_comment_count;
    public int view;
    public String _id;

    public count(int like_count, int video_comment_count, int view, String _id) {
        this.like_count = like_count;
        this.video_comment_count = video_comment_count;
        this.view = view;
        this._id = _id;
    }

    public int getLike_count() {
        return like_count;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }

    public int getVideo_comment_count() {
        return video_comment_count;
    }

    public void setVideo_comment_count(int video_comment_count) {
        this.video_comment_count = video_comment_count;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
