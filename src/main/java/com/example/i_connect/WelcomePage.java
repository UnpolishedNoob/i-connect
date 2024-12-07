package com.example.i_connect;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;

public class WelcomePage {
    @FXML
    private TextField SignInEmail;
    @FXML
    private PasswordField SignInPassword;
    @FXML
    private Label ErrorMessage;

    public void handle_signin(){
        String Email = SignInEmail.getText();
        String Password = SignInPassword.getText();
        if(Email.isEmpty()||Password.isEmpty()){
            ErrorMessage.setText("Please enter both email and password.");
        }else{
            try {
                FXMLLoader Loader=new FXMLLoader(getClass().getResource("Dashboard.fxml"));
                Parent root=Loader.load();
                Stage stage=(Stage) SignInEmail.getScene().getWindow();
                stage.setScene(new javafx.scene.Scene(root));
                stage.setTitle("Dashboard");
                stage.show();
            } catch (IOException e) {
                System.out.println("Failed to Load !");
                throw new RuntimeException(e);
            }
        }
    }

    public void handle_register_here(MouseEvent mouseEvent) {
        try {
            FXMLLoader Loader=new FXMLLoader(getClass().getResource("signup_page.fxml"));
            Parent root=Loader.load();
            Stage stage=(Stage) SignInEmail.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("Sign Up");
            stage.show();
        } catch (IOException e) {
            System.out.println("Failed to Load !");
            throw new RuntimeException(e);
        }
    }
}
