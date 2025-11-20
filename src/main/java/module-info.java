module com.br.renzoluigi.ciprianjospdv {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires net.synedra.validatorfx;
    requires eu.hansolo.tilesfx;
    requires org.kordamp.ikonli.fontawesome5;
    requires org.kordamp.ikonli.javafx;
    requires java.sql;
    requires com.h2database;
    requires jasperreports;

    opens com.br.renzoluigi.ciprianjospdv to javafx.fxml;
    exports com.br.renzoluigi.ciprianjospdv;
}