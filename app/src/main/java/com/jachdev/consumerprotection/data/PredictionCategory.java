package com.jachdev.consumerprotection.data;

import com.jachdev.consumerprotection.data.enums.PredictionType;

import java.util.List;

/**
 * Created by Asanka on 10/11/2021.
 */
public class PredictionCategory {

    private String name;
    private List<SubCategory> subs;

    public PredictionCategory() {
    }

    public PredictionCategory(String name, List<SubCategory> subs) {
        this.name = name;
        this.subs = subs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SubCategory> getSubCategory() {
        return subs;
    }

    public void setSubCategory(List<SubCategory> subs) {
        this.subs = subs;
    }

    public boolean hasSubCategory() {
        return subs != null && !subs.isEmpty();
    }

    public static class SubCategory {

        public SubCategory(String name) {
            this.name = name;
        }

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
