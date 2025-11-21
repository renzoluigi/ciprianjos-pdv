package com.br.renzoluigi.ciprianjospdv;

import com.br.renzoluigi.ciprianjospdv.db.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

public class CiprianjosApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        DatabaseManager.startH2ConsoleServer();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login.fxml")));
        Scene scene = new Scene(root);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}