package com.br.renzoluigi.ciprianjospdv;

import com.br.renzoluigi.ciprianjospdv.dto.CustomerData;
import com.br.renzoluigi.ciprianjospdv.db.DatabaseManager;
import com.br.renzoluigi.ciprianjospdv.dto.ProductData;
import com.br.renzoluigi.ciprianjospdv.model.Product;
import com.br.renzoluigi.ciprianjospdv.model.ProductDAO;
import com.br.renzoluigi.ciprianjospdv.model.ProductDAOImpl;
import com.br.renzoluigi.ciprianjospdv.util.FormatPrice;
import com.br.renzoluigi.ciprianjospdv.util.GetData;
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
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

import static javafx.scene.control.Alert.AlertType.ERROR;
import static javafx.scene.control.Alert.AlertType.INFORMATION;

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

    @FXML
    private TextField orders_nameField;

    private Alert alert;

    private double y = 0;
    private double x = 0;

    private Connection connection;
    private PreparedStatement preparedStatement;
    private Statement statement;
    private ResultSet resultSet;

    private Image image;

    private int customerId;

    // ADD PRODUCTS PANE

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
            showAlert(ERROR, e.getMessage());
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
                totalIncome = new BigDecimal(resultSet.getDouble("SUM(total)"))
                        .setScale(2, RoundingMode.HALF_UP);
            }

            home_totalIncome.setText(FormatPrice.bigDecimalToString(totalIncome));
        } catch (Exception e) {
            showAlert(ERROR, e.getMessage());
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
            showAlert(ERROR, e.getMessage());
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
            showAlert(ERROR, e.getMessage());
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
            showAlert(ERROR, e.getMessage());
        }
    }

    public void addProductsAdd() {
        if (addProductsFieldsAreEmpty()) {
            showAlert(ERROR, "Por favor, preencha todos os campos.");
            return;
        }

        try {
            Product product = new Product(
                    addProducts_barcode.getText(),
                    addProducts_type.getValue(),
                    addProducts_brand.getText(),
                    addProducts_name.getText(),
                    FormatPrice.stringToBigDecimal(addProducts_price.getText()),
                    Integer.parseInt(addProducts_quantity.getText()),
                    GetData.path,
                    new Date(System.currentTimeMillis())
            );

            ProductDAO dao = new ProductDAOImpl();
            dao.save(product);
        } catch (NumberFormatException nfe) {
            showAlert(ERROR, "Preço ou quantidade possuem formato inválido.");
        } catch (SQLException e) {
            if (e.getErrorCode() == 23505) {
                showAlert(ERROR, "Já existe um produto com este código de barras.");
            }
            showAlert(ERROR, "Falha ao inserir produto: " + e.getMessage());
        } catch (Exception e) {
            showAlert(ERROR, e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String message) { // header based on alertType
        // alertType factory
    }

    private boolean addProductsFieldsAreEmpty() {
        return addProducts_barcode.getText().isBlank()
                || addProducts_name.getText().isBlank()
                || addProducts_brand.getText().isBlank()
                || addProducts_quantity.getText().isBlank()
                || addProducts_type.getSelectionModel().getSelectedItem() == null
                || addProducts_price.getText().isBlank();
    }

    public void addProductsUpdate() {
        if (addProductsFieldsAreEmpty()) {
            showAlert(ERROR, "Por favor, preencha todos os campos.");
            return;
        }
        try {
            updateProduct();
        } catch (NumberFormatException nfe) {
            showAlert(ERROR, "Preço ou quantidade possuem formato inválido.");
        } catch (SQLException e) {
            showAlert(ERROR, "Falha ao atualizar produto: " + e.getMessage());
        }
    }

    public void updateProduct() throws SQLException {
        String uri = (GetData.path == null || GetData.path.isBlank()) ? null : GetData.path.replace("\\", "\\\\");
        String sql = "UPDATE product " + "SET type = ?, brand = ?, name = ?, price = ?, quantity = ?, image = ? " +
                "WHERE barcode = ?";

        String checkData = "SELECT barcode FROM product WHERE barcode = '"
                + addProducts_barcode.getText() + "'";
        statement = connection.createStatement();
        resultSet = statement.executeQuery(checkData);

        if (resultSet.next()) {
            BigDecimal price = FormatPrice.stringToBigDecimal(addProducts_price.getText());
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

            showAlert(INFORMATION, "Atualizado com sucesso");

            addProductsShowListData();
            addProductsClear();
        } else {
            showAlert(ERROR, "Produto com o código de barras: " + addProducts_barcode.getText() +
                    "não encontrado.");
        }
    }

    public void addProductsDelete() {
        if (addProductsFieldsAreEmpty()) {
            showAlert(ERROR, "Por favor, preencha todos os campos.");
            return;
        }
        String sql = "DELETE FROM product WHERE barcode = '" + addProducts_barcode.getText() + "'";

        connection = DatabaseManager.getConnection();

        try {
            String checkData = "SELECT barcode FROM product WHERE barcode = '" +
                    addProducts_barcode.getText() + "'";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(checkData);

            if (resultSet.next()) {
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Mensagem de confirmação");
                alert.setHeaderText(null);
                alert.setContentText("Tem certeza que deseja deletar o produto: "
                        + addProducts_barcode.getText() + "?");
                Optional<ButtonType> option = alert.showAndWait();

                if (option.get() == ButtonType.OK) {
                    statement = connection.createStatement();
                    statement.executeUpdate(sql);

                    showAlert(INFORMATION, "Deletado com sucesso");

                    addProductsClear();
                    addProductsShowListData();
                }
            } else {
                showAlert(ERROR, "Não existe um produto com o código de barras: " + addProducts_barcode.getText());
            }
        } catch (SQLException e) {
            showAlert(ERROR, e.getMessage());
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
            showAlert(ERROR, "Informe essa mensagem ao desenvolvedor: " + e.getMessage());
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

                    if (predicateProductData.getBarcode().contains(searchKey)) {
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
        addProducts_type.getSelectionModel().select(selectedProduct.getType());
        addProducts_brand.setText(selectedProduct.getBrand());
        addProducts_name.setText(selectedProduct.getName());
        addProducts_quantity.setText(String.valueOf(selectedProduct.getQuantity()));
        addProducts_price.setText(String.valueOf(selectedProduct.getPrice()));

        if (selectedProduct.getImage() != null && !selectedProduct.getImage().isBlank()) {
            image = new Image("file:" + selectedProduct.getImage(), 115, 128, false, true);
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

    // ORDERS PANE

    public void ordersReadBarcode() {
        if (orders_barcode.getText().isBlank()) return;

        String sqlProduct = "SELECT * FROM product WHERE barcode = '" + orders_barcode.getText() + "'";

        connection = DatabaseManager.getConnection();
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sqlProduct);
            if (resultSet.next()) {
                String productType = resultSet.getString("type");
                String productBrand = resultSet.getString("brand");
                String productName = resultSet.getString("name");
                BigDecimal productPrice = resultSet.getBigDecimal("price"); // clear

                setCustomerId();

                String sqlCheck = "SELECT * FROM customer " +
                        "WHERE customer_id = ? AND type = ? AND brand = ? AND productName = ?";
                try {
                    preparedStatement = connection.prepareStatement(sqlCheck);
                    preparedStatement.setInt(1, customerId);
                    preparedStatement.setString(2, productType);
                    preparedStatement.setString(3, productBrand);
                    preparedStatement.setString(4, productName);

                    resultSet = preparedStatement.executeQuery();

                    if (resultSet.next()) {
                        int newQuantity = resultSet.getInt("quantity") + 1;
                        BigDecimal newPrice = productPrice.multiply(new BigDecimal(newQuantity));

                        String sqlUpdate = "UPDATE customer SET quantity = ?, price = ? " +
                                "WHERE customer_id = ? AND type = ? AND brand = ? AND productName = ?";

                        preparedStatement = connection.prepareStatement(sqlUpdate);
                        preparedStatement.setInt(1, newQuantity);
                        preparedStatement.setBigDecimal(2, newPrice);
                        preparedStatement.setInt(3, customerId);
                        preparedStatement.setString(4, productType);
                        preparedStatement.setString(5, productBrand);
                        preparedStatement.setString(6, productName);

                        preparedStatement.executeUpdate();
                    } else {
                        String sqlInsert = "INSERT INTO customer (customer_id, type, brand, productName, quantity, price, date) " +
                                "VALUES(?, ?, ?, ?, ?, ?, ?)";

                        try {
                            preparedStatement = connection.prepareStatement(sqlInsert);
                            preparedStatement.setInt(1, customerId);
                            preparedStatement.setString(2, productType);
                            preparedStatement.setString(3, productBrand);
                            preparedStatement.setString(4, productName);
                            preparedStatement.setInt(5, 1);
                            preparedStatement.setBigDecimal(6, productPrice);
                            preparedStatement.setDate(7, new Date(System.currentTimeMillis()));
                            preparedStatement.executeUpdate();
                        } catch (Exception e) {
                            showAlert(ERROR, e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    showAlert(ERROR, e.getMessage());
                }

                ordersClear();
                showOrdersList();
            } else {
                showAlert(ERROR, "Nenhum produto encontrado com esse código de barras (" + orders_barcode.getText() + ")");
            }
        } catch (Exception e) {
            showAlert(ERROR, e.getMessage());
        }
    }

    public void ordersListName() {
        if (orders_nameField.getText().length() < 3) {
            showAlert(ERROR, "Nome do produto precisa ter ao menos 3 letras");
            return;
        }

        String sql = "SELECT name FROM product WHERE LOWER(name) LIKE LOWER(?)";
        connection = DatabaseManager.getConnection();

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, orders_nameField.getText() + "%");
            resultSet = preparedStatement.executeQuery();

            ObservableList<String> availableNames = FXCollections.observableArrayList();

            while (resultSet.next()) {
                availableNames.add(resultSet.getString("name"));
            }
            orders_name.setItems(availableNames);

            if (availableNames.isEmpty()) {
                showAlert(ERROR, "Nenhum produto encontrado com esse nome");
            }
        } catch (Exception e) {
            showAlert(ERROR, e.getMessage());
        }
    }

    private SpinnerValueFactory<Integer> orders_quantitySpinner;

    public void ordersSpinner() {
        orders_quantitySpinner = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0);

        orders_quantity.setValueFactory(orders_quantitySpinner);
    }

    public void ordersClear() {
        orders_barcode.clear();
        orders_name.getSelectionModel().clearSelection();
        orders_quantitySpinner.setValue(0);
    }

    private BigDecimal discount = BigDecimal.ZERO;

    public void ordersDiscount() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Aplicar desconto");
        dialog.setHeaderText(null);
        dialog.setContentText("Insira o desconto:");

        Optional<String> result = dialog.showAndWait();

        String discountText = result.orElse("");
        try {
            if (discountText.isBlank()) {
                return;
            }

            discount = FormatPrice.stringToBigDecimal(discountText).setScale(2, RoundingMode.HALF_UP);
            showTotalPrice();
        } catch (NumberFormatException nfe) {
            showAlert(ERROR, "Valor inserido incorreto.");
        } catch (Exception e) {
            showAlert(ERROR, e.getMessage());
        }
    }

    public void ordersDelete() {
        CustomerData selected = orders_table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(ERROR, "Primeiro selecione um item para remover");
            return;
        }

        String sql = "DELETE FROM customer " +
                "WHERE customer_id = ? " +
                "  AND type = ? " +
                "  AND brand = ? " +
                "  AND productName = ? " +
                "  AND quantity = ? " +
                "  AND price = ? " +
                "LIMIT 1";

        connection = DatabaseManager.getConnection();

        try {
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, selected.getCustomerId());
            preparedStatement.setString(2, selected.getType());
            preparedStatement.setString(3, selected.getBrand());
            preparedStatement.setString(4, selected.getProductName());
            preparedStatement.setInt(5, selected.getQuantity());
            preparedStatement.setBigDecimal(6, selected.getPrice());

            preparedStatement.executeUpdate();

            showTotalPrice();
            showOrdersList();
        } catch (Exception e) {
            showAlert(ERROR, e.getMessage());
        }
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
                        .multiply(new BigDecimal(orders_quantity.getValue())); //
            }

            if (orders_quantity.getValue() == null
                    || orders_name.getSelectionModel().getSelectedItem() == null || totalProductPrice.doubleValue() == 0) {
                showAlert(ERROR, "Por favor, escolha o produto primeiro.");
            } else {
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, String.valueOf(customerId));
                preparedStatement.setString(2, "orders_type.getSelectionModel().getSelectedItem()");
                preparedStatement.setString(3, "orders_brand.getSelectionModel().getSelectedItem()");
                preparedStatement.setString(4, orders_name.getSelectionModel().getSelectedItem());
                preparedStatement.setString(5, String.valueOf(orders_quantity.getValue()));
                preparedStatement.setString(6, totalProductPrice.toString());
                preparedStatement.setString(7, String.valueOf(new Date(System.currentTimeMillis())));

                preparedStatement.executeUpdate();

                showTotalPrice();
                showOrdersList();
            }
        } catch (Exception e) {
            showAlert(ERROR, e.getMessage());
        }
    }

    private BigDecimal totalPrice = BigDecimal.ZERO;

    public void showTotalPrice() {
        String sql = "SELECT SUM(price) FROM customer WHERE customer_id = '" + customerId + "'";

        connection = DatabaseManager.getConnection();

        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                totalPrice = new BigDecimal(resultSet.getDouble("SUM(price)"))
                        .setScale(2, RoundingMode.HALF_UP);
            }

            if (totalPrice.compareTo(discount) >= 0) {
                totalPrice = totalPrice.subtract(discount);
                orders_total.setText(FormatPrice.bigDecimalToString(totalPrice));
            }
        } catch (Exception e) {
            showAlert(ERROR, e.getMessage());
        }
    }

    public void ordersPay() {
        if (amountPrice == null) {
            showAlert(ERROR, "Pressione ENTER após inserir o valor recebido.");
            return;
        }

        setCustomerId();
        String sql = "INSERT INTO customer_receipt (customer_id,total,amount,balance,date) VALUES(?,?,?,?,?)";

        connection = DatabaseManager.getConnection();

        try {
            if (totalPrice.doubleValue() > 0 && !orders_amount.getText().isBlank() && amountPrice.doubleValue() > 0
                    && Double.parseDouble(orders_amount.getText()) > totalPrice.doubleValue()) {
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

                    showAlert(INFORMATION, "Mensagem de informação");

                    showOrdersList();
                    ordersClear();

                    totalPrice = BigDecimal.ZERO;
                    balance = BigDecimal.ZERO;
                    amountPrice = BigDecimal.ZERO;
                    discount = BigDecimal.ZERO;
                    orders_total.setText(FormatPrice.bigDecimalToString(BigDecimal.ZERO));
                    orders_amount.clear();
                    orders_balance.setText(FormatPrice.bigDecimalToString(BigDecimal.ZERO));
                }
            } else {
                showAlert(ERROR, "Preço ou valor recebido inválido.");
            }
        } catch (Exception e) {
            showAlert(ERROR, e.getMessage());
        }
    }

    private BigDecimal amountPrice;
    private BigDecimal balance;

    public void ordersAmount() {
        if (!orders_amount.getText().isEmpty()) {
            amountPrice = FormatPrice.stringToBigDecimal(orders_amount.getText());

            if (totalPrice.doubleValue() > 0) { // COMPARE
                if (amountPrice.compareTo(totalPrice) >= 0) {
                    balance = (amountPrice.subtract(totalPrice));
                    orders_balance.setText(FormatPrice.bigDecimalToString(balance));
                } else {
                    showAlert(ERROR, "Preço maior que valor recebido.");
                }
            } else {
                showAlert(ERROR, "Preço inválido (" + totalPrice + ")");
            }
        } else {
            showAlert(ERROR, "Valor recebido não inserido.");
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
            showAlert(ERROR, e.getMessage());
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
            showAlert(ERROR, e.getMessage());
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

    public void ordersClearAll() {
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

                ordersClear();

                orders_total.setText(FormatPrice.bigDecimalToString(BigDecimal.ZERO));
                orders_amount.clear();
                orders_balance.setText(FormatPrice.bigDecimalToString(BigDecimal.ZERO));
            } catch (Exception e) {
                showAlert(ERROR, e.getMessage());
            }
        }
    }

    public void ordersReceipt() {
        try {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Observação do pedido");
            dialog.setHeaderText("Adicionar observação (opcional)");
            dialog.setContentText("Insira a observação:");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String observationText = result.orElse("");

                HashMap<String, Object> parameters = new HashMap<>();
                parameters.put("ticketId", String.valueOf(customerId));
                parameters.put("subtotal", FormatPrice.bigDecimalToString(totalPrice.add(discount)));

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                parameters.put("date", String.valueOf(sdf.format(new java.util.Date(System.currentTimeMillis()))));

                parameters.put("sellerName", String.valueOf(GetData.name));
                parameters.put("discount", FormatPrice.bigDecimalToString(discount));
                parameters.put("totalPrice", FormatPrice.bigDecimalToString(totalPrice));
                parameters.put("observation", observationText);

                JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(ordersList);

                InputStream reportStream = getClass().getResourceAsStream("receipt.jrxml");

                JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

                JasperViewer.viewReport(jasperPrint, false);
            }
        } catch (Exception e) {
            showAlert(ERROR, e.getMessage());
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
            showAlert(ERROR, e.getMessage());
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
        ordersSpinner();
    }
}
