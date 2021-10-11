package com.jachdev.consumerprotection.data;

import com.google.gson.annotations.SerializedName;
import com.jachdev.consumerprotection.AppConstant;

/**
 * Created by Charitha Ratnayake(charitha.r@eyepax.com) on 9/28/2021.
 */
public class Organization {

    private long id;
    @SerializedName("user_id")
    private long userId;
    private String logo;
    @SerializedName("shop_type_id")
    private long shopTypeId;
    private String name;
    private String description;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getLogo() {
        return AppConstant.BASE_URL + AppConstant.IMAGE_PATH + logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public long getShopTypeId() {
        return shopTypeId;
    }

    public void setShopTypeId(long shopTypeId) {
        this.shopTypeId = shopTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
