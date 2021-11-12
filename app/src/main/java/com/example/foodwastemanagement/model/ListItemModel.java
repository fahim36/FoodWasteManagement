package com.example.foodwastemanagement.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ListItemModel {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("phoneno")
    @Expose
    private String phoneno;
    @SerializedName("pickupid")
    @Expose
    private String pickupid;
    @SerializedName("fooddesc")
    @Expose
    private String fooddesc;
    @SerializedName("itemdetails")
    @Expose
    private String itemdetails;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("pickupstatus")
    @Expose
    private String pickupstatus;

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

    public String getPickupid() {
        return pickupid;
    }

    public void setPickupid(String pickupid) {
        this.pickupid = pickupid;
    }

    public String getFooddesc() {
        return fooddesc;
    }

    public void setFooddesc(String fooddesc) {
        this.fooddesc = fooddesc;
    }

    public String getItemdetails() {
        return itemdetails;
    }

    public void setItemdetails(String itemdetails) {
        this.itemdetails = itemdetails;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPickupstatus() {
        return pickupstatus;
    }

    public void setPickupstatus(String pickupstatus) {
        this.pickupstatus = pickupstatus;
    }
}
