package com.example.shuanghu.stock;

/**
 * Created by shuanghu on 11/25/17.
 */

public class FavStockItem {

    private String name;

    private String price;

    private String change;

    private String changePercent;

    private long dateCreated;



    public FavStockItem(String name, String price, String change, String changePercent, long dateCreated) {
        this.name = name;
        this.price = price;
        this.change = change;
        this.changePercent = changePercent;
        this.dateCreated = dateCreated;
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

    public long getDateCreated() {
        return dateCreated;
    }

    public double getPriceVal() {
        return Double.parseDouble(price);
    }

    public double getChangeVal() {
        return Double.parseDouble(change);
    }

    public double getChangePercentVal() {
        return Double.parseDouble(changePercent);
    }


}
