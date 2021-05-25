package com.gomorra.witf.model;

public class Product {
    //19222106,24998702,Red Onions Pack,1000,1,20201217,8
    private int productId;
    private String productName;
    private int productWeight;
    private int productQuantity;
    private String productExpiryDate;
    private int productSecondaryQuantity;
    private int productTotalQuantity;

    public Product() {
    }

    public Product(int id, String productName, int productWeight, int quantity, String date, int productSecondaryQuantity, int productTotalQuantity) {
        this.productId = id;
        this.productName = productName;
        this.productWeight = productWeight;
        this.productQuantity = quantity;
        this.productExpiryDate = date;
        this.productSecondaryQuantity = productSecondaryQuantity;
        this.productTotalQuantity = productTotalQuantity;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int id) {
        this.productId = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductWeight() {
        return productWeight;
    }

    public void setProductWeight(int productWeight) {
        this.productWeight = productWeight;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int quantity) {
        this.productQuantity = quantity;
    }

    public String getProductExpiryDate() {
        return productExpiryDate;
    }

    public void setProductExpiryDate(String date) {
        this.productExpiryDate = date;
    }

    public int getProductSecondaryQuantity() {
        return productSecondaryQuantity;
    }

    public void setProductSecondaryQuantity(int productSecondaryQuantity) {
        this.productSecondaryQuantity = productSecondaryQuantity;
    }

    public int getProductTotalQuantity() {
        return productTotalQuantity;
    }

    public void setProductTotalQuantity(int productTotalQuantity) {
        this.productTotalQuantity = productTotalQuantity;
    }
}
