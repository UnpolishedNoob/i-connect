//Project details

//App name : i_connect
//Build by : Pritom Banik

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
        FXMLLoader Loader=new FXMLLoader(getClass().getResource("welcome_page.fxml"));
        stage.setScene(new Scene(Loader.load()));
        stage.setTitle("i connect");
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {launch();}
}