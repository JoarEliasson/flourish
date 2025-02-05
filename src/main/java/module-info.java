module se.myhappyplants {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;
    requires javafx.swing;
    requires javafx.web;
    requires com.google.gson;
    requires jbcrypt;
    requires com.fasterxml.jackson.databind;
    requires java.net.http;
    requires java.sql;

    exports se.myhappyplants.client.controller;
    exports se.myhappyplants.client.model;
    exports se.myhappyplants.client.view;
    exports se.myhappyplants.server;
    exports se.myhappyplants.server.controller;
    exports se.myhappyplants.server.services;
    exports se.myhappyplants.server.config;
    exports se.myhappyplants.server.db;
    exports se.myhappyplants.api;

    opens se.myhappyplants.client.controller to javafx.fxml;
    opens se.myhappyplants.api to com.fasterxml.jackson.databind;
}