package com.jachdev.consumerprotection.data;

/**
 * Created by Asanka on 9/26/2021.
 */
public class Category {

    private int id;
    private String type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", type='" + type + '\'' +
                '}';
    }
}
