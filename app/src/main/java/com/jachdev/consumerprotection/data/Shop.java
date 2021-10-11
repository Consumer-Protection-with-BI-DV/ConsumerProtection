package com.jachdev.consumerprotection.data;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Charitha Ratnayake(charitha.r@eyepax.com) on 9/29/2021.
 */
public class Shop {

    private long id;
    private String name;
    private String address;
    @SerializedName("phonenumber")
    private String phoneNumber;
    @SerializedName("organization_id")
    private long organizationId;
    private String location;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(long organizationId) {
        this.organizationId = organizationId;
    }

    public String getLocation() {
        return location;
    }

    public LatLong getLatLong() {
        return new Gson().fromJson(location, LatLong.class);
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setLocation(double lat, double lon) {
        this.location = new Gson().toJson(new LatLong(lat, lon));
    }
}
