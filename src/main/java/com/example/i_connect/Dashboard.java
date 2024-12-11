package com.example.i_connect;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.*;

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
    public Pane ProfilePic;
    @FXML
    public Text Quotes;
    @FXML
    public Text Writer;
    @FXML
    private String currentUserEmail;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/student";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "2107052@Kuet";

    public void setUserEmail(String page) {
        this.currentUserEmail = page;
        try {
            ProfileSet(page);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void ProfileSet(String email) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT full_name, username, institution, roll_number FROM users WHERE email = ?";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, email);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        FullName.setText(resultSet.getString("full_name"));
                        UserName.setText(resultSet.getString("username"));
                        Institution.setText(resultSet.getString("institution"));
                        Roll.setText(String.valueOf(resultSet.getInt("roll_number")));
                        UserEmail.setText(currentUserEmail);
                    }
                }
            }
        }
    }

    public void GoNotesPage(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("KeepNotes.fxml"));
            Parent root = loader.load();
            KeepNotes controller = loader.getController();
            controller.setUserEmail(currentUserEmail);
            Stage stage = (Stage) ChatButton.getScene().getWindow();

            // Set new scene
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("i connect");
            stage.setResizable(false);
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
            controller.setUserEmail(currentUserEmail);
            Stage stage = (Stage) ChatButton.getScene().getWindow();

            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("i connect");
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static final String API_KEY = "LKzs2TeGe01h7Z6BQF+CVQ==5P7SSMYP8X1PEsmN";
    private static final String API_URL = "https://api.api-ninjas.com/v1/quotes";


    @FXML
    public void initialize() {
        LogOutHandle.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("welcome_page.fxml"));
                    Parent root = loader.load();
                    WelcomePage controller = loader.getController();
                    Stage stage = (Stage) ChatButton.getScene().getWindow();

                    stage.setScene(new javafx.scene.Scene(root));
                    stage.setTitle("i connect");
                    stage.setResizable(false);
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("X-Api-Key", API_KEY)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            jsonParse(response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void jsonParse(String response) {
        JsonArray jsonArray = JsonParser.parseString(response).getAsJsonArray();
        StringBuilder stringBuilder = new StringBuilder();

        JsonObject quoteObject = jsonArray.get(0).getAsJsonObject();
        String quote = quoteObject.get("quote").getAsString();
        String author = quoteObject.get("author").getAsString();

        Quotes.setText(quote);
        Writer.setText("- "+author);

    }
}
