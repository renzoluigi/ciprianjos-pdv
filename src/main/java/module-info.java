module com.br.renzoluigi.ciprianjospdv {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires java.sql;
    requires com.h2database;
    requires jasperreports;

    opens com.br.renzoluigi.ciprianjospdv to javafx.fxml;
    exports com.br.renzoluigi.ciprianjospdv;
    exports com.br.renzoluigi.ciprianjospdv.db;
    opens com.br.renzoluigi.ciprianjospdv.db to javafx.fxml;
    exports com.br.renzoluigi.ciprianjospdv.util;
    opens com.br.renzoluigi.ciprianjospdv.util to javafx.fxml;
    exports com.br.renzoluigi.ciprianjospdv.dto;
    opens com.br.renzoluigi.ciprianjospdv.dto to javafx.fxml;
}