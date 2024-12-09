//package com.example.i_connect;
//
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.control.Alert;
//import javafx.scene.control.ListView;
//import javafx.scene.control.TextInputDialog;
//import javafx.scene.image.ImageView;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//import java.sql.*;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.Optional;
//
//public class KeepNotes {
//    @FXML
//    private ListView<Note> notesListView;
//
//    @FXML
//    private ImageView BackToDashboard;
//
//    // Database connection details
//    private static final String DB_URL = "jdbc:mysql://localhost:3306/student";
//    private static final String DB_USER = "root";  // Replace with your MySQL username
//    private static final String DB_PASSWORD = "2107052@Kuet";  // Replace with your MySQL password
//
//    // Current user's email
//    private String currentUserEmail;
//
//    // Observable list to hold notes
//    private ObservableList<Note> notesList;
//
//    @FXML
//    public void initialize() {
//        // Initialize the notes list
//        notesList = FXCollections.observableArrayList();
//        notesListView.setItems(notesList);
//
//        // Custom cell factory to display notes nicely
//        notesListView.setCellFactory(param -> new NoteListCell());
//
//        // Double-click to edit note
//        notesListView.setOnMouseClicked(event -> {
//            if (event.getClickCount() == 2) {
//                Note selectedNote = notesListView.getSelectionModel().getSelectedItem();
//                if (selectedNote != null) {
//                    editNote(selectedNote);
//                }
//            }
//        });
//    }
//
//    // Method to set current user's email when navigating from login/dashboard
//    public void setUserEmail(String page) {
//        this.currentUserEmail = page;
//        loadNotesFromDatabase();
//    }
//
//    @FXML
//    public void addNewNote() {
//        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
//            showAlert("Error", "User not logged in!");
//            return;
//        }
//
//        TextInputDialog dialog = new TextInputDialog();
//        dialog.setTitle("New Note");
//        dialog.setHeaderText("Create a New Note");
//        dialog.setContentText("Enter note content:");
//
//        Optional<String> result = dialog.showAndWait();
//        result.ifPresent(content -> {
//            if (!content.trim().isEmpty()) {
//                saveNoteToDatabase(content);
//            }
//        });
//    }
//
//    private void editNote(Note note) {
//        TextInputDialog dialog = new TextInputDialog(note.getContent());
//        dialog.setTitle("Edit Note");
//        dialog.setHeaderText("Edit Your Note");
//        dialog.setContentText("Edit note content:");
//
//        Optional<String> result = dialog.showAndWait();
//        result.ifPresent(content -> {
//            if (!content.trim().isEmpty()) {
//                updateNoteInDatabase(note.getId(), content);
//            }
//        });
//    }
//
//    private void loadNotesFromDatabase() {
//        notesList.clear();
//        String query = "SELECT id, content, created_at FROM notes WHERE user_email = ? ORDER BY created_at DESC";
//
//        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//             PreparedStatement pstmt = conn.prepareStatement(query)) {
//
//            pstmt.setString(1, currentUserEmail);
//
//            try (ResultSet rs = pstmt.executeQuery()) {
//                while (rs.next()) {
//                    Note note = new Note(
//                            rs.getString("id"),
//                            rs.getString("content"),
//                            rs.getTimestamp("created_at").toLocalDateTime()
//                    );
//                    notesList.add(note);
//                }
//            }
//        } catch (SQLException e) {
//            showAlert("Database Error", "Could not load notes: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    private void saveNoteToDatabase(String content) {
//        String insertQuery = "INSERT INTO notes (id, user_email, content, created_at) VALUES (?, ?, ?, ?)";
//
//        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//             PreparedStatement pstmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
//
//            String noteId = generateUniqueId();
//            pstmt.setString(1, noteId);
//            pstmt.setString(2, currentUserEmail);
//            pstmt.setString(3, content);
//            pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
//
//            int affectedRows = pstmt.executeUpdate();
//
//            if (affectedRows > 0) {
//                Note newNote = new Note(noteId, content, LocalDateTime.now());
//                notesList.add(0, newNote);
//            }
//
//        } catch (SQLException e) {
//            showAlert("Database Error", "Could not save note: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    private void updateNoteInDatabase(String noteId, String newContent) {
//        String updateQuery = "UPDATE notes SET content = ? WHERE id = ?";
//
//        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
//
//            pstmt.setString(1, newContent);
//            pstmt.setString(2, noteId);
//
//            int affectedRows = pstmt.executeUpdate();
//
//            if (affectedRows > 0) {
//                // Update the note in the list view
//                for (Note note : notesList) {
//                    if (note.getId().equals(noteId)) {
//                        note.setContent(newContent);
//                        notesListView.refresh();
//                        break;
//                    }
//                }
//            }
//
//        } catch (SQLException e) {
//            showAlert("Database Error", "Could not update note: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    @FXML
//    public void backToDashboard() {
//        try {
//            // Load dashboard page
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
//            Parent root = loader.load();
//            Dashboard controller = loader.getController();
//            controller.setUserEmail(currentUserEmail);
//            // Get current stage
//            Stage stage = (Stage) BackToDashboard.getScene().getWindow();
//
//            // Set new scene
//            stage.setScene(new javafx.scene.Scene(root));
//            stage.setTitle("Dashboard");
//            stage.show();
//        } catch (IOException e) {
//            //ErrorMessage.setText("Failed to load dashboard.");
//            e.printStackTrace();
//        }
//    }
//
//    private String generateUniqueId() {
//        return "NOTE_" + System.currentTimeMillis() + "_" +
//                Math.round(Math.random() * 1000);
//    }
//
//    private void showAlert(String title, String content) {
//        Alert alert = new Alert(Alert.AlertType.ERROR);
//        alert.setTitle(title);
//        alert.setHeaderText(null);
//        alert.setContentText(content);
//        alert.showAndWait();
//    }
//
//    // Note class to represent individual notes
//    public static class Note {
//        private String id;
//        private String content;
//        private LocalDateTime createdAt;
//
//        public Note(String id, String content, LocalDateTime createdAt) {
//            this.id = id;
//            this.content = content;
//            this.createdAt = createdAt;
//        }
//
//        // Getters and setters
//        public String getId() { return id; }
//        public String getContent() { return content; }
//        public void setContent(String content) { this.content = content; }
//        public LocalDateTime getCreatedAt() { return createdAt; }
//
//        @Override
//        public String toString() {
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//            return content + "\n(Created: " + createdAt.format(formatter) + ")";
//        }
//    }
//
//    // Custom cell factory for list view
//    private static class NoteListCell extends javafx.scene.control.ListCell<Note> {
//        @Override
//        protected void updateItem(Note note, boolean empty) {
//            super.updateItem(note, empty);
//
//            if (empty || note == null) {
//                setText(null);
//                setGraphic(null);
//            } else {
//                setText(note.toString());
//            }
//        }
//    }
//}


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

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/student";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "2107052@Kuet";

    // Current user's email
    private String currentUserEmail;

    // Observable list to hold notes
    private ObservableList<Note> notesList;

    @FXML
    public void initialize() {
        // Initialize the notes list
        notesList = FXCollections.observableArrayList();
        notesListView.setItems(notesList);

        // Custom cell factory to display notes nicely
        notesListView.setCellFactory(param -> new NoteListCell());

        // Double-click to edit note
        notesListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Note selectedNote = notesListView.getSelectionModel().getSelectedItem();
                if (selectedNote != null) {
                    editNote(selectedNote);
                }
            }
        });

        // Context menu for right-click options
        ContextMenu contextMenu = new ContextMenu();

        // Edit menu item
        MenuItem editItem = new MenuItem("Edit Note");
        editItem.setOnAction(event -> {
            Note selectedNote = notesListView.getSelectionModel().getSelectedItem();
            if (selectedNote != null) {
                editNote(selectedNote);
            }
        });

        // Delete menu item
        MenuItem deleteItem = new MenuItem("Delete Note");
        deleteItem.setOnAction(event -> deleteSelectedNote());

        // Add items to context menu
        contextMenu.getItems().addAll(editItem, deleteItem);
        notesListView.setContextMenu(contextMenu);
    }

    // Method to set current user's email when navigating from login/dashboard
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

        // Create a custom dialog for note input
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Add New Note");
        dialog.setHeaderText("Enter Note Content");

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the note content text area
        TextArea noteContentArea = new TextArea();
        noteContentArea.setWrapText(true);
        noteContentArea.setPromptText("Enter your note here...");

        // Set the text area in the dialog
        dialog.getDialogPane().setContent(noteContentArea);

        // Convert the result to the note content when save is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return noteContentArea.getText();
            }
            return null;
        });

        // Show the dialog and process the result
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(content -> {
            if (!content.trim().isEmpty()) {
                saveNoteToDatabase(content);
            }
        });
    }

    private void editNote(Note note) {
        // Create a custom dialog for note editing
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Edit Note");
        dialog.setHeaderText("Modify Note Content");

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the note content text area
        TextArea noteContentArea = new TextArea(note.getContent());
        noteContentArea.setWrapText(true);

        // Set the text area in the dialog
        dialog.getDialogPane().setContent(noteContentArea);

        // Convert the result to the note content when save is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return noteContentArea.getText();
            }
            return null;
        });

        // Show the dialog and process the result
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

        // Confirmation dialog
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
                // Remove the note from the list view
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
                // Update the note in the list view
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
            // Load dashboard page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
            Parent root = loader.load();

            // Get the dashboard controller and set the user email
            Dashboard controller = loader.getController();
            controller.setUserEmail(currentUserEmail);

            // Get current stage
            Stage stage = (Stage) BackToDashboard.getScene().getWindow();

            // Set new scene
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            showAlert("Navigation Error", "Failed to load dashboard.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // Utility method to generate unique note ID
    private String generateUniqueId() {
        return "NOTE_" + System.currentTimeMillis() + "_" +
                Math.round(Math.random() * 1000);
    }

    // Enhanced alert method with different alert types
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Note class remains the same as in previous implementation
    public static class Note {
        private String id;
        private String content;
        private LocalDateTime createdAt;

        public Note(String id, String content, LocalDateTime createdAt) {
            this.id = id;
            this.content = content;
            this.createdAt = createdAt;
        }

        // Getters and setters
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

    // Custom cell factory for list view
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