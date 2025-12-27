package com.br.renzoluigi.ciprianjospdv.model;

import com.br.renzoluigi.ciprianjospdv.db.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ProductDAOImpl implements ProductDAO {
    private Connection connection;

    public ProductDAOImpl() {
        this.connection = DatabaseManager.getConnection();
    }

    @Override
    public void save(Product product) throws SQLException {
        String sql = "INSERT INTO product(barcode, type, brand, name, price, quantity, image, register_date) " +
                "VALUES(?,?,?,?,?,?,?,?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, product.getBarcode());
            ps.setString(2, product.getType());
            ps.setString(3, product.getBrand());
            ps.setString(4, product.getName());
            ps.setBigDecimal(5, product.getPrice());
            ps.setInt(6, product.getQuantity());
            ps.setString(7, product.getImagePath());
            ps.setDate(8, product.getRegisterDate());

            ps.executeUpdate();
        }
    }

    @Override
    public void update(Product product) throws SQLException {
        String sql = "UPDATE product " + "SET type = ?, brand = ?, name = ?, price = ?, quantity = ?, image = ? " +
                "WHERE barcode = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

        }
    }

    @Override
    public void delete(String barcode) throws Exception {

    }

    @Override
    public Product findByBarcode(String barcode) {
        return null;
    }

    @Override
    public List<Product> findAll() {
        return List.of();
    }
}
