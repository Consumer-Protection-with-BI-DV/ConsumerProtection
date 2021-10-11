package com.jachdev.consumerprotection.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Address implements Parcelable {

    private String line1;
    private String line2;
    private String line3;
    private String postalCode;
    private String country;
    private double latitude;
    private double longitude;

    public Address() {
    }

    public String getLine1() {
        return line1 != null? line1 : "";
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2 != null? line2 : "";
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getLine3() {
        return line3 != null? line3 : "";
    }

    public void setLine3(String line3) {
        this.line3 = line3;
    }

    public String getPostalCode() {
        return postalCode != null? postalCode : "";
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country != null? country : "";
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Address{" +
                "line1='" + line1 + '\'' +
                ", line2='" + line2 + '\'' +
                ", line3='" + line3 + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", country='" + country + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    public boolean compareTo(Address address){
        return (address != null &&
                !(getCountry().equals(address.getCountry())
                && getPostalCode().equals(address.getPostalCode())
                && getLine1().equals(address.getLine1())
                && getLine2().equals(address.getLine2())
                && getLine3().equals(address.getLine3())));
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.line1);
        dest.writeString(this.line2);
        dest.writeString(this.line3);
        dest.writeString(this.postalCode);
        dest.writeString(this.country);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
    }

    protected Address(Parcel in) {
        this.line1 = in.readString();
        this.line2 = in.readString();
        this.line3 = in.readString();
        this.postalCode = in.readString();
        this.country = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
    }

    public static final Creator<Address> CREATOR = new Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel source) {
            return new Address(source);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };

    public String getLongAddress() {
        return (getLine1().isEmpty() ? "" : line1 + ", ") + (getLine2().isEmpty() ? "" : line2 + ", ")
                + (getLine3().isEmpty() ? "" : line3 + ", ") + (getCountry().isEmpty() ? "" : country + ".")
                + (getPostalCode().isEmpty() ? "" : "[" + postalCode + "]");
    }
}
