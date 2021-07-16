package com.enja.videostreamingapp.Models;

public class user_info {
    public String first_name;
    public String last_name;
    public String fb_id;
    public String profile_pic;
    public String gender;
    public String verified;
    public String _id;
    public String username;

    public user_info(String first_name, String last_name, String fb_id, String profile_pic, String gender, String verified, String _id, String username) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.fb_id = fb_id;
        this.profile_pic = profile_pic;
        this.gender = gender;
        this.verified = verified;
        this._id = _id;
        this.username = username;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getFb_id() {
        return fb_id;
    }

    public void setFb_id(String fb_id) {
        this.fb_id = fb_id;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
