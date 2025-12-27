package com.br.renzoluigi.ciprianjospdv.model;

import java.util.List;

public interface ProductDAO {
    void save(Product product) throws Exception;
    void update(Product product) throws Exception;
    void delete(String barcode) throws Exception;
    Product findByBarcode(String barcode);
    List<Product> findAll();
}
