package com.jachdev.consumerprotection.data;

import com.jachdev.consumerprotection.util.Helper;

import java.util.Calendar;

/**
 * Created by Asanka on 10/3/2021.
 */
public class PredictionData {

    private String date;
    private String importValue;
    private String priceValue;
    private String salesValue;

    public String getDate() {
        return date;
    }

    public int getMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(getUnix());
        return calendar.get(Calendar.MONTH);
    }

    public long getUnix() {
        return Helper.dateTimeToUnix(date);
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImportValue() {
        return importValue;
    }

    public Integer getValue() {
        if(importValue != null){

            return (int)Double.parseDouble(importValue);
        }else if(priceValue != null){

            return (int)Double.parseDouble(priceValue);
        }else if(salesValue != null){

            return (int)Double.parseDouble(salesValue);
        }
        return 0;
    }

    public void setImportValue(String importValue) {
        this.importValue = importValue;
    }

    @Override
    public String toString() {
        return "PredictionData{" +
                "date='" + date + '\'' +
                ", importValue='" + importValue + '\'' +
                '}';
    }
}
