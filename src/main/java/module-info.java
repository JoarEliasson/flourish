module se.myhappyplants {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;
    requires javafx.swing;
    requires javafx.web;
    requires com.google.gson;
    requires com.microsoft.sqlserver.jdbc;
    requires java.sql;
    requires jbcrypt;

    exports se.myhappyplants.client.controller;
    exports se.myhappyplants.client.model;
    exports se.myhappyplants.client.view;
    exports se.myhappyplants.server;
    exports se.myhappyplants.server.controller;
    exports se.myhappyplants.server.services;

    opens se.myhappyplants.client.controller to javafx.fxml;
  exports se.myhappyplants.server.config;
    exports se.myhappyplants.server.db;
  exports se.myhappyplants.server.db.refactored;
}