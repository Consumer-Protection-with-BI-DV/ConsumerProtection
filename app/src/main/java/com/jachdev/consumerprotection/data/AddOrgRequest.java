package com.jachdev.consumerprotection.data;

import com.google.gson.annotations.SerializedName;
import com.jachdev.consumerprotection.AppConstant;

import java.io.File;

/**
 * Created by Charitha Ratnayake(charitha.r@eyepax.com) on 9/28/2021.
 */
public class AddOrgRequest extends BaseRequest{

    @SerializedName("user_id")
    private long userId;
    private String logo;
    @SerializedName("shop_type_id")
    private long shopTypeId;
    private String name;
    private String description;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;

        setImageParts(AppConstant.ORG_LOGO_KEY, new File(logo));
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

    public void createDataPart(){
        setTextParts("user_id", String.valueOf(userId));
        setTextParts("shop_type_id", String.valueOf(shopTypeId));
        setTextParts("name", name);
        setTextParts("description", description);
    }
}
