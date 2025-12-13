package UI;

import DAO.AdminDAO;
import DAO.ParentDAO;
import DAO.StudentDAO;
import DAO.TeacherDAO;
import Model.Admin;
import Model.Parent;
import Model.Student;
import Model.Teacher;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.InputStream;

public class LoginScreen {

    public void show(Stage stage) {
        // ==========================================
        // 1. LEFT PANE: The Login Form
        // ==========================================
        VBox leftPane = new VBox(20); // 20px vertical spacing
        leftPane.setPadding(new Insets(40, 60, 40, 60));
        leftPane.setPrefWidth(500);
        leftPane.setAlignment(Pos.CENTER);
        leftPane.setStyle("-fx-background-color: #f9f9f9;");

        // --- A. Logo ---
        ImageView logoView = loadImageView("/images/sis_logo.png", 60, 60);

        // --- B. Title & Description ---
        Label titleLabel = new Label("Faculty Information System (FIS)");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        titleLabel.setTextFill(Color.web("#333333"));

        Label descLabel = new Label("(FIS) system is for Undergraduate, Postgraduate\nStudents, Alumni and Guests only.");
        descLabel.setFont(Font.font("Segoe UI", 13));
        descLabel.setTextFill(Color.web("#777777"));

        // --- C. Email Input (HBox Trick) ---
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setPrefHeight(40);
        // Make the actual field transparent so the HBox border shows
        emailField.setStyle("-fx-background-color: transparent; -fx-text-fill: #333;");
        HBox.setHgrow(emailField, Priority.ALWAYS);

        ImageView emailIcon = loadImageView("/images/email_icon.png", 18, 18);

        HBox emailContainer = new HBox(10);
        emailContainer.setAlignment(Pos.CENTER_LEFT);
        emailContainer.setPadding(new Insets(0, 10, 0, 10));
        emailContainer.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-radius: 5;");
        emailContainer.getChildren().addAll(emailIcon, emailField);

        // --- D. Password Input (HBox Trick) ---
        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        passField.setPrefHeight(40);
        passField.setStyle("-fx-background-color: transparent; -fx-text-fill: #333;");
        HBox.setHgrow(passField, Priority.ALWAYS);

        ImageView passIcon = loadImageView("/images/password_icon.png", 18, 18);

        HBox passContainer = new HBox(10);
        passContainer.setAlignment(Pos.CENTER_LEFT);
        passContainer.setPadding(new Insets(0, 10, 0, 10));
        passContainer.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-radius: 5;");
        passContainer.getChildren().addAll(passIcon, passField);

        // --- E. Messages & Buttons ---
        Label messageLabel = new Label();
        messageLabel.setFont(Font.font("Segoe UI", 12));

        Button loginBtn = new Button("Login");
        loginBtn.setPrefSize(400, 45);
        loginBtn.setStyle("-fx-background-color: #6a5acd; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-background-radius: 25; -fx-cursor: hand;");

//        // --- F. "OR" Separator ---
//        HBox orSeparator = new HBox(10);
//        orSeparator.setAlignment(Pos.CENTER);
//        Pane line1 = new Pane(); line1.setPrefHeight(1); line1.setStyle("-fx-background-color: #ccc;"); HBox.setHgrow(line1, Priority.ALWAYS);
//        Label orLbl = new Label("or"); orLbl.setTextFill(Color.GRAY);
//        Pane line2 = new Pane(); line2.setPrefHeight(1); line2.setStyle("-fx-background-color: #ccc;"); HBox.setHgrow(line2, Priority.ALWAYS);
//        orSeparator.getChildren().addAll(line1, orLbl, line2);

        // --- G. Links ---
        Hyperlink forgotPassLink = new Hyperlink("Can Not Sign In? Press Here");
        forgotPassLink.setStyle("-fx-text-fill: #6a5acd; -fx-underline: true;");

        Button signupBtn = new Button("Sign Up");
        signupBtn.setPrefSize(400, 45);
        signupBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-background-radius: 25; -fx-cursor: hand;");

        // Add everything to Left Pane
        leftPane.getChildren().addAll(logoView, titleLabel, descLabel, messageLabel, emailContainer, passContainer, loginBtn, /*orSeparator,*/ forgotPassLink, signupBtn);

        // ==========================================
        // 2. RIGHT PANE: The Background Image
        // ==========================================
        VBox rightPane = new VBox();
        rightPane.setPrefWidth(600);
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        // IMPORTANT: Ensure 'background_image.jpg' exists in src/main/resources/images/
        rightPane.setStyle("-fx-background-image: url('/images/background_image.png'); " +
                "-fx-background-size: cover; " +
                "-fx-background-position: center center;");

        // ==========================================
        // 3. ROOT LAYOUT
        // ==========================================
        HBox root = new HBox();
        root.getChildren().addAll(leftPane, rightPane);

        // ==========================================
        // 4. LOGIC (Actions)
        // ==========================================

        // LOGIN LOGIC
        loginBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            String pass = passField.getText().trim();

            if (email.isEmpty() || pass.isEmpty()) {
                messageLabel.setText("Please enter email and password.");
                messageLabel.setTextFill(Color.RED);
                return;
            }

            messageLabel.setText("Authenticating...");
            messageLabel.setTextFill(Color.BLACK);

            // 1. Try Student Login
            StudentDAO studentDAO = new StudentDAO();
            Student student = studentDAO.login(email, pass);
            if (student != null) {
                messageLabel.setText("Login Success! Welcome " + student.getFirstName());
                messageLabel.setTextFill(Color.GREEN);
                // 1. Try Student Login
                if (student != null) {
                    messageLabel.setText("Login Success!");

                    // OPEN DASHBOARD
                    StudentDashboard dashboard = new StudentDashboard();
                    dashboard.show(stage, student); // Pass the logged-in student!
                    return;
                }
                return;
            }

            // 2. If not Student, Try Teacher
            TeacherDAO teacherDAO = new TeacherDAO();
            Teacher teacher = teacherDAO.login(email, pass);
            if (teacher != null) {
                messageLabel.setText("Login Success! Welcome Prof. " + teacher.getLastName());
                messageLabel.setTextFill(Color.GREEN);
                new TeacherDashboard().show(stage, teacher);
                return;
            }

            // 3. If not Teacher, Try Admin (Admin uses Username usually, checking Email just in case)
            AdminDAO adminDAO = new AdminDAO();
            Admin admin = adminDAO.login(email, pass);
            if (admin != null) {
                messageLabel.setText("Login Success! Welcome Admin " + admin.getUsername());
                messageLabel.setTextFill(Color.GREEN);
                new AdminDashboard().show(stage, admin);
                return;
            }

            // 4. Try Parent Login (NEW)
            ParentDAO parentDAO = new ParentDAO();
            Parent parent = parentDAO.login(email, pass);
            if (parent != null) {
                messageLabel.setText("Login Success! Welcome " + parent.getFirstName());
                messageLabel.setTextFill(Color.GREEN);
                new ParentDashboard().show(stage, parent);
                return;
            }

            // 5. If all fail
            messageLabel.setText("Invalid Email or Password.");
            messageLabel.setTextFill(Color.RED);
        });

        // SIGNUP LOGIC
        signupBtn.setOnAction(e -> {
            new SignupScreen().show(stage);
        });

        Scene scene = new Scene(root, 1000, 650);
        stage.setTitle("FIS Login");
        stage.setScene(scene);
        stage.show();
    }

    // Helper to load images safely without crashing if file is missing
    private ImageView loadImageView(String path, double w, double h) {
        ImageView iv = new ImageView();
        try {
            InputStream is = getClass().getResourceAsStream(path);
            if (is != null) {
                iv.setImage(new Image(is));
            } else {
                // System.out.println("Image not found: " + path); // Debug
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        iv.setFitWidth(w);
        iv.setFitHeight(h);
        iv.setPreserveRatio(true);
        return iv;
    }
}