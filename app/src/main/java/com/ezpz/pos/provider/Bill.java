package com.ezpz.pos.provider;

/**
 * Created by RezaPramudhika on 8/31/2017.
 */

public class Bill {
    private int productId;
    private String productName;
    private String price;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
