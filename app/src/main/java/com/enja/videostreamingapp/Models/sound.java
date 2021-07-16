package com.enja.videostreamingapp.Models;

public class sound {
    public int id;
    public String sound_name;
    public String description;
    public String thum;
    public String section;
    public String _id;
    public String created;
    public audio_path audio_path;

    public sound(int id, String sound_name, String description, String thum, String section, String _id, String created, com.enja.videostreamingapp.Models.audio_path audio_path) {
        this.id = id;
        this.sound_name = sound_name;
        this.description = description;
        this.thum = thum;
        this.section = section;
        this._id = _id;
        this.created = created;
        this.audio_path = audio_path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSound_name() {
        return sound_name;
    }

    public void setSound_name(String sound_name) {
        this.sound_name = sound_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThum() {
        return thum;
    }

    public void setThum(String thum) {
        this.thum = thum;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public com.enja.videostreamingapp.Models.audio_path getAudio_path() {
        return audio_path;
    }

    public void setAudio_path(com.enja.videostreamingapp.Models.audio_path audio_path) {
        this.audio_path = audio_path;
    }
}
