package com.jachdev.consumerprotection.data;

/**
 * Created by Charitha Ratnayake(charitha.r@eyepax.com) on 10/13/2021.
 */
public class FirebasePredictionData {

    private String name;
    private String dataType;
    private int max;
    private int min;
    private double rate;
    private String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "FirebasePredictionData{" +
                "name='" + name + '\'' +
                ", dataType='" + dataType + '\'' +
                ", max=" + max +
                ", min=" + min +
                ", rate=" + rate +
                ", type='" + type + '\'' +
                '}';
    }
}
