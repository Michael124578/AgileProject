package UI;

import DAO.StudentDAO;
import DAO.TeacherDAO;
import Model.Course;
import Model.StudentGrade;
import Model.Teacher;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public class TeacherDashboard {

    public void show(Stage stage, Teacher teacher) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f4f6f9;");

        root.setTop(createTopBar());
        root.setLeft(createSidebar(teacher, root));
        root.setCenter(createMainContent(teacher, root));

        Scene scene = new Scene(root, 1200, 700);
        stage.setTitle("Teacher Dashboard - Prof. " + teacher.getLastName());
        stage.setScene(scene);
        stage.show();
    }

    // 1. Top Bar
    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(10, 20, 10, 20));
        topBar.setStyle("-fx-background-color: #2c3e50;");
        topBar.setAlignment(Pos.CENTER_LEFT);

        ImageView logo = loadImageView("/images/sis_logo.png", 30, 30);
        Label brandLabel = new Label("Faculty SIS (Instructor Mode)");
        brandLabel.setTextFill(Color.WHITE);
        brandLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topBar.getChildren().addAll(logo, brandLabel, spacer);
        return topBar;
    }

    // 2. Sidebar (Updated with Profile Logic)
    private VBox createSidebar(Teacher teacher, BorderPane root) {
        VBox sidebar = new VBox(15);
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: #34495e;");
        sidebar.setPadding(new Insets(30, 10, 20, 10));
        sidebar.setAlignment(Pos.TOP_CENTER);

        // A. Profile Pic Logic
        ImageView profilePic = new ImageView();
        profilePic.setFitWidth(80); profilePic.setFitHeight(80);
        try {
            if (teacher.getProfilePicPath() != null && !teacher.getProfilePicPath().isEmpty()) {
                profilePic.setImage(new Image("file:///" + teacher.getProfilePicPath().replace("\\", "/")));
            } else {
                profilePic.setImage(new Image(getClass().getResourceAsStream("/images/student_placeholder.png")));
            }
        } catch (Exception e) {
            try { profilePic.setImage(new Image(getClass().getResourceAsStream("/images/student_placeholder.png"))); } catch(Exception ignored){}
        }
        Circle clip = new Circle(40, 40, 40);
        profilePic.setClip(clip);

        // B. Name
        Label nameLabel = new Label("Prof. " + teacher.getLastName());
        nameLabel.setTextFill(Color.WHITE);
        nameLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        Label deptLabel = new Label(teacher.getPassword());
        deptLabel.setTextFill(Color.LIGHTGRAY);

        // C. Menu Buttons
        VBox navMenu = new VBox(5);
        Button dashBtn = createNavButton("My Courses", true);
        Button profileBtn = createNavButton("My Profile", false); // NEW
        Button logoutBtn = createNavButton("Logout", false);

        // Actions
        dashBtn.setOnAction(e -> {
            root.setCenter(createMainContent(teacher, root));
            setActive(dashBtn, profileBtn, logoutBtn);
        });

        profileBtn.setOnAction(e -> {
            root.setCenter(createProfileView(teacher, root)); // NEW View
            setActive(profileBtn, dashBtn, logoutBtn);
        });

        logoutBtn.setOnAction(e -> {
            Stage current = (Stage) logoutBtn.getScene().getWindow();
            current.close();
            try { new LoginScreen().show(new Stage()); } catch (Exception ex) {}
        });

        navMenu.getChildren().addAll(dashBtn, profileBtn, logoutBtn);
        sidebar.getChildren().addAll(profilePic, nameLabel, deptLabel, new Region(), navMenu);
        return sidebar;
    }

    // 3. Main Content
    private VBox createMainContent(Teacher teacher, BorderPane root) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        Label title = new Label("My Teaching Schedule");
        title.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 28));

        TeacherDAO dao = new TeacherDAO();
        List<Course> myCourses = dao.getTeacherCourses(teacher.getTeacherId());
        FlowPane grid = new FlowPane();
        grid.setHgap(20); grid.setVgap(20);

        if (myCourses.isEmpty()) {
            grid.getChildren().add(new Label("No courses assigned yet."));
        } else {
            for (Course c : myCourses) {
                grid.getChildren().add(createCourseCard(teacher, c, root));
            }
        }
        content.getChildren().addAll(title, grid);
        return content;
    }

    // 4. Course Card
    private Pane createCourseCard(Teacher teacher, Course course, BorderPane root) {
        VBox card = new VBox(10);
        card.setPrefSize(250, 150);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Label code = new Label(course.getCourseCode());
        code.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        code.setTextFill(Color.web("#3498db"));
        Label name = new Label(course.getCourseName());
        name.setWrapText(true);

        card.setOnMouseClicked(e -> root.setCenter(createGradingView(teacher, course, root)));
        card.getChildren().addAll(code, name, new Separator(), new Label("Click to Grade →"));
        return card;
    }

    // 5. Grading View
    private VBox createGradingView(Teacher teacher, Course course, BorderPane root) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));

        Button backBtn = new Button("← Back to Courses");
        backBtn.setOnAction(e -> root.setCenter(createMainContent(teacher, root)));

        Label title = new Label("Grading: " + course.getCourseCode());
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        TableView<StudentGrade> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<StudentGrade, String> nameCol = new TableColumn<>("Student Name");
        nameCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("fullName"));

        TableColumn<StudentGrade, String> gradeCol = new TableColumn<>("Current Grade");
        gradeCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("gradeString"));

        TableColumn<StudentGrade, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Assign");
            {
                btn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
                btn.setOnAction(e -> handleGradeUpdate(teacher, getTableView().getItems().get(getIndex()), course, root));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        table.getColumns().addAll(nameCol, gradeCol, actionCol);
        TeacherDAO dao = new TeacherDAO();
        table.getItems().addAll(dao.getStudentsInCourse(course.getCourseId()));

        content.getChildren().addAll(backBtn, title, table);
        return content;
    }

    // =========================================
    // 6. NEW: Profile View
    // =========================================
    private VBox createProfileView(Teacher teacher, BorderPane root) {
        VBox content = new VBox(15); // Consistent spacing
        content.setPadding(new Insets(40));
        content.setAlignment(Pos.TOP_LEFT);

        // Header
        Label title = new Label("Edit Instructor Profile");
        title.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 28));
        title.setTextFill(Color.web("#2c3e50"));

        // --- 1. Profile Picture Section ---
        HBox picSection = new HBox(20);
        picSection.setAlignment(Pos.CENTER_LEFT);

        ImageView currentPic = new ImageView();
        currentPic.setFitWidth(100); currentPic.setFitHeight(100);
        Circle clip = new Circle(50, 50, 50);
        currentPic.setClip(clip);

        try {
            if (teacher.getProfilePicPath() != null && !teacher.getProfilePicPath().isEmpty())
                currentPic.setImage(new Image("file:///" + teacher.getProfilePicPath().replace("\\", "/")));
            else
                currentPic.setImage(new Image(getClass().getResourceAsStream("/images/student_placeholder.png")));
        } catch (Exception e) {
            // Fallback
            try { currentPic.setImage(new Image(getClass().getResourceAsStream("/images/student_placeholder.png"))); } catch (Exception ignored) {}
        }

        Button uploadBtn = new Button("Change Picture");
        final StringBuilder newImagePath = new StringBuilder(teacher.getProfilePicPath() == null ? "" : teacher.getProfilePicPath());

        uploadBtn.setOnAction(e -> {
            javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
            fc.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("Images", "*.jpg", "*.png"));
            java.io.File f = fc.showOpenDialog(null);
            if (f != null) {
                newImagePath.setLength(0);
                newImagePath.append(f.getAbsolutePath());
                currentPic.setImage(new Image(f.toURI().toString()));
            }
        });
        picSection.getChildren().addAll(currentPic, uploadBtn);

        // --- 2. Form Fields ---

        // First Name
        Label lblF = new Label("First Name:");
        TextField fNameField = new TextField(teacher.getFirstName());
        fNameField.setMaxWidth(400);

        // Last Name
        Label lblL = new Label("Last Name:");
        TextField lNameField = new TextField(teacher.getLastName());
        lNameField.setMaxWidth(400);

        // Email
        Label lblE = new Label("Email:");
        TextField emailField = new TextField(teacher.getEmail());
        emailField.setMaxWidth(400);

        // Department (Teacher Specific)
        Label lblD = new Label("Department:");
        TextField deptField = new TextField(teacher.getPassword());
        deptField.setMaxWidth(400);

        // Password
        Label lblP = new Label("Password:");
        PasswordField passField = new PasswordField();
        passField.setText(teacher.getDepartment()); // Pre-fill
        passField.setMaxWidth(400);

        // --- 3. Save Button ---
        Button saveBtn = new Button("Save Changes");
        saveBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        saveBtn.setPrefWidth(150);

        Label statusLabel = new Label();

        saveBtn.setOnAction(e -> {
            TeacherDAO dao = new TeacherDAO();

            // Pass all fields to the DAO (Ensure this matches DAO parameter order!)
            boolean success = dao.updateProfile(
                    teacher.getTeacherId(),
                    fNameField.getText(),
                    lNameField.getText(),
                    emailField.getText(),
                    passField.getText(),
                    deptField.getText(),
                    newImagePath.toString()
            );

            if (success) {
                statusLabel.setText("Saved! Please Logout to see changes.");
                statusLabel.setTextFill(Color.GREEN);
            } else {
                statusLabel.setText("Error updating profile.");
                statusLabel.setTextFill(Color.RED);
            }
        });

        // Add all to layout
        content.getChildren().addAll(
                title,
                picSection,
                lblF, fNameField,
                lblL, lNameField,
                lblE, emailField,
                lblD, deptField, // Added Dept
                lblP, passField,
                new Region(), // Spacer
                saveBtn,
                statusLabel
        );
        return content;
    }

    // Helpers
    private void handleGradeUpdate(Teacher teacher, StudentGrade sg, Course course, BorderPane root) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Assign Grade");
        dialog.setHeaderText("Enter grade for " + sg.getFullName());
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(gradeStr -> {
            try {
                double g = Double.parseDouble(gradeStr);
                if(new TeacherDAO().updateGrade(sg.getStudentId(), course.getCourseId(), g)) {
                    root.setCenter(createGradingView(teacher, course, root));
                }
            } catch (Exception e) {}
        });
    }

    private Button createNavButton(String text, boolean isActive) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(10));
        btn.setStyle(isActive ? "-fx-background-color: #2c3e50; -fx-text-fill: white;" : "-fx-background-color: transparent; -fx-text-fill: #bdc3c7;");
        // Hover Effect
        btn.setOnMouseEntered(e -> {
            // Only change color if it's NOT the active button (we check style string)
            if (!btn.getStyle().contains("#34495e")) {
                btn.setStyle("-fx-background-color: #3e5871; -fx-text-fill: white;");
            }
        });
        btn.setOnMouseExited(e -> {
            // Reset to inactive style if it wasn't clicked
            if (!btn.getStyle().contains("#34495e")) {
                btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #bdc3c7;");
            }
        });
        return btn;
    }

    private void setActive(Button active, Button... others) {
        active.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white;");
        for (Button b : others) b.setStyle("-fx-background-color: transparent; -fx-text-fill: #bdc3c7;");
    }

    private ImageView loadImageView(String path, double w, double h) {
        ImageView iv = new ImageView();
        try { InputStream is = getClass().getResourceAsStream(path); if(is!=null) iv.setImage(new Image(is)); } catch(Exception e){}
        iv.setFitWidth(w); iv.setFitHeight(h);
        return iv;
    }
}