package UI;

import DAO.LMSDAO;
import DAO.StudentDAO;
import Model.EnrolledCourse;
import Model.Material;
import Model.Student;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.awt.Desktop;
import java.io.File;

public class LMSStudentView {

    public VBox createView(Student student) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("My Learning Materials");
        title.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 28));
        title.setTextFill(Color.web("#2c3e50"));

        // 1. Course Selection
        ComboBox<EnrolledCourse> courseBox = new ComboBox<>();
        courseBox.setPromptText("Select Course");
        courseBox.setPrefWidth(300);

        StudentDAO sDao = new StudentDAO();
        courseBox.getItems().addAll(sDao.getEnrolledCourses(student.getStudentId()));

        courseBox.setConverter(new javafx.util.StringConverter<>() {
            public String toString(EnrolledCourse c) {
                // We need CourseID, but EnrolledCourse only had code.
                // However, updated StudentDAO query fetches everything.
                // We'll rely on matching Code to ID in DAO or update EnrolledCourse model.
                return c == null ? "" : c.getCourseCode() + " - " + c.getCourseName();
            }
            public EnrolledCourse fromString(String s) { return null; }
        });

        // 2. Materials List
        ListView<Material> materialsList = new ListView<>();
        materialsList.setPrefHeight(400);

        // 3. Download/Open Button
        Button openBtn = new Button("Open / Download");
        openBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");
        openBtn.setDisable(true);

        LMSDAO lmsDao = new LMSDAO();

        // Helper to find ID (since EnrolledCourse might not store raw CourseID depending on your version)
        // For simplicity, we assume we can fetch materials by Course Code mapping or if you added CourseID to EnrolledCourse
        // *NOTE*: Ensure your EnrolledCourse model has courseId or use StudentDAO to get ID from Code.

        courseBox.setOnAction(e -> {
            EnrolledCourse ec = courseBox.getValue();
            if (ec != null) {
                // Quick lookup for CourseID based on code
                int cId = getCourseIdFromCode(ec.getCourseCode());
                if(cId != -1) {
                    materialsList.getItems().setAll(lmsDao.getMaterialsByCourse(cId));
                }
            }
        });

        materialsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            openBtn.setDisable(newVal == null);
        });

        openBtn.setOnAction(e -> {
            Material m = materialsList.getSelectionModel().getSelectedItem();
            if (m != null) {
                try {
                    File f = new File(m.getFilePath());
                    if (f.exists()) {
                        Desktop.getDesktop().open(f); // Tries to open file with system default app
                    } else {
                        new Alert(Alert.AlertType.ERROR, "File not found on server.").show();
                    }
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, "Cannot open file.").show();
                }
            }
        });

        content.getChildren().addAll(title, new Label("Select Course:"), courseBox, new Label("Available Materials:"), materialsList, openBtn);
        return content;
    }

    // Small helper to get ID from Code (You might want to add this to StudentDAO)
    private int getCourseIdFromCode(String code) {
        java.sql.Connection conn = null;
        try {
            conn = DAO.DatabaseManager.getConnection();
            java.sql.PreparedStatement ps = conn.prepareStatement("SELECT CourseID FROM Courses WHERE CourseCode = ?");
            ps.setString(1, code);
            java.sql.ResultSet rs = ps.executeQuery();
            if(rs.next()) return rs.getInt(1);
        } catch(Exception e) {}
        return -1;
    }
}