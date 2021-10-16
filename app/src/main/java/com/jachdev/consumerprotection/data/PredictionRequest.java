package com.jachdev.consumerprotection.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Asanka on 10/3/2021.
 */
public class PredictionRequest {

    private String path = "import_dataset";
    private String category = "";
    @SerializedName("sub_category")
    private String subCategory = "";

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    @Override
    public String toString() {
        return "PredictionRequest{" +
                "path='" + path + '\'' +
                ", category='" + category + '\'' +
                ", subCategory='" + subCategory + '\'' +
                '}';
    }
}
