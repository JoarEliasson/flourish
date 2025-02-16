module flourish.server {
    requires com.fasterxml.jackson.databind;
    requires jakarta.mail;
    requires java.net.http;
    requires jbcrypt;
    requires flourish.shared;
    requires java.sql;
    requires mysql.connector.j;

    exports com.flourish.server;
    exports com.flourish.server.controller;
    exports com.flourish.server.services;
    exports com.flourish.server.config;
    exports com.flourish.server.db;
    exports com.flourish.api;

    opens com.flourish.api to com.fasterxml.jackson.databind;
}