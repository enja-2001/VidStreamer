package com.enja.videostreamingapp.Models;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class CustomOutput {

    @Expose
    public String s;
    @Expose
    public String code;
    @Expose
    public ArrayList<single_msg> msg;

    public CustomOutput(String s, String code, ArrayList<single_msg> msg) {
        this.s = s;
        this.code = code;
        this.msg = msg;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ArrayList<single_msg> getMsg() {
        return msg;
    }

    public void setMsg(ArrayList<single_msg> msg) {
        this.msg = msg;
    }
}