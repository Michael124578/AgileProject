package UI;

import DAO.StudentDAO;
import Model.Register; // Use your validation logic
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class SignupScreen {

    public void show(Stage stage) {
        // 1. Main Layout
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: #f4f6f9;"); // Light Grey Background

        // 2. Title
        Label title = new Label("Create Student Account");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        title.setTextFill(Color.web("#2c3e50"));

        Label subtitle = new Label("Join the Faculty Information System");
        subtitle.setFont(Font.font("Segoe UI", 14));
        subtitle.setTextFill(Color.web("#7f8c8d"));

        // 3. Input Fields
        TextField fNameField = createStyledField("First Name");
        TextField lNameField = createStyledField("Last Name");
        TextField emailField = createStyledField("Email Address");
        PasswordField passField = createStyledPasswordField("Password");

        // Password Hint
        Label passHint = new Label("Password must have 8+ chars, 1 Upper, 1 Lower, 1 Number, 1 Special Char");
        passHint.setFont(Font.font("Segoe UI", 11));
        passHint.setTextFill(Color.GREY);

        // 4. Buttons
        Button signupBtn = new Button("Create Account");
        signupBtn.setPrefWidth(300);
        signupBtn.setPrefHeight(45);
        signupBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 5; -fx-cursor: hand;");

        Button backBtn = new Button("Back to Login");
        backBtn.setPrefWidth(300);
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #7f8c8d; -fx-font-size: 13px; -fx-cursor: hand;");

        Label messageLabel = new Label(); // To show errors/success
        messageLabel.setFont(Font.font("Segoe UI", 13));

        // 5. Logic
        signupBtn.setOnAction(e -> {
            String fName = fNameField.getText().trim();
            String lName = lNameField.getText().trim();
            String email = emailField.getText().trim();
            String pass = passField.getText();

            // --- VALIDATION (Using your Utility.Register class) ---
            if (!Register.isValidFirstName(fName)) {
                showError(messageLabel, "Invalid First Name (Must start with Capital letter).");
                return;
            }
            if (!Register.isValidLastName(lName)) {
                showError(messageLabel, "Invalid Last Name (Must start with Capital letter).");
                return;
            }
            if (!Register.isValidEmail(email)) {
                showError(messageLabel, "Invalid Email format (use gmail, yahoo, or hotmail).");
                return;
            }
            if (!Register.isValidPassword(pass)) {
                showError(messageLabel, "Weak Password. Check criteria below.");
                return;
            }

            // --- DATABASE CALL ---
            StudentDAO dao = new StudentDAO();
            boolean success = dao.signup(fName, lName, email, pass);

            if (success) {
                messageLabel.setText("Account Created! Redirecting to Login...");
                messageLabel.setTextFill(Color.GREEN);

                // Delay slightly or just go back immediately? Let's go back.
                new LoginScreen().show(stage);
            } else {
                showError(messageLabel, "Signup Failed. Email might already exist.");
            }
        });

        backBtn.setOnAction(e -> {
            // Go back to Login Screen
            new LoginScreen().show(stage);
        });

        // 6. Add to Layout
        layout.getChildren().addAll(
                title, subtitle,
                new Label(""), // Spacer
                fNameField, lNameField, emailField, passField, passHint,
                new Label(""), // Spacer
                messageLabel,
                signupBtn, backBtn
        );

        // 7. Scene
        Scene scene = new Scene(layout, 1000, 650); // Same size as login
        stage.setTitle("FIS Signup");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    // Helper to style TextFields consistently
    private TextField createStyledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setMaxWidth(300);
        tf.setPrefHeight(40);
        tf.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 0 10;");
        return tf;
    }

    private PasswordField createStyledPasswordField(String prompt) {
        PasswordField pf = new PasswordField();
        pf.setPromptText(prompt);
        pf.setMaxWidth(300);
        pf.setPrefHeight(40);
        pf.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 0 10;");
        return pf;
    }

    private void showError(Label label, String text) {
        label.setText(text);
        label.setTextFill(Color.RED);
    }
}
