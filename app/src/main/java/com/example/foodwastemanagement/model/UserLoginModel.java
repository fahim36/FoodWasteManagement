package com.example.foodwastemanagement.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserLoginModel {
    @SerializedName("error")
    @Expose
    private String error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private UserLoginDataModel UserLoginDataModel;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserLoginDataModel getUserLoginDataModel() {
        return UserLoginDataModel;
    }

    public void setUserLoginDataModel(UserLoginDataModel UserLoginDataModel) {
        this.UserLoginDataModel = UserLoginDataModel;
    }


}
