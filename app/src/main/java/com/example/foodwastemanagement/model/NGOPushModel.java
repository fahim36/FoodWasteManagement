package com.example.foodwastemanagement.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NGOPushModel {
    @SerializedName("locationtext")
    @Expose
    private String locationtext;
    @SerializedName("fooddesc")
    @Expose
    private String fooddesc;
    @SerializedName("pickupid")
    @Expose
    private String pickupid;
    @SerializedName("phoneno")
    @Expose
    private String phoneno;

    public String getLocationtext() {
        return locationtext;
    }

    public void setLocationtext(String locationtext) {
        this.locationtext = locationtext;
    }

    public String getFooddesc() {
        return fooddesc;
    }

    public void setFooddesc(String fooddesc) {
        this.fooddesc = fooddesc;
    }

    public String getPickupid() {
        return pickupid;
    }

    public void setPickupid(String pickupid) {
        this.pickupid = pickupid;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }
}
