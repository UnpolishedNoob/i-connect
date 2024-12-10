package com.example.i_connect;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WelcomePage {
    @FXML
    private TextField SignInEmail;

    @FXML
    private PasswordField SignInPassword;

    @FXML
    private Label ErrorMessage;

    @FXML
    private Label signup_page;

    @FXML
    private Button signin;

    private String UserEmail;
    private String UserPassword;

    // Database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/student";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "2107052@Kuet";

    @FXML
    public void handle_signin() {
        String email = SignInEmail.getText();
        String password = SignInPassword.getText();

        // Clear previous error messages
        ErrorMessage.setText("");

        // Validate input
        if (email.isEmpty() || password.isEmpty()) {
            ErrorMessage.setText("Please enter both email and password.");
            return;
        }

        // Authenticate user
        try {
            if (authenticateUser(email, password)) {
                // Navigate to dashboard
                UserEmail=email;
                UserPassword=password;
                navigateToDashboard();
            } else {
                ErrorMessage.setText("              Invalid email or password.");
            }
        } catch (SQLException e) {
            ErrorMessage.setText("Database error. Please try again.");
            e.printStackTrace();
        }
    }

    private boolean authenticateUser(String email, String password) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM users WHERE email = ? AND password = ?";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, email);
                statement.setString(2, password);

                try (ResultSet resultSet = statement.executeQuery()) {
                    return resultSet.next();
                }
            }
        }
    }

    @FXML
    public void handle_register_here(MouseEvent event) {
        try {
            // Load signup page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("signup_page.fxml"));
            Parent root = loader.load();

            // Get current stage
            Stage stage = (Stage) signup_page.getScene().getWindow();

            // Set new scene
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("Sign Up");
            stage.show();
        } catch (IOException e) {
            System.out.println("Failed to load signup page!");
            e.printStackTrace();
        }
    }

    private void navigateToDashboard() {
        try {
            // Load dashboard page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
            Parent root = loader.load();

            Dashboard controller = loader.getController();
            controller.setUserEmail(UserEmail);

            // Get current stage
            Stage stage = (Stage) signin.getScene().getWindow();

            // Set new scene
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            ErrorMessage.setText("Failed to load dashboard.");
            e.printStackTrace();
        }
    }
    public String getUserEmail(){
        return UserEmail;
    }
    public String getUserPassword(){
        return UserPassword;
    }
}