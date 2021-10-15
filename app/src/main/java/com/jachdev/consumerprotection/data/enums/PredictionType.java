package com.jachdev.consumerprotection.data.enums;

import com.jachdev.consumerprotection.R;

/**
 * Created by Charitha Ratnayake(charitha.r@eyepax.com) on 6/5/2021.
 */
public enum PredictionType {
    IMPORT(0), PRICE(1), QUANTITY(2), SALES(3);

    PredictionType(int value) {
        this.id = value;

        switch (value){
            case 0:
                name = "import";
                title = R.string.import_prediction;
                break;
            case 1:
                name = "price";
                title = R.string.price_prediction;
                break;
            case 2:
                name = "quantity";
                title = R.string.price_prediction;
                break;
            case 3:
                name = "sales";
                title = R.string.sales_prediction;
                break;
        }
    }

    public int id;
    public String name;
    public int title;

    public static PredictionType getPredictionType(int value) {
        PredictionType[] types = PredictionType.values();
        for (PredictionType type : types) {
            if(type.getId() == value)
                return type;
        }
        return IMPORT;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }
}
