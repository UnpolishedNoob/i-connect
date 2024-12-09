package com.example.i_connect;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader Loader=new FXMLLoader(getClass().getResource("Welcome_page.fxml"));
        stage.setScene(new Scene(Loader.load()));
        stage.setTitle("Hello!");
        stage.show();
    }

    public static void main(String[] args) {launch();}
}