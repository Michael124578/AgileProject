package UI;

import DAO.LMSDAO;
import DAO.TeacherDAO;
import Model.Course;
import Model.Material;
import Model.Teacher;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.List;

public class LMSTeacherView {

    public VBox createView(Teacher teacher) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("LMS - Course Materials");
        title.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 28));
        title.setTextFill(Color.web("#2c3e50"));

        // 1. Course Selection
        ComboBox<Course> courseBox = new ComboBox<>();
        courseBox.setPromptText("Select Course to Manage");
        courseBox.setPrefWidth(300);

        TeacherDAO tDao = new TeacherDAO();
        courseBox.getItems().addAll(tDao.getTeacherCourses(teacher.getTeacherId()));

        courseBox.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Course c) { return c == null ? "" : c.getCourseCode() + " - " + c.getCourseName(); }
            public Course fromString(String s) { return null; }
        });

        // 2. Materials List
        ListView<Material> materialsList = new ListView<>();
        materialsList.setPrefHeight(400);

        // 3. Upload Button
        Button uploadBtn = new Button("Upload Material");
        uploadBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand;");
        uploadBtn.setDisable(true);

        // 4. Delete Button
        Button deleteBtn = new Button("Delete Selected");
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
        deleteBtn.setDisable(true);

        // Logic
        LMSDAO lmsDao = new LMSDAO();

        courseBox.setOnAction(e -> {
            Course c = courseBox.getValue();
            if (c != null) {
                materialsList.getItems().setAll(lmsDao.getMaterialsByCourse(c.getCourseId()));
                uploadBtn.setDisable(false);
            }
        });

        uploadBtn.setOnAction(e -> {
            Course c = courseBox.getValue();
            if (c == null) return;

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Material File");
            File file = fileChooser.showOpenDialog(null);

            if (file != null) {
                // In a real app, you would copy this file to a server directory.
                // Here we store the absolute path.
                boolean success = lmsDao.uploadMaterial(c.getCourseId(), file.getName(), file.getAbsolutePath());
                if (success) {
                    materialsList.getItems().setAll(lmsDao.getMaterialsByCourse(c.getCourseId()));
                    new Alert(Alert.AlertType.INFORMATION, "File Uploaded Successfully!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Upload Failed.").show();
                }
            }
        });

        materialsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            deleteBtn.setDisable(newVal == null);
        });

        deleteBtn.setOnAction(e -> {
            Material m = materialsList.getSelectionModel().getSelectedItem();
            if (m != null) {
                lmsDao.deleteMaterial(m.getMaterialId());
                materialsList.getItems().remove(m);
            }
        });

        HBox controls = new HBox(10, uploadBtn, deleteBtn);
        content.getChildren().addAll(title, new Label("Select Course:"), courseBox, new Label("Uploaded Materials:"), materialsList, controls);
        return content;
    }
}