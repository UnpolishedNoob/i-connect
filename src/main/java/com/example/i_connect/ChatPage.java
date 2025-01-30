package com.example.i_connect;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChatPage implements Initializable {
    @FXML
    public ImageView backtodashboard;
    @FXML
    public ImageView SendText;


    @FXML
    private TextField InputText;

    @FXML
    private ScrollPane Scrollmsg;

    @FXML
    private VBox ShowMsg;

    private String currentUserEmail;
    private String currentUsername;
    private int lastMessageId = 0;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/student";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "2107052@Kuet";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        retrieveCurrentUserInfo();

        SendText.setOnMouseClicked(event -> sendMessage());

        InputText.setOnAction(event -> sendMessage());

        ShowMsg.heightProperty().addListener((observable, oldValue, newValue) ->
                Scrollmsg.setVvalue(1.0));

        startMessagePolling();
    }

    public void setUserEmail(String page) {
        this.currentUserEmail = page;
    }

    private void retrieveCurrentUserInfo() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT email, username FROM users LIMIT 1")) {

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    currentUserEmail = rs.getString("email");
                    currentUsername = rs.getString("username");
                } else {
                    showAlert("Login Error", "No user found.");
                }
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to retrieve user information: " + e.getMessage());
        }
    }

    @FXML
    private void sendMessage() {
        String messageContent = InputText.getText().trim();
        if (!messageContent.isEmpty() && currentUserEmail != null) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(
                         "INSERT INTO messages (sender_email, content, timestamp) VALUES (?, ?, ?)")) {

                pstmt.setString(1, currentUserEmail);
                pstmt.setString(2, messageContent);
                pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                pstmt.executeUpdate();

                InputText.clear();
            } catch (SQLException e) {
                showAlert("Send Message Error", "Failed to send message: " + e.getMessage());
            }
        }
    }

    private void startMessagePolling() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::fetchNewMessages, 0, 2, TimeUnit.SECONDS);
    }

    private void fetchNewMessages() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT m.id, m.sender_email, u.username, m.content, m.timestamp " +
                             "FROM messages m " +
                             "JOIN users u ON m.sender_email = u.email " +
                             "WHERE m.id > ? ORDER BY m.id")) {

            pstmt.setInt(1, lastMessageId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int messageId = rs.getInt("id");
                    String senderEmail = rs.getString("sender_email");
                    String senderUsername = rs.getString("username");
                    String content = rs.getString("content");
                    LocalDateTime timestamp = rs.getTimestamp("timestamp").toLocalDateTime();

                    Platform.runLater(() -> displayMessage(senderUsername, content, timestamp));

                    lastMessageId = Math.max(lastMessageId, messageId);
                }
            }
        } catch (SQLException e) {
            Platform.runLater(() ->
                    showAlert("Fetch Messages Error", "Failed to retrieve messages: " + e.getMessage())
            );
        }
    }

    private void displayMessage(String username, String content, LocalDateTime timestamp) {
        HBox messageBox = new HBox();
        messageBox.setSpacing(10);
        messageBox.setPadding(new Insets(5));

        Label usernameLabel = new Label(username + ":");
        usernameLabel.setStyle("-fx-font-weight: bold;");

        Text messageText = new Text(content);
        TextFlow textFlow = new TextFlow(messageText);
        textFlow.setStyle("-fx-background-color: #F0F0F0; -fx-background-radius: 10; -fx-padding: 5px;");

        Label timestampLabel = new Label(
                timestamp.format(DateTimeFormatter.ofPattern("HH:mm"))
        );
        timestampLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");

        messageBox.getChildren().addAll(usernameLabel, textFlow, timestampLabel);
        messageBox.setAlignment(Pos.CENTER_LEFT);

        ShowMsg.getChildren().add(messageBox);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void GoDashboard(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
            Parent root = loader.load();
            Dashboard controller = loader.getController();
            controller.setUserEmail(currentUserEmail);
            Stage stage = (Stage) SendText.getScene().getWindow();

            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("i connect");
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


//Project details

//App name : i_connect
//Build by : Pritom Banik