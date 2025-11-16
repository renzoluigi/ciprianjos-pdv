package com.br.renzoluigi.ciprianjospdv;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class DashboardController implements Initializable {
    @FXML
    private Label name;

    @FXML
    private Label home_numberOrders;

    @FXML
    private Label home_totalIncome;

    @FXML
    private Label home_availableProducts;

    @FXML
    private AreaChart home_incomeChart;

    @FXML
    private BarChart home_ordersChart;

    @FXML
    private AnchorPane main_form;

    @FXML
    public Button home_button;

    @FXML
    private Button addProducts_button;

    @FXML
    private Button orders_button;

    @FXML
    private AnchorPane home_pane;

    @FXML
    private AnchorPane addProducts_pane;

    @FXML
    private AnchorPane orders_pane;

    @FXML
    private TableView<ProductData> addProducts_table;

    @FXML
    private TableColumn<ProductData, String> addProducts_col_barcode;

    @FXML
    private TableColumn<ProductData, String> addProducts_col_type;

    @FXML
    private TableColumn<ProductData, String> addProducts_col_brand;

    @FXML
    private TableColumn<ProductData, String> addProducts_col_name;

    @FXML
    private TableColumn<ProductData, BigDecimal> addProducts_col_price;

    @FXML
    private TableColumn<ProductData, Integer> addProducts_col_quantity;

    @FXML
    private ImageView addProducts_imageView;

    @FXML
    private TextField addProducts_barcode;

    @FXML
    private ComboBox<String> addProducts_type;

    @FXML
    private TextField addProducts_brand;

    @FXML
    private TextField addProducts_name;

    @FXML
    private TextField addProducts_quantity;

    @FXML
    private TextField addProducts_price;

    @FXML
    private TextField addProducts_search;

    @FXML
    private TextField orders_amount;

    @FXML
    private Spinner<Integer> orders_quantity;

    @FXML
    private ComboBox<String> orders_name;

    @FXML
    private ComboBox<String> orders_brand;

    @FXML
    private ComboBox<String> orders_type;

    @FXML
    private TableColumn<ProductData, String> orders_col_type;

    @FXML
    private TableColumn<ProductData, String> orders_col_brand;

    @FXML
    private TableColumn<ProductData, String> orders_col_name;

    @FXML
    private TableColumn<ProductData, Integer> orders_col_quantity;

    @FXML
    private TableColumn<ProductData, BigDecimal> orders_col_price;

    @FXML
    private TableView<CustomerData> orders_table;

    @FXML
    private Label orders_total;

    @FXML
    private Label orders_balance;

    @FXML
    private TextField orders_barcode;

    private Alert alert;

    private double y = 0;
    private double x = 0;

    private Connection connection;
    private PreparedStatement preparedStatement;
    private Statement statement;
    private ResultSet resultSet;

    private Image image;

    private int customerId;

    private final String[] ordersListType = {"Borracha", "Caneta", "Caderno", "Papel", "Lápis", "Outros"};

    // methods

    public void homeDisplayTotalOrders() { // from today
        String sql = "SELECT COUNT(id) FROM customer_receipt WHERE DATE = CURRENT_DATE";

        connection = DatabaseManager.getConnection();

        int countOrders = 0;

        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                countOrders = resultSet.getInt("COUNT(id)");
            }

            home_numberOrders.setText(String.valueOf(countOrders));
        } catch (Exception e) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Mensagem de erro");
            alert.setHeaderText("Informe esse erro ao desenvolvedor");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    public void homeTotalIncome() {
        String sql = "SELECT SUM(total) FROM customer_receipt WHERE DATE = CURRENT_DATE";

        connection = DatabaseManager.getConnection();

        BigDecimal totalIncome = BigDecimal.ZERO;

        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                totalIncome = new BigDecimal(resultSet.getDouble("SUM(total)")).setScale(2, RoundingMode.UP);
            }

            home_totalIncome.setText("R$ " + totalIncome);
        } catch (Exception e) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Mensagem de erro");
            alert.setHeaderText("Informe esse erro ao desenvolvedor");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    public void homeAvailableProducts() {
        String sql = "SELECT COUNT(barcode) FROM product";

        connection = DatabaseManager.getConnection();

        int countAvailableProducts = 0;

        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                countAvailableProducts = resultSet.getInt("COUNT(barcode)");
            }

            home_availableProducts.setText(String.valueOf(countAvailableProducts));
        } catch (Exception e) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Mensagem de erro");
            alert.setHeaderText("Informe esse erro ao desenvolvedor");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    public void displayHomeIncomeChart() {
        home_incomeChart.getData().clear();

        String sql = "SELECT date, SUM(total) FROM customer_receipt GROUP BY date ORDER BY date";

        connection = DatabaseManager.getConnection();

        try {
            XYChart.Series chart = new XYChart.Series();

            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                chart.getData().add(new XYChart.Data<>(resultSet.getString(1), resultSet.getInt(2)));
            }

            home_incomeChart.getData().add(chart);

        } catch (Exception e) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Mensagem de erro");
            alert.setHeaderText("Informe esse erro ao desenvolvedor");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    public void displayHomeOrdersChart() {
        home_ordersChart.getData().clear();

        String sql = "SELECT date, COUNT(id) FROM customer GROUP BY date ORDER BY date ASC LIMIT 5";

        connection = DatabaseManager.getConnection();

        try {
            XYChart.Series chart = new XYChart.Series();

            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                chart.getData().add(new XYChart.Data(resultSet.getString(1), resultSet.getInt(2)));
            }

            home_ordersChart.getData().add(chart);

        } catch (Exception e) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Mensagem de erro");
            alert.setHeaderText("Informe esse erro ao desenvolvedor");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    public void addProductsAdd() {

        String sql = "INSERT INTO product(barcode, type, brand, name, price, quantity, image, register_date)" +
                "VALUES(?,?,?,?,?,?,?,?)";
        connection = DatabaseManager.getConnection();

        try {
            if (addProducts_barcode.getText().isBlank()
                    || addProducts_name.getText().isBlank()
                    || addProducts_brand.getText().isBlank()
                    || addProducts_quantity.getText().isBlank()
                    || addProducts_type.getSelectionModel().getSelectedItem() == null
                    || addProducts_price.getText().isBlank()) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Mensagem de erro");
                alert.setHeaderText(null);
                alert.setContentText("Por favor, preencha todos os campos.");
                alert.showAndWait();
            } else {
                String checkData = "SELECT barcode FROM product WHERE barcode = '" +
                        addProducts_barcode.getText() + "'";

                statement = connection.createStatement();
                resultSet = statement.executeQuery(checkData);

                if (resultSet.next()) {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText(null);
                    alert.setTitle("Mensagem de erro");
                    alert.setContentText("Já existe um produto com o código de barras: " + addProducts_barcode.getText());
                    alert.showAndWait();
                } else {
                    BigDecimal price = new BigDecimal(addProducts_price.getText().trim());
                    int quantity = Integer.parseInt(addProducts_quantity.getText().trim());

                    preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setString(1, addProducts_barcode.getText());
                    preparedStatement.setString(2, addProducts_type.getSelectionModel().getSelectedItem());
                    preparedStatement.setString(3, addProducts_brand.getText());
                    preparedStatement.setString(4, addProducts_name.getText());
                    preparedStatement.setBigDecimal(5, price);
                    preparedStatement.setInt(6, quantity);
                    if (GetData.path == null) {
                        preparedStatement.setString(7, null);
                    } else {
                        preparedStatement.setString(7, GetData.path.replace("\\", "\\\\"));
                    }
                    preparedStatement.setDate(8, new Date(System.currentTimeMillis()));

                    preparedStatement.executeUpdate();

                    addProductsShowListData();
                    addProductsClear();
                }
            }
        } catch (NumberFormatException nfe) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Mensagem de erro");
            alert.setHeaderText(null);
            alert.setContentText("Preço ou quantidade possuem formato inválido.");
            alert.showAndWait();
            nfe.printStackTrace();
        } catch (SQLException e) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Mensagem de erro");
            alert.setHeaderText(null);
            if (e.getErrorCode() == 23505) {
                alert.setContentText("Já existe um produto com este código de barras.");
            } else {
                alert.setContentText("Falha ao inserir produto: " + e.getMessage());
            }
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    public void addProductsUpdate() {
        String uri = (GetData.path == null || GetData.path.isBlank()) ? null : GetData.path.replace("\\", "\\\\");
        String sql = "UPDATE product " + "SET type = ?, brand = ?, name = ?, price = ?, quantity = ?, image = ? " +
                "WHERE barcode = ?";

        connection = DatabaseManager.getConnection();

        try {
            if (addProducts_barcode.getText().isBlank()
                    || addProducts_name.getText().isBlank()
                    || addProducts_brand.getText().isBlank()
                    || addProducts_quantity.getText().isBlank()
                    || addProducts_type.getSelectionModel().getSelectedItem() == null
                    || addProducts_price.getText().isBlank()) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Mensagem de erro");
                alert.setHeaderText(null);
                alert.setContentText("Por favor, preencha todos os campos.");
                alert.showAndWait();
            } else {
                String checkData = "SELECT barcode FROM product WHERE barcode = '"
                        + addProducts_barcode.getText() + "'";
                statement = connection.createStatement();
                resultSet = statement.executeQuery(checkData);

                if (resultSet.next()) {
                    BigDecimal price = new BigDecimal(addProducts_price.getText().trim());
                    int quantity = Integer.parseInt(addProducts_quantity.getText().trim());

                    alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Mensagem de confirmação");
                    alert.setHeaderText(null);
                    alert.setContentText("Tem certeza que deseja atualizar o produto: " + addProducts_barcode.getText() + "?");
                    Optional<ButtonType> option = alert.showAndWait();
                    if (option.isEmpty() || option.get() != ButtonType.OK) return;

                    preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setString(1, addProducts_type.getSelectionModel().getSelectedItem());
                    preparedStatement.setString(2, addProducts_brand.getText().trim());
                    preparedStatement.setString(3, addProducts_name.getText().trim());
                    preparedStatement.setBigDecimal(4, price);
                    preparedStatement.setInt(5, quantity);
                    if (uri == null) {
                        preparedStatement.setNull(6, Types.VARCHAR);
                    } else {
                        preparedStatement.setString(6, uri);
                    }
                    preparedStatement.setString(7, addProducts_barcode.getText().trim());

                    preparedStatement.executeUpdate();

                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Mensagem de informação");
                    alert.setHeaderText(null);
                    alert.setContentText("Atualizado com sucesso");
                    alert.showAndWait();

                    addProductsShowListData();
                    addProductsClear();
                } else {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Mensagem de erro");
                    alert.setHeaderText(null);
                    alert.setContentText("Produto com o código de barras: " + addProducts_barcode.getText() + "não encontrado.");
                    alert.showAndWait();
                }
            }
        } catch (NumberFormatException nfe) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Mensagem de erro");
            alert.setHeaderText(null);
            alert.setContentText("Preço ou quantidade possuem formato inválido.");
            alert.showAndWait();
            nfe.printStackTrace();
        } catch (SQLException e) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Mensagem de erro");
            alert.setHeaderText("Informe esse erro ao desenvolvedor");
            alert.setContentText("Falha ao atualizar produto: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    public void addProductsDelete() {
        String sql = "DELETE FROM product WHERE barcode = '" + addProducts_barcode.getText() + "'";

        connection = DatabaseManager.getConnection();

        try {
            if (addProducts_barcode.getText().isBlank()
                    || addProducts_name.getText().isBlank()
                    || addProducts_brand.getText().isBlank()
                    || addProducts_quantity.getText().isBlank()
                    || addProducts_type.getSelectionModel().getSelectedItem() == null
                    || addProducts_price.getText().isBlank()) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Mensagem de erro");
                alert.setHeaderText(null);
                alert.setContentText("Por favor, preencha todos os campos.");
                alert.showAndWait();
            } else {
                String checkData = "SELECT barcode FROM product WHERE barcode = '" +
                        addProducts_barcode.getText() + "'";
                statement = connection.createStatement();
                resultSet = statement.executeQuery(checkData);

                if (resultSet.next()) {
                    alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Mensagem de confirmação");
                    alert.setHeaderText(null);
                    alert.setContentText("Tem certeza que deseja atualizar o produto: " + addProducts_barcode.getText() + "?");
                    Optional<ButtonType> option = alert.showAndWait();

                    if (option.get() == ButtonType.OK) {
                        statement = connection.createStatement();
                        statement.executeUpdate(sql);

                        alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText(null);
                        alert.setContentText("Deletado com sucesso");
                        alert.showAndWait();
                    }

                    statement = connection.createStatement();
                    statement.executeUpdate(sql);

                    addProductsClear();
                    addProductsShowListData();
                } else {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Mensagem de erro");
                    alert.setHeaderText(null);
                    alert.setContentText("Não existe um produto com o código de barras: " + addProducts_barcode.getText());
                    alert.showAndWait();
                }

            }
        } catch (SQLException e) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Mensagem de erro");
            alert.setHeaderText("Informe esse erro ao desenvolvedor");
            alert.setContentText("Falha ao deletar o produto: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    public void addProductsClear() {
        addProducts_quantity.clear();
        addProducts_price.clear();
        addProducts_name.clear();
        addProducts_brand.clear();
        addProducts_type.getSelectionModel().clearSelection();
        addProducts_barcode.clear();
        addProducts_imageView.setImage(null);
        GetData.path = "";
    }

    public void addProductsImage() {
        FileChooser open = new FileChooser();
        open.setTitle("Abrir arquivo de imagem");
        open.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivo de imagem", "*jpg", "*png"));

        File file = open.showOpenDialog(main_form.getScene().getWindow());
        if (file != null) {
            image = new Image(file.toURI().toString(), 115, 128, false, true);
            addProducts_imageView.setImage(image);
            GetData.path = file.getAbsolutePath();
        }
    }

    public ObservableList<ProductData> getAddProductsListData() {

        ObservableList<ProductData> productList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM product";

        connection = DatabaseManager.getConnection();

        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            ProductData productData;
            while (resultSet.next()) {

                productData = new ProductData(
                        resultSet.getString("barcode"),
                        resultSet.getString("type"),
                        resultSet.getString("brand"),
                        resultSet.getString("name"),
                        resultSet.getBigDecimal("price"),
                        resultSet.getInt("quantity"),
                        resultSet.getDate("register_date"),
                        resultSet.getString("image")
                );

                productList.add(productData);
            }
        } catch (Exception e) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Mensagem de erro");
            alert.setHeaderText(null);
            alert.setContentText("Informe essa mensagem ao desenvolvedor: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
        return productList;
    }

    public ObservableList<ProductData> addProductsList;

    public void addProductsShowListData() {
        addProductsList = getAddProductsListData();

        addProducts_col_barcode.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        addProducts_col_type.setCellValueFactory(new PropertyValueFactory<>("type"));
        addProducts_col_brand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        addProducts_col_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        addProducts_col_quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        addProducts_col_price.setCellValueFactory(new PropertyValueFactory<>("price"));

        addProducts_table.setItems(addProductsList);
    }

    public void addProductsSearch() {
        FilteredList<ProductData> filter = new FilteredList<>(addProductsList, e -> true);

        addProducts_search.textProperty().addListener((Observable, oldValue, newValue) ->
                filter.setPredicate(predicateProductData -> {
                    if (newValue == null || newValue.isBlank()) {
                        return true;
                    }

                    String searchKey = newValue.toLowerCase();

                    if (predicateProductData.getBarcode().toString().contains(searchKey)) {
                        return true;
                    } else if (predicateProductData.getType().toLowerCase().contains(searchKey)) {
                        return true;
                    } else if (predicateProductData.getName().toLowerCase().contains(searchKey)) {
                        return true;
                    } else if (predicateProductData.getBrand().toLowerCase().contains(searchKey)) {
                        return true;
                    } else return predicateProductData.getPrice().toString().contains(searchKey);
                })
        );
        SortedList<ProductData> sortedList = new SortedList<>(filter);
        sortedList.comparatorProperty().bind(addProducts_table.comparatorProperty());
        addProducts_table.setItems(sortedList);
    }

    public void addProductsSelect() {
        ProductData selectedProduct = addProducts_table.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) return;

        addProducts_barcode.setText(selectedProduct.getBarcode());
        addProducts_brand.setText(selectedProduct.getBrand());
        addProducts_name.setText(selectedProduct.getName());
        addProducts_quantity.setText(String.valueOf(selectedProduct.getQuantity()));
        addProducts_price.setText(String.valueOf(selectedProduct.getPrice()));

        if (selectedProduct.getImage() != null && !selectedProduct.getImage().isBlank()) {
            Image image = new Image("file:" + selectedProduct.getImage(), 115, 128, false, true);
            addProducts_imageView.setImage(image);
        } else {
            addProducts_imageView.setImage(null);
        }
    }

    private final String[] listType = {"Borracha", "Caneta", "Caderno", "Papel", "Lápis", "Outros"};

    public void addProductsListType() {
        List<String> listType = Arrays.stream(this.listType).toList();

        ObservableList<String> listData = FXCollections.observableArrayList(listType);
        addProducts_type.setItems(listData);
    }

    public void ordersListType() {
        List<String> listT = new ArrayList<>(Arrays.asList(ordersListType));

        ObservableList<String> listData = FXCollections.observableArrayList(listT);
        orders_type.setItems(listData);

        ordersListBrand();
    }

    public void ordersListBrand() {
        String sql = "SELECT brand FROM product WHERE type = '" + orders_type.getSelectionModel().getSelectedItem() + "'";

        connection = DatabaseManager.getConnection();

        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            ObservableList<String> availableBrands = FXCollections.observableArrayList();

            while (resultSet.next()) {
                availableBrands.add(resultSet.getString("brand"));
            }
            orders_brand.setItems(availableBrands);

            ordersListName();
        } catch (Exception e) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Mensagem de erro");
            alert.setContentText(e.getMessage());
            alert.setHeaderText("Informe esse erro ao desenvolvedor");
            alert.showAndWait();
            e.printStackTrace();
        }

    }

    public void ordersListName() {
        String sql = "SELECT name FROM product WHERE brand = '" + orders_brand.getSelectionModel().getSelectedItem() + "'";

        connection = DatabaseManager.getConnection();

        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            ObservableList<String> availableNames = FXCollections.observableArrayList();

            while (resultSet.next()) {
                availableNames.add(resultSet.getString("name"));
            }

            orders_name.setItems(availableNames);
        } catch (Exception e) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Mensagem de erro");
            alert.setHeaderText("Informe esse erro ao desenvolvedor");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    private SpinnerValueFactory<Integer> spinner;

    public void ordersSpinner() {
        spinner = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0);

        orders_quantity.setValueFactory(spinner);
    }

    public void ordersAdd() {
        setCustomerId();
        String sql = "INSERT INTO customer (customer_id,type,brand,productName,quantity,price,date) " +
                "VALUES(?,?,?,?,?,?,?)";

        connection = DatabaseManager.getConnection();

        try {
            String checkData = "SELECT * FROM product WHERE name = '" +
                    orders_name.getSelectionModel().getSelectedItem() + "'";

            BigDecimal totalProductPrice = BigDecimal.ZERO;

            statement = connection.createStatement();
            resultSet = statement.executeQuery(checkData);

            if (resultSet.next()) {
                totalProductPrice = resultSet.getBigDecimal("price")
                        .multiply(new BigDecimal(orders_quantity.getValue())).setScale(2, RoundingMode.UP);
            }

            if (orders_type.getSelectionModel().getSelectedItem() == null || orders_quantity.getValue() == null
                    || orders_name.getSelectionModel().getSelectedItem() == null || totalProductPrice.doubleValue() == 0) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Mensagem de erro");
                alert.setHeaderText(null);
                alert.setContentText("Por favor, escolha o produto primeiro.");
                alert.showAndWait();
            } else {

                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, String.valueOf(customerId));
                preparedStatement.setString(2, orders_type.getSelectionModel().getSelectedItem());
                preparedStatement.setString(3, orders_brand.getSelectionModel().getSelectedItem());
                preparedStatement.setString(4, orders_name.getSelectionModel().getSelectedItem());
                preparedStatement.setString(5, String.valueOf(orders_quantity.getValue()));
                preparedStatement.setString(6, totalProductPrice.toString());
                preparedStatement.setString(7, String.valueOf(new Date(System.currentTimeMillis())));

                preparedStatement.executeUpdate();

                showTotalPrice();
                showOrdersList();
            }
        } catch (Exception e) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Mensagem de erro");
            alert.setHeaderText("Informe esse erro ao desenvolvedor");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    private BigDecimal totalPrice;
    public void showTotalPrice() {
        String sql = "SELECT SUM(price) FROM customer WHERE customer_id = '" + customerId + "'";

        connection = DatabaseManager.getConnection();

        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                totalPrice = new BigDecimal(resultSet.getDouble("SUM(price)"))
                        .setScale(2, RoundingMode.UP);
            }

            orders_total.setText("R$ " + totalPrice);
        } catch (Exception e) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Mensagem de erro");
            alert.setHeaderText("Informe esse erro ao desenvolvedor");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    public void ordersPay() {
        if (amountPrice == null) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Mensagem de erro");
            alert.setHeaderText(null);
            alert.setContentText("Pressione ENTER após inserir o valor recebido.");
            alert.showAndWait();
            return;
        }

        setCustomerId();
        String sql = "INSERT INTO customer_receipt (customer_id,total,amount,balance,date) VALUES(?,?,?,?,?)";

        connection = DatabaseManager.getConnection();

        try {
            if (totalPrice.doubleValue() > 0 && !orders_amount.getText().isBlank() && amountPrice.doubleValue() > 0) { //109
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Mensagem de confirmação");
                alert.setHeaderText(null);
                alert.setContentText("Você tem certeza?");
                Optional<ButtonType> option = alert.showAndWait();

                if (option.get().equals(ButtonType.OK)) {
                    preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setString(1, String.valueOf(customerId));
                    preparedStatement.setString(2, String.valueOf(totalPrice));
                    preparedStatement.setString(3, String.valueOf(amountPrice));
                    preparedStatement.setString(4, String.valueOf(balance));
                    preparedStatement.setString(5, String.valueOf(new Date(new java.util.Date().getTime())));

                    preparedStatement.executeUpdate();

                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Mensagem de informação");
                    alert.setHeaderText(null);
                    alert.setContentText("Concluído!");
                    alert.showAndWait();

                    showOrdersList();

                    totalPrice = BigDecimal.ZERO;
                    balance = BigDecimal.ZERO;
                    orders_amount.clear();
                    orders_balance.setText("R$ 0.00");
                }
            } else {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Mensagem de erro");
                alert.setHeaderText(null);
                alert.setContentText("Preço inválido ("+ totalPrice + ")");
                alert.showAndWait();
            }
        } catch (Exception e) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Mensagem de erro");
            alert.setHeaderText("Informe esse erro ao desenvolvedor:");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    private BigDecimal amountPrice;
    private BigDecimal balance;
    public void ordersAmount() {

        if (!orders_amount.getText().isEmpty()) {
            amountPrice = new BigDecimal(orders_amount.getText());

            if (totalPrice.doubleValue() > 0) {
                if (amountPrice.compareTo(totalPrice) >= 0) {

                    balance = (amountPrice.subtract(totalPrice));
                    orders_balance.setText("R$ " + balance);

                } else {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Mensagem de erro");
                    alert.setHeaderText(null);
                    alert.setContentText("Preço maior que valor recebido.");
                    alert.showAndWait();
                }
            } else {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Mensagem de erro");
                alert.setHeaderText(null);
                alert.setContentText("Preço inválido (" + totalPrice + ")");
                alert.showAndWait();
            }
        } else {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Mensagem de erro");
            alert.setHeaderText(null);
            alert.setContentText("Valor recebido não inserido.");
            alert.showAndWait();
        }
    }

    public void setCustomerId() {
        String customId = "SELECT * FROM customer";
        connection = DatabaseManager.getConnection();

        try {
            preparedStatement = connection.prepareStatement(customId);
            resultSet = preparedStatement.executeQuery();

            int checkId = 0;

            while (resultSet.next()) {
                customerId = resultSet.getInt("customer_id");
            }

            String checkData = "SELECT * FROM customer_receipt";

            statement = connection.createStatement();
            resultSet = statement.executeQuery(checkData);

            while (resultSet.next()) {
                checkId = resultSet.getInt("customer_id");
            }
            if (customerId == 0) {
                customerId++;
            } else if (checkId == customerId) {
                customerId++;
            }

        } catch (Exception e) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Mensagem de erro");
            alert.setHeaderText("Informe esse erro ao desenvolvedor:");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    public ObservableList<CustomerData> getOrdersList() {
        setCustomerId();
        ObservableList<CustomerData> listData = FXCollections.observableArrayList();
        String sql = "SELECT * FROM customer WHERE customer_id = '" + customerId + "'";

        connection = DatabaseManager.getConnection();

        try {
            CustomerData customerData;
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                customerData = new CustomerData(
                        resultSet.getInt("customer_id"),
                        resultSet.getString("type"),
                        resultSet.getString("brand"),
                        resultSet.getString("productName"),
                        resultSet.getInt("quantity"),
                        resultSet.getBigDecimal("price"),
                        new Date(System.currentTimeMillis()));
                listData.add(customerData);
            }

        } catch (Exception e) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Mensagem de erro");
            alert.setHeaderText("Informe esse erro ao desenvolvedor:");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
        return listData;
    }

    private ObservableList<CustomerData> ordersList;

    public void showOrdersList() {
        ordersList = getOrdersList();

        orders_col_type.setCellValueFactory(new PropertyValueFactory<>("type"));
        orders_col_brand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        orders_col_name.setCellValueFactory(new PropertyValueFactory<>("productName"));
        orders_col_quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        orders_col_price.setCellValueFactory(new PropertyValueFactory<>("price"));

        orders_table.setItems(ordersList);
        showTotalPrice();
    }

    public void ordersClear() {
        alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Mensagem de confirmação");
        alert.setHeaderText(null);
        alert.setContentText("Tem certeza que deseja limpar a lista de pedidos?");
        Optional<ButtonType> option = alert.showAndWait();

        if (option.get() == ButtonType.OK) {
            String sql = "DELETE FROM customer WHERE customer_id = ?";

            connection = DatabaseManager.getConnection();

            try {
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, String.valueOf(customerId));
                preparedStatement.executeUpdate();

                orders_table.getItems().clear();
                totalPrice = BigDecimal.ZERO;
                balance = BigDecimal.ZERO;
                amountPrice = BigDecimal.ZERO;

                orders_type.getSelectionModel().clearSelection();
                orders_brand.getSelectionModel().clearSelection();
                orders_name.getSelectionModel().clearSelection();

                orders_total.setText("R$ 0.00");
                orders_amount.clear();
                orders_balance.setText("R$ 0.00");

            } catch (Exception e) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Mensagem de erro");
                alert.setHeaderText("Informe esse erro ao desenvolvedor:");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
                e.printStackTrace();
            }
        }
    }

    public void ordersReadBarcode() {
        String sql = "SELECT * FROM product WHERE barcode = '" + orders_barcode.getText() + "'";
        connection = DatabaseManager.getConnection();

        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                System.out.println(resultSet.getString("type"));
                System.out.println(resultSet.getString("brand"));
                System.out.println(resultSet.getString("name"));
            } else {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Mensagem de erro");
                alert.setHeaderText(null);
                alert.setContentText("Nenhum produto encontrado com o código de barras: " + orders_barcode.getText());
                alert.showAndWait();
            }
        } catch (Exception e) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Mensagem de erro");
            alert.setHeaderText("Informe esse erro ao desenvolvedor:");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    public void switchPane(ActionEvent event) {
        if (event.getSource() == home_button) {
            home_pane.setVisible(true);
            addProducts_pane.setVisible(false);
            orders_pane.setVisible(false);

            home_button.setStyle("-fx-background-color: linear-gradient(to right, #0077ff, #00A3FF);");
            addProducts_button.setStyle("-fx-background-color: transparent");
            orders_button.setStyle("-fx-background-color: transparent");
            homeDisplayTotalOrders();
            homeTotalIncome();
            homeAvailableProducts();
            displayHomeIncomeChart();
            displayHomeOrdersChart();

        } else if (event.getSource() == addProducts_button) {
            home_pane.setVisible(false);
            addProducts_pane.setVisible(true);
            orders_pane.setVisible(false);

            home_button.setStyle("-fx-background-color: transparent");
            addProducts_button.setStyle("-fx-background-color: linear-gradient(to right, #0077ff, #00A3FF);");
            orders_button.setStyle("-fx-background-color: transparent");

            addProductsShowListData();
            addProductsListType();
            addProductsSearch();
        } else if (event.getSource() == orders_button) {
            home_pane.setVisible(false);
            addProducts_pane.setVisible(false);
            orders_pane.setVisible(true);

            home_button.setStyle("-fx-background-color: transparent");
            addProducts_button.setStyle("-fx-background-color: transparent");
            orders_button.setStyle("-fx-background-color: linear-gradient(to right, #0077ff, #00A3FF);");
        }
    }

    public void defaultNav() {
        home_button.setStyle("-fx-background-color: linear-gradient(to right, #0077ff, #00A3FF);");
    }

    public void logout() {
        try {
            alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Mensagem de confirmação");
            alert.setHeaderText(null);
            alert.setContentText("Tem certeza que deseja sair?");

            Optional<ButtonType> option = alert.showAndWait();

            if (option.isPresent() && option.get() == ButtonType.OK) {
                main_form.getScene().getWindow().hide();

                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login.fxml")));
                Scene scene = new Scene(root);
                Stage stage = new Stage();

                root.setOnMousePressed(mouseEvent -> {
                    x = mouseEvent.getSceneX();
                    y = mouseEvent.getSceneY();
                });

                root.setOnMouseDragged(mouseEvent -> {
                    stage.setX(mouseEvent.getScreenX() - x);
                    stage.setY(mouseEvent.getScreenY() - y);
                });

                root.setOnMouseReleased(mouseEvent -> {
                    stage.setOpacity(1);
                });

                stage.initStyle(StageStyle.TRANSPARENT);

                stage.setScene(scene);
                stage.show();
            }


        } catch (Exception e) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Mensagem de erro");
            alert.setHeaderText("Informe esse erro ao desenvolvedor");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    public void displayName() {
        name.setText(GetData.name);
    }

    public void minimize() {
        Stage stage = (Stage) main_form.getScene().getWindow();
        stage.setIconified(true);
    }

    public void close() {
        System.exit(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        displayName();
        defaultNav();

        homeDisplayTotalOrders();
        homeTotalIncome();
        homeAvailableProducts();
        displayHomeIncomeChart();
        displayHomeOrdersChart();

        addProductsShowListData();
        addProductsSearch();
        addProductsListType();

        showOrdersList();
        ordersListType();
        ordersListBrand();
        ordersListName();
        ordersSpinner();
    }
}
