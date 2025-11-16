package com.br.renzoluigi.ciprianjospdv;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;

public class LoginController {
    @FXML
    private AnchorPane mainForm;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password; //password field
    @FXML
    private Button loginButton;
    @FXML
    private Button close;

    private double y = 0;
    private double x = 0;

    // database
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    public void login() {
        String sql = "SELECT * FROM admin WHERE username = ?  and  password = ?";
        connection = DatabaseManager.getConnection();
        Alert alert;
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username.getText());
            preparedStatement.setString(2, password.getText());
            resultSet = preparedStatement.executeQuery();
            if (username.getText().isBlank() || password.getText().isBlank()) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Mensagem de Erro");
                alert.setHeaderText(null);
                alert.setContentText("Por favor, preencha todos os campos.");
                alert.showAndWait();
            } else {
                if (resultSet.next()) {
                    GetData.name = resultSet.getString("name");

                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Mensagem de informação");
                    alert.setHeaderText(null);
                    alert.setContentText("Login bem sucedido!");
                    alert.showAndWait();

                    loginButton.getScene().getWindow().hide();

                    Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("dashboard.fxml")));
                    Stage dashboardStage = new Stage();
                    Scene dashboardScene = new Scene(root);

                    root.setOnMousePressed(mouseEvent -> {
                        x = mouseEvent.getSceneX();
                        y = mouseEvent.getSceneY();
                    });

                    root.setOnMouseDragged(mouseEvent -> {
                        dashboardStage.setX(mouseEvent.getScreenX() - x);
                        dashboardStage.setY(mouseEvent.getScreenY() - y);
                    });

                    root.setOnMouseReleased(_ -> {
                        dashboardStage.setOpacity(1);
                    });

                    dashboardStage.initStyle(StageStyle.TRANSPARENT);

                    dashboardStage.setScene(dashboardScene);
                    dashboardStage.show();
                } else {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Mensagem de erro");
                    alert.setHeaderText(null);
                    alert.setContentText("Nome de usuário ou senha incorretos.");
                    alert.showAndWait();
                }
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

    public void close(ActionEvent event) {
        System.exit(0); // mainForm.getScene().getWindow().close()
    }
}
