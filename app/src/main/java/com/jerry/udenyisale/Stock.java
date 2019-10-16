package com.jerry.udenyisale;

public class Stock {
    private int quantity;
    private String itemName;
    private double sumTotal =0.0;
    private double unitPrice;
    public Stock(String itemName,int quantity, double unitPrice){
        this.quantity= quantity;
        this.itemName=itemName;
        this.unitPrice=unitPrice;
        this.sumTotal = sumTotal;
    }
    public String getItemName(){
        return itemName;
    }
    public int getQuantity(){
        return quantity;
    }
    public double getSumTotal(){
        return (quantity*unitPrice);
    }
    public double getUnitPrice(){
        return unitPrice;
    }
}
