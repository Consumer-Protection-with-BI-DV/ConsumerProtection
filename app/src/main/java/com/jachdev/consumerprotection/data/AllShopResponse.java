package com.jachdev.consumerprotection.data;

import com.google.gson.annotations.SerializedName;
import com.jachdev.consumerprotection.AppConstant;

/**
 * Created by Asanka on 10/4/2021.
 */
public class AllShopResponse extends Shop{

    @SerializedName("org_name")
    private String orgName;
    @SerializedName("logo")
    private String logo;
    @SerializedName("description")
    private String description;
    private String distance;

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getLogo() {
        return AppConstant.BASE_URL + AppConstant.IMAGE_PATH + logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
