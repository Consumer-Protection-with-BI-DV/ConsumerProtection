package com.jachdev.consumerprotection.data.enums;

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
                break;
            case 1:
                name = "price";
                break;
            case 2:
                name = "quantity";
                break;
            case 3:
                name = "sales";
                break;
        }
    }

    public int id;
    public String name;

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
}
