package com.example.i_connect;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class Dashboard {
    private String currentUserEmail;
    public Button ResourceButton;

    public void setUserEmail(String page) {
        this.currentUserEmail = page;
    }
    public void GoResourcesPage(MouseEvent mouseEvent) {
        try {
            // Load dashboard page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("KeepNotes.fxml"));
            Parent root = loader.load();
            KeepNotes controller = loader.getController();
            controller.setUserEmail(currentUserEmail);
            // Get current stage
            Stage stage = (Stage) ResourceButton.getScene().getWindow();

            // Set new scene
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("Resources");
            stage.show();
        } catch (IOException e) {
            //ErrorMessage.setText("Failed to load dashboard.");
            e.printStackTrace();
        }
    }
}
