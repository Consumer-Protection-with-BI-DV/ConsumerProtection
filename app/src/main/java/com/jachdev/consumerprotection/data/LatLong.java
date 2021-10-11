package com.jachdev.consumerprotection.data;

/**
 * Created by Charitha Ratnayake(charitha.r@eyepax.com) on 9/29/2021.
 */
public class LatLong {

    private double lat;
    private double lon;

    public LatLong(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}
