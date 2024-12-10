package com.example.i_connect;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class SignupPage {
    @FXML
    private TextField SignUpFullName;

    @FXML
    private TextField SignUpUsername;

    @FXML
    private TextField SignUpInst;

    @FXML
    private TextField SignUpRoll;

    @FXML
    private TextField SignUpEmail;

    @FXML
    private PasswordField SignUpPass;

    @FXML
    private PasswordField ReEnterPass;

    @FXML
    private CheckBox SignUpCheckbox;

    @FXML
    private Button SignUpButton;

    @FXML
    private ImageView SignUpBack;

    // Database connection parameters (replace with your actual database details)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/student";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "2107052@Kuet";

    @FXML
    public void SignUpButtonClicked(MouseEvent event) {
        if (!validateInputs()) {
            System.out.println("not valid ! ");
            return;
        }

        try {
            if (registerUser()) {
                navigateToLogin();
            }
        } catch (SQLException e) {
            showErrorDialog("Registration Failed", "An error occurred: " + e.getMessage());
        }
    }

    @FXML
    public void SignUpBackClicked(MouseEvent event) {
        try {
            FXMLLoader Loader = new FXMLLoader(getClass().getResource("welcome_page.fxml"));
            Parent root = Loader.load();
            Stage stage = (Stage) SignUpBack.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("i connect");
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            System.out.println("Failed to Load !");
            throw new RuntimeException(e);
        }
    }

    private boolean validateInputs() {
        if (SignUpFullName.getText().isEmpty()) {
            showErrorDialog("Validation Error", "Full Name cannot be empty");
            return false;
        }

        if (SignUpUsername.getText().isEmpty() || SignUpUsername.getText().length() < 3) {
            showErrorDialog("Validation Error", "Username must be at least 3 characters long");
            return false;
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!Pattern.matches(emailRegex, SignUpEmail.getText())) {
            showErrorDialog("Validation Error", "Invalid email format");
            return false;
        }

        if (SignUpPass.getText().isEmpty() || SignUpPass.getText().length() < 8) {
            showErrorDialog("Validation Error", "Password must be at least 8 characters long");
            return false;
        }

        if (!SignUpPass.getText().equals(ReEnterPass.getText())) {
            showErrorDialog("Validation Error", "Passwords do not match");
            return false;
        }

        if (!SignUpCheckbox.isSelected()) {
            showErrorDialog("Validation Error", "You must agree to the Terms and Conditions");
            return false;
        }

        return true;
    }

    private boolean registerUser() throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO users (full_name, username, institution, roll_number, email, password) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, SignUpFullName.getText());
            statement.setString(2, SignUpUsername.getText());
            statement.setString(3, SignUpInst.getText());
            statement.setString(4, SignUpRoll.getText());
            statement.setString(5, SignUpEmail.getText());

            statement.setString(6, SignUpPass.getText());

            int rowsInserted = statement.executeUpdate();

            return rowsInserted > 0;
        }
    }

    private void navigateToLogin() {
        try {
            FXMLLoader Loader = new FXMLLoader(getClass().getResource("welcome_page.fxml"));
            Parent root = Loader.load();
            Stage stage = (Stage) SignUpButton.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("i connect");
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            System.out.println("Failed to Load !");
            throw new RuntimeException(e);
        }
    }

    private void showErrorDialog(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}