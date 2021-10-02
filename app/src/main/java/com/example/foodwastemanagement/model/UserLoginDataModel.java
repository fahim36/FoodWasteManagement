package com.example.foodwastemanagement.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserLoginDataModel {
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("phoneno")
    @Expose
    private String phoneno;
    @SerializedName("userid")
    @Expose
    private String userid;
    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("usertoken")
    @Expose
    private String usertoken;

    public String getUsertoken() {
        return usertoken;
    }

    public void setUsertoken(String usertoken) {
        this.usertoken = usertoken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
