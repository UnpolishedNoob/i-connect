package com.example.i_connect;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class KeepNotes {
    @FXML
    private ListView<Note> notesListView;

    @FXML
    private ImageView BackToDashboard;


    private static final String DB_URL = "jdbc:mysql://localhost:3306/student";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "2107052@Kuet";

    private String currentUserEmail;

    private ObservableList<Note> notesList;

    @FXML
    public void initialize() {
        notesList = FXCollections.observableArrayList();
        notesListView.setItems(notesList);

        notesListView.setCellFactory(param -> new NoteListCell());

        notesListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Note selectedNote = notesListView.getSelectionModel().getSelectedItem();
                if (selectedNote != null) {
                    editNote(selectedNote);
                }
            }
        });

        ContextMenu contextMenu = new ContextMenu();

        MenuItem editItem = new MenuItem("Edit Note");
        editItem.setOnAction(event -> {
            Note selectedNote = notesListView.getSelectionModel().getSelectedItem();
            if (selectedNote != null) {
                editNote(selectedNote);
            }
        });

        MenuItem deleteItem = new MenuItem("Delete Note");
        deleteItem.setOnAction(event -> deleteSelectedNote());

        contextMenu.getItems().addAll(editItem, deleteItem);
        notesListView.setContextMenu(contextMenu);
    }

    public void setUserEmail(String email) {
        this.currentUserEmail = email;
        loadNotesFromDatabase();
    }

    @FXML
    public void addNewNote() {
        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
            showAlert("Error", "User not logged in!", Alert.AlertType.ERROR);
            return;
        }

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Add New Note");
        dialog.setHeaderText("Enter Note Content");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextArea noteContentArea = new TextArea();
        noteContentArea.setWrapText(true);
        noteContentArea.setPromptText("Enter your note here...");

        dialog.getDialogPane().setContent(noteContentArea);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return noteContentArea.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(content -> {
            if (!content.trim().isEmpty()) {
                saveNoteToDatabase(content);
            }
        });
    }

    private void editNote(Note note) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Edit Note");
        dialog.setHeaderText("Modify Note Content");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextArea noteContentArea = new TextArea(note.getContent());
        noteContentArea.setWrapText(true);

        dialog.getDialogPane().setContent(noteContentArea);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return noteContentArea.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(content -> {
            if (!content.trim().isEmpty()) {
                updateNoteInDatabase(note.getId(), content);
            }
        });
    }

    private void deleteSelectedNote() {
        Note selectedNote = notesListView.getSelectionModel().getSelectedItem();
        if (selectedNote == null) {
            showAlert("Error", "No note selected to delete.", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Delete Note");
        confirmDialog.setHeaderText("Are you sure you want to delete this note?");
        confirmDialog.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteNoteFromDatabase(selectedNote.getId());
        }
    }

    private void deleteNoteFromDatabase(String noteId) {
        String deleteQuery = "DELETE FROM notes WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {

            pstmt.setString(1, noteId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                notesList.removeIf(note -> note.getId().equals(noteId));
            } else {
                showAlert("Error", "Failed to delete note.", Alert.AlertType.ERROR);
            }

        } catch (SQLException e) {
            showAlert("Database Error", "Could not delete note: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void loadNotesFromDatabase() {
        notesList.clear();
        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
            return;
        }

        String query = "SELECT id, content, created_at FROM notes WHERE user_email = ? ORDER BY created_at DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, currentUserEmail);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Note note = new Note(
                            rs.getString("id"),
                            rs.getString("content"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );
                    notesList.add(note);
                }
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Could not load notes: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void saveNoteToDatabase(String content) {
        String insertQuery = "INSERT INTO notes (id, user_email, content, created_at) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

            String noteId = generateUniqueId();
            pstmt.setString(1, noteId);
            pstmt.setString(2, currentUserEmail);
            pstmt.setString(3, content);
            pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                Note newNote = new Note(noteId, content, LocalDateTime.now());
                notesList.add(0, newNote);
            }

        } catch (SQLException e) {
            showAlert("Database Error", "Could not save note: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void updateNoteInDatabase(String noteId, String newContent) {
        String updateQuery = "UPDATE notes SET content = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

            pstmt.setString(1, newContent);
            pstmt.setString(2, noteId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                for (Note note : notesList) {
                    if (note.getId().equals(noteId)) {
                        note.setContent(newContent);
                        notesListView.refresh();
                        break;
                    }
                }
            }

        } catch (SQLException e) {
            showAlert("Database Error", "Could not update note: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    public void backToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
            Parent root = loader.load();

            Dashboard controller = loader.getController();
            controller.setUserEmail(currentUserEmail);
            Stage stage = (Stage) BackToDashboard.getScene().getWindow();

            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("i connect");
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            showAlert("Navigation Error", "Failed to load dashboard.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private String generateUniqueId() {
        return "NOTE_" + System.currentTimeMillis() + "_" +
                Math.round(Math.random() * 1000);
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static class Note {
        private String id;
        private String content;
        private LocalDateTime createdAt;

        public Note(String id, String content, LocalDateTime createdAt) {
            this.id = id;
            this.content = content;
            this.createdAt = createdAt;
        }

        public String getId() { return id; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public LocalDateTime getCreatedAt() { return createdAt; }

        @Override
        public String toString() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            return content + "\n(Created: " + createdAt.format(formatter) + ")";
        }
    }

    private static class NoteListCell extends javafx.scene.control.ListCell<Note> {
        @Override
        protected void updateItem(Note note, boolean empty) {
            super.updateItem(note, empty);

            if (empty || note == null) {
                setText(null);
                setGraphic(null);
            } else {
                setText(note.toString());
            }
        }
    }
}