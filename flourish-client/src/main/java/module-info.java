module flourish.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;
    requires javafx.swing;
    requires javafx.web;
    requires java.net.http;
    requires java.mail;
    requires jbcrypt;
    requires com.fasterxml.jackson.databind;
    requires mysql.connector.j;
    requires flourish.shared;
    requires java.sql;


    exports com.flourish.client.controller;
    exports com.flourish.client.model;
    exports com.flourish.client.view;
    exports com.flourish.client.service;

    opens com.flourish.client.controller to javafx.fxml;
}