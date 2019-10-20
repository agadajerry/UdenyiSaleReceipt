package com.jerry.udenyisale;

public class Stock {
     int quantity;
     String itemName;
     double sumTotal =0.0;
     double unitPrice;
    public Stock(String itemName,int quantity, double unitPrice){
        this.quantity= quantity;
        this.itemName=itemName;
        this.unitPrice=unitPrice;
        this.sumTotal = sumTotal;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
    public double getSumTotal(){
        return (quantity*unitPrice);
    }
}

