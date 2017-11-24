package com.example.shuanghu.stock;

/**
 * Created by shuanghu on 11/22/17.
 */

public class TableItem {
    private String property;
    private String value;
    private int arrow;
    public TableItem(String property, String value, int arrow) {
        this.property = property;
        this.value = value;
        this.arrow = arrow;
    }

    public String getProperty() {
        return property;
    }
    public void setProperty(String property) {
        this.property = property;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }

    public int getArrow() {
        return arrow;
    }

    public void setArrow(int arrow) {
        this.arrow = arrow;
    }
}
