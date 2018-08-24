package com.example.disaster;

public class Product {
    String productName;
    int imgDrawable;
    public Product(String productName, int productImage){
        imgDrawable = productImage;
        this.productName = productName;
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
