package com.example.i_connect;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.*;

import java.io.IOException;

public class Dashboard {
    @FXML
    public Button ChatButton;
    @FXML
    public Button NotesButton;
    @FXML
    public ImageView LogOutHandle;
    @FXML
    public Text Roll;
    @FXML
    public Text UserEmail;
    @FXML
    public Text Institution;
    @FXML
    public Text FullName;
    @FXML
    public Text UserName;
    @FXML
    private String currentUserEmail;
    @FXML
    public Button ResourceButton;

    public void setUserEmail(String page) {
        this.currentUserEmail = page;
    }
    public void GoNotesPage(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("KeepNotes.fxml"));
            Parent root = loader.load();
            KeepNotes controller = loader.getController();
            controller.setUserEmail(currentUserEmail);
            Stage stage = (Stage) ResourceButton.getScene().getWindow();

            // Set new scene
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("Notes");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void GoResourcesPage(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ResourcePage.fxml"));
            Parent root = loader.load();
            ResourcePage controller = loader.getController();
            //controller.setUserEmail(currentUserEmail);
            Stage stage = (Stage) ResourceButton.getScene().getWindow();

            // Set new scene
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("Resources");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void GoChat(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("chat_page.fxml"));
            Parent root = loader.load();
            ChatPage controller = loader.getController();
            //controller.setUserEmail(currentUserEmail);
            Stage stage = (Stage) ResourceButton.getScene().getWindow();

            // Set new scene
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("Chat");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        LogOutHandle.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("welcome_page.fxml"));
                    Parent root = loader.load();
                    WelcomePage controller = loader.getController();
                    Stage stage = (Stage) ResourceButton.getScene().getWindow();

                    // Set new scene
                    stage.setScene(new javafx.scene.Scene(root));
                    stage.setTitle("i connect");
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
