package com.example.shuanghu.stock;

/**
 * Created by shuanghu on 11/25/17.
 */

public class FavStockItem {

    private String name;

    private String price;

    private String change;

    private String changePercent;


    public FavStockItem(String name, String price, String change, String changePercent) {
        this.name = name;
        this.price = price;
        this.change = change;
        this.changePercent = changePercent;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getChange() {
        return change;
    }

    public String getChangePercent() {return changePercent;}

}
