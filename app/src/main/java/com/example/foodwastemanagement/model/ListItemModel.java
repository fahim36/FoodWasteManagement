package com.example.foodwastemanagement.model;


public class ListItemModel {

    private String itemDetails;
    private String longitude;
    private String latitude;
    private String name;
    private String fooddesc;
    private String pickupid;
    private String phoneno;
    private String pickupstatus;
    private String timestamp;

    public String getPickupStatus() {
        return pickupstatus;
    }

    public void setPickupStatus(String pickupStatus) {
        this.pickupstatus = pickupStatus;
    }


    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
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



    public String getLocationtext() {
        return itemDetails;
    }

    public void setLocationtext(String locationtext) {
        this.itemDetails = locationtext;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFooddesc() {
        return fooddesc;
    }

    public void setFooddesc(String fooddesc) {
        this.fooddesc = fooddesc;
    }
}
