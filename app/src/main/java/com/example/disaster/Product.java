package com.example.disaster;

public class Product {
    String productName;
    int imgDrawable, orders;

    public int getOrders() {
        return orders;
    }

    public void setOrders(int orders) {
        this.orders = orders;
    }

    public Product(String productName, int productImage, int orders){
        imgDrawable = productImage;
        this.productName = productName;
        this.orders = orders;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getImgDrawable() {
        return imgDrawable;
    }

    public void setImgDrawable(int imgDrawable) {
        this.imgDrawable = imgDrawable;
    }



}
