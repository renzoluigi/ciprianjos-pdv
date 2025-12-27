package com.br.renzoluigi.ciprianjospdv.model;

import java.math.BigDecimal;
import java.sql.Date;

public class Product {
    private String barcode;
    private String type;
    private String brand;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private String imagePath;
    private Date registerDate;

    public Product(String barcode, String type, String brand, String name, BigDecimal price, Integer quantity, String imagePath, Date registerDate) {
        this.barcode = barcode;
        this.type = type;
        this.brand = brand;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imagePath = imagePath;
        this.registerDate = registerDate;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getType() {
        return type;
    }

    public String getBrand() {
        return brand;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getImagePath() {
        return imagePath;
    }

    public Date getRegisterDate() {
        return registerDate;
    }
}
