package com.jachdev.consumerprotection.data;

import com.google.gson.annotations.SerializedName;
import com.jachdev.consumerprotection.data.enums.UserType;

/**
 * Created by Charitha Ratnayake(charitha.r@eyepax.com) on 9/20/2021.
 */
public class User {

    private long id;
    @SerializedName("user_type")
    private int type;
    @SerializedName("full_name")
    private String name;
    @SerializedName("email")
    private String email;
    @SerializedName("phone_number")
    private String number;
    @SerializedName("password")
    private String password;
    @SerializedName("birth_day")
    private long birthDay;
    @SerializedName("address")
    private long address;
    @SerializedName("gender")
    private int gender;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(long birthDay) {
        this.birthDay = birthDay;
    }

    public long getAddress() {
        return address;
    }

    public void setAddress(long address) {
        this.address = address;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public UserType getUserType(){
        for (UserType type: UserType.values()) {
            if (type.id == this.type) {
                return type;
            }
        }
        return UserType.CONSUMER;
    }

    @Override
    public String toString() {
        return "User{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", birthDay=" + birthDay +
                ", address=" + address +
                ", gender=" + gender +
                '}';
    }
}
