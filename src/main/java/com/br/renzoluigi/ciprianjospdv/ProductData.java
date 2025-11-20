package com.br.renzoluigi.ciprianjospdv;

import java.math.BigDecimal;
import java.util.Date;

public class ProductData {
    private String barcode;
    private String type;
    private String brand;
    private String name;
    private BigDecimal price;

    private Integer quantity;
    private Date registerDate;
    private String image;

    public ProductData(String barcode, String type, String brand, String name, BigDecimal price, Integer quantity, Date registerDate, String image) {
        this.barcode = barcode;
        this.type = type;
        this.brand = brand;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.registerDate = registerDate;
        this.image = image;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
