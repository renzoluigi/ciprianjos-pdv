package com.br.renzoluigi.ciprianjospdv;

import org.h2.tools.Server;

import java.sql.*;

public class DatabaseManager {

    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:h2:file/./data/ciprianjos_db", "sa", "admin");
            initializeDatabase(connection);
            return connection;
        } catch (SQLException e) {
            System.out.println("Error during the initialization of db: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static void initializeDatabase(Connection connection) {
        try (Statement stmt = connection.createStatement()) {
            String sqlAdmins = "CREATE TABLE IF NOT EXISTS ADMIN ("
                    + "ID IDENTITY PRIMARY KEY,"
                    + "USERNAME VARCHAR(55) NOT NULL,"
                    + "NAME VARCHAR(255) NOT NULL,"
                    + "PASSWORD VARCHAR(255) NOT NULL"
                    + ");";
            stmt.executeUpdate(sqlAdmins);

            String sqlProducts = "CREATE TABLE IF NOT EXISTS PRODUCT ("
                    + "BARCODE VARCHAR(255) PRIMARY KEY,"
                    + "TYPE VARCHAR(50) NOT NULL,"
                    + "BRAND VARCHAR(50) NOT NULL,"
                    + "NAME VARCHAR(255) NOT NULL,"
                    + "PRICE DECIMAL(10, 2) NOT NULL,"
                    + "QUANTITY INT NOT NULL,"
                    + "IMAGE VARCHAR(255),"
                    + "REGISTER_DATE DATE NOT NULL"
                    + ");";
            stmt.executeUpdate(sqlProducts);

            String sqlCustomer = "CREATE TABLE IF NOT EXISTS CUSTOMER ("
                    + "ID IDENTITY PRIMARY KEY, "
                    + "CUSTOMER_ID INT, "
                    + "TYPE VARCHAR(100), "
                    + "BRAND VARCHAR(100), "
                    + "PRODUCTNAME VARCHAR(100), "
                    + "QUANTITY INT, "
                    + "PRICE DECIMAL(10, 2), "
                    + "DATE DATE"
                    + ");";
            stmt.executeUpdate(sqlCustomer);

            String sqlCustomerReceipt = "CREATE TABLE IF NOT EXISTS CUSTOMER_RECEIPT ("
                    + "ID IDENTITY PRIMARY KEY, "
                    + "CUSTOMER_ID INT NOT NULL, "
                    + "TOTAL DOUBLE NOT NULL, "
                    + "AMOUNT DOUBLE NOT NULL,"
                    + "BALANCE DOUBLE NOT NULL,"
                    + "DATE DATE"
                    + ");";
            stmt.executeUpdate(sqlCustomerReceipt);

            String checkAdminSql = "SELECT COUNT(*) FROM ADMIN WHERE USERNAME = 'admin'";

            try (ResultSet rs = stmt.executeQuery(checkAdminSql)) {

                if (rs.next() && rs.getInt(1) == 0) {

                    String insertAdminSql = "INSERT INTO ADMIN (USERNAME, NAME, PASSWORD) VALUES ("
                            + "'admin', "
                            + "'admin', "
                            + "'admin'"
                            + ")";
                    stmt.executeUpdate(insertAdminSql);

                    System.out.println("Congrats");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error during initialization of schemas");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void startH2ConsoleServer() {
        try {
            Server webServer = Server.createWebServer("-webPort", "8082", "-webAllowOthers").start();
            System.out.println("H2 Console URL: " + webServer.getURL());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
