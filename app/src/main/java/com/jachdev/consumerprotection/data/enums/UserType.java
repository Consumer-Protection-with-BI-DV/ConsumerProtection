package com.jachdev.consumerprotection.data.enums;

/**
 * Created by Charitha Ratnayake(charitha.r@eyepax.com) on 6/5/2021.
 */
public enum UserType {
    CONSUMER(0), VENDOR(1), ADMIN(2);

    UserType(int value) {
        this.id = value;

        switch (value){
            case 0:
                name = "Consumer";
                break;
            case 1:
                name = "Vendor";
                break;
            case 2:
                name = "Admin";
                break;
        }
    }

    public int id;
    public String name;

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
