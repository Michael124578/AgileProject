package UI;

import DAO.StudentDAO;
import Model.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
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

public class StudentDashboard {

    public void show(Stage stage, Student student) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f4f6f9;");

        root.setTop(createTopBar());
        root.setLeft(createSidebar(student, root));

        // FIX: Pass 'root' here
        root.setCenter(createMainContent(student, root));

        Scene scene = new Scene(root, 1200, 700);
        stage.setTitle("Student Dashboard");
        stage.setScene(scene);
        stage.show();
    }

    // =========================================
    // Helper: Create Top Bar
    // =========================================
    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(10, 20, 10, 20));
        topBar.setStyle("-fx-background-color: #4a69bd;"); // Royal Blue
        topBar.setAlignment(Pos.CENTER_LEFT);

        // Logo & Title
        ImageView logo = loadImageView("/images/sis_logo.png", 30, 30);
        Label brandLabel = new Label("SIS");
        brandLabel.setTextFill(Color.WHITE);
        brandLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));

        // Hamburger Menu (Placeholder)
        Label menuIcon = new Label("☰");
        menuIcon.setTextFill(Color.WHITE);
        menuIcon.setFont(Font.font(18));
        menuIcon.setPadding(new Insets(0, 0, 0, 15));

        // Breadcrumb
        Label breadcrumb = new Label(" /  Dashboard");
        breadcrumb.setTextFill(Color.web("#d1d8e0"));
        breadcrumb.setFont(Font.font("Segoe UI", 14));

        // Spacer to push right icons
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Right Icons (Notification, Message, User) - Placeholders
        Label notifIcon = new Label("\uD83D\uDD14"); // Bell
        notifIcon.setTextFill(Color.WHITE);
        Label msgIcon = new Label("\uD83D\uDCAC 0"); // Chat bubble
        msgIcon.setTextFill(Color.WHITE);

        topBar.getChildren().addAll(logo, brandLabel, menuIcon, breadcrumb, spacer, notifIcon, msgIcon);
        return topBar;
    }

    // =========================================
    // Helper: Sidebar with REAL Profile Pic
    // =========================================
    // =========================================
    // 1. The Sidebar Method
    // =========================================
    private VBox createSidebar(Student student, BorderPane root) {
        VBox sidebar = new VBox(15);
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: #2c3e50;"); // Dark Slate
        sidebar.setPadding(new Insets(30, 10, 20, 10));
        sidebar.setAlignment(Pos.TOP_CENTER);

        // --- A. Profile Picture Logic ---
        ImageView profilePic = new ImageView();
        profilePic.setFitWidth(80);
        profilePic.setFitHeight(80);

        try {
            String dbPath = student.getProfilePicPath();
            // 1. Try to load from Database Path (Real User Pic)
            if (dbPath != null && !dbPath.isEmpty()) {
                String url = "file:///" + dbPath.replace("\\", "/");
                profilePic.setImage(new Image(url));
            } else {
                // 2. Fallback to Resource Placeholder
                InputStream is = getClass().getResourceAsStream("/images/student_placeholder.png");
                if (is != null) profilePic.setImage(new Image(is));
            }
        } catch (Exception e) {
            // 3. Safety Net (If file missing, try placeholder again or ignore)
            try {
                InputStream is = getClass().getResourceAsStream("/images/student_placeholder.png");
                if (is != null) profilePic.setImage(new Image(is));
            } catch (Exception ignored) {}
        }

        // Make it Circular
        Circle clip = new Circle(40, 40, 40);
        profilePic.setClip(clip);

        // --- B. Name Label ---
        Label nameLabel = new Label(student.getFirstName() + " " + student.getLastName());
        nameLabel.setTextFill(Color.WHITE);
        nameLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        nameLabel.setWrapText(true);
        nameLabel.setAlignment(Pos.CENTER);

        // --- C. Navigation Menu ---
        VBox navMenu = new VBox(5);

        // 1. Create Buttons
        Button dashBtn = createNavButton("Dashboard", true); // Initially Active
        Button bookBtn = createNavButton("Book Hall", false); // NEW
        Button hallInfoBtn = createNavButton("Hall Info", false); // <--- NEW BUTTON
        Button lmsBtn = createNavButton("LMS ->", false);
        Button profileBtn = createNavButton("Profile", false);
        Button logoutBtn = createNavButton("Logout", false);

        // 2. Dashboard Action (Switch View)
        dashBtn.setOnAction(e -> {
            root.setCenter(createMainContent(student, root));
            setActive(dashBtn, profileBtn, logoutBtn);
        });
        lmsBtn.setOnAction(e -> {
            root.setCenter(new LMSStudentView().createView(student));
            setActive(lmsBtn, dashBtn, bookBtn, hallInfoBtn, profileBtn, logoutBtn);
        });

        // 3. Profile Action (Switch View)
        profileBtn.setOnAction(e -> {
            root.setCenter(createProfileView(student)); // Switch content
            setActive(profileBtn, dashBtn, logoutBtn);  // Update visual style
        });

        // 4. Logout Action
        logoutBtn.setOnAction(e -> {
            // Close current window
            Stage currentStage = (Stage) logoutBtn.getScene().getWindow();
            currentStage.close();
            // Open Login Screen
            try {
                new LoginScreen().show(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        bookBtn.setOnAction(e -> {
            root.setCenter(createBookingView(student, root));
            setActive(bookBtn, dashBtn, profileBtn, logoutBtn);
        });
        hallInfoBtn.setOnAction(e -> {
            root.setCenter(createHallInfoView(student)); // Call the new view method
            setActive(hallInfoBtn, dashBtn, bookBtn, profileBtn, logoutBtn);
        });

        navMenu.getChildren().addAll(dashBtn, profileBtn,lmsBtn, bookBtn,hallInfoBtn, logoutBtn );

        // Add everything to Sidebar
        sidebar.getChildren().addAll(profilePic, nameLabel, new Region(), navMenu);
        return sidebar;
    }

    // =========================================
    // 2. Helper: Create Styling for Buttons
    // =========================================
    private Button createNavButton(String text, boolean isActive) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(10, 20, 10, 20));
        btn.setFont(Font.font("Segoe UI", 14));
        btn.setCursor(javafx.scene.Cursor.HAND);

        if (isActive) {
            // Active Style: Darker background + Blue Left Border
            btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-border-color: transparent transparent transparent #3498db; -fx-border-width: 0 0 0 4;");
        } else {
            // Inactive Style: Transparent
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #bdc3c7;");
        }

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

    // =========================================
    // 3. Helper: Toggle Active State (Visuals)
    // =========================================
    private void setActive(Button active, Button... others) {
        // Set the clicked button to "Active" style
        active.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-border-color: transparent transparent transparent #3498db; -fx-border-width: 0 0 0 4;");

        // Reset all other buttons to "Inactive" style
        for (Button b : others) {
            b.setStyle("-fx-background-color: transparent; -fx-text-fill: #bdc3c7; -fx-border-width: 0;");
        }
    }

    // =========================================
    // Helper: Main Content Area
    // =========================================
    private VBox createMainContent(Student student, BorderPane root) {
        VBox content = new VBox(25);
        content.setPadding(new Insets(30));

        // 1. Header
        Label title = new Label("Dashboard");
        title.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 28));
        title.setTextFill(Color.web("#2c3e50"));

        Label subtitle = new Label("Welcome to the Faculty SIS System");
        subtitle.setFont(Font.font("Segoe UI", 14));
        subtitle.setTextFill(Color.web("#7f8c8d"));

        // 2. Top Row: Stats Cards
        HBox statsRow = new HBox(20);
        Pane gpaCard = createStatCard("CUMULATIVE GPA", String.valueOf(student.getGpa()), "4", "#f39c12");
        Pane trainingCard = createStatCard("TRAINING WEEKS", String.valueOf(student.getWeeks()), "12", "#3f51b5"); // Assuming you added getWeeks() to Student
        Pane creditCard = createStatCard("CREDIT HOURS", String.valueOf(student.getCreditHours()), "170", "#27ae60"); // Assuming you added getCreditHours()
        statsRow.getChildren().addAll(gpaCard, trainingCard, creditCard);

        // 3. Bottom Area: Action Cards
        FlowPane actionGrid = new FlowPane();
        actionGrid.setHgap(20);
        actionGrid.setVgap(20);

        // --- Card A: "My Courses" (Enrolled) ---
        Pane myCoursesCard = createActionCard("My Courses", "/images/courses_icon.png");
        myCoursesCard.setOnMouseClicked(e -> {
            root.setCenter(createMyCoursesView(student, root));
        });

        // --- Card B: "Available Courses" (Register)
        Pane availableCoursesCard = createActionCard("Register Courses", "/images/register_icon.png");
        availableCoursesCard.setOnMouseClicked(e -> {
            root.setCenter(createRegistrationView(student, root));
        });

        // --- Card C: "My Schedule" ---
        Pane scheduleCard = createActionCard("My Schedule", "/images/schedule_icon.png");
        scheduleCard.setOnMouseClicked(e -> {
            root.setCenter(createScheduleView(student, root));
        });

        // --- Card D: "Fees" ---
        Pane feesCard = createActionCard("Fees & Payments", "/images/fees_icon.png");
        feesCard.setOnMouseClicked(e -> {
            root.setCenter(createFeesView(student, root));
        });

        // Add all 4 cards
        actionGrid.getChildren().addAll(myCoursesCard, availableCoursesCard, scheduleCard, feesCard);

        // 4. Add all to VBox
        content.getChildren().addAll(title, subtitle, statsRow, actionGrid);
        return content;
    }

    // =========================================
    // Helper: Create a Single Colorful Card
    // =========================================
    private Pane createStatCard(String title, String mainValue, String badgeValue, String hexColor) {
        VBox card = new VBox(10);
        card.setPrefSize(300, 150);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + hexColor + "; -fx-background-radius: 5;");

        // Shadow effect
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.2));
        shadow.setRadius(5);
        shadow.setOffsetY(2);
        card.setEffect(shadow);

        // Top Row: Main Value + Badge
        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label valLabel = new Label(mainValue);
        valLabel.setTextFill(Color.WHITE);
        valLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 36));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Circle Badge
        StackPane badge = new StackPane();
        Circle badgeBg = new Circle(15, Color.rgb(255, 255, 255, 0.3));
        Label badgeText = new Label(badgeValue);
        badgeText.setTextFill(Color.WHITE);
        badgeText.setFont(Font.font(12));
        badge.getChildren().addAll(badgeBg, badgeText);

        topRow.getChildren().addAll(valLabel, spacer, badge);

        // Title
        Label titleLabel = new Label(title);
        titleLabel.setTextFill(Color.rgb(255, 255, 255, 0.9));
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));

        // Progress Bar (Decorative)
        ProgressBar pb = new ProgressBar(0.7);
        pb.setPrefWidth(Double.MAX_VALUE);
        pb.setPrefHeight(5);
        pb.setStyle("-fx-accent: rgba(255,255,255,0.5); -fx-control-inner-background: rgba(0,0,0,0.1); -fx-text-box-border: transparent;");

        card.getChildren().addAll(topRow, titleLabel, pb);
        return card;
    }

    private ImageView loadImageView(String path, double w, double h) {
        ImageView iv = new ImageView();
        try {
            InputStream is = getClass().getResourceAsStream(path);
            if (is != null) iv.setImage(new Image(is));
        } catch (Exception e) { e.printStackTrace(); }
        iv.setFitWidth(w); iv.setFitHeight(h);
        return iv;
    }

    // =========================================
    // Helper: Create White Action Card (Like "My Courses")
    // =========================================
    private Pane createActionCard(String title, String iconPath) {
        VBox card = new VBox(15);
        card.setPrefSize(250, 140); // A nice rectangular size
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-cursor: hand;");

        // Add a Shadow to make it pop
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.1));
        shadow.setRadius(10);
        shadow.setOffsetY(3);
        card.setEffect(shadow);

        // Hover Effect (Card moves up slightly when hovered)
        card.setOnMouseEntered(e -> card.setTranslateY(-3));
        card.setOnMouseExited(e -> card.setTranslateY(0));

        // Icon
        ImageView icon = loadImageView(iconPath, 40, 40);
        // If icon missing, use a generic shape (optional fallback logic could go here)

        // Label
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 16));
        titleLabel.setTextFill(Color.web("#34495e"));

        // Icon on right side (Optional design choice)
        // For now, centered stack:
        card.getChildren().addAll(icon, titleLabel);

        return card;
    }

    // =========================================
    // NEW: Profile Edit View
    // =========================================
    private VBox createProfileView(Student student) {
        VBox content = new VBox(15);
        content.setPadding(new Insets(40));
        content.setAlignment(Pos.TOP_LEFT);

        // Header
        Label title = new Label("Edit Profile");
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
            if (student.getProfilePicPath() != null)
                currentPic.setImage(new Image("file:///" + student.getProfilePicPath().replace("\\", "/")));
            else
                currentPic.setImage(new Image(getClass().getResourceAsStream("/images/student_placeholder.png")));
        } catch (Exception e) { /* Ignore */ }

        Button uploadBtn = new Button("Change Picture");
        final StringBuilder newImagePath = new StringBuilder(student.getProfilePicPath() == null ? "" : student.getProfilePicPath());

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
        TextField fNameField = new TextField(student.getFirstName());
        fNameField.setMaxWidth(400);

        // Last Name
        Label lblL = new Label("Last Name:");
        TextField lNameField = new TextField(student.getLastName());
        lNameField.setMaxWidth(400);

        // Email (NEW)
        Label lblE = new Label("Email:");
        TextField emailField = new TextField(student.getEmail());
        emailField.setMaxWidth(400);

        // Password (NEW)
        Label lblP = new Label("Password:");
        PasswordField passField = new PasswordField();
        passField.setText(student.getPassword()); // Pre-fill current password
        passField.setMaxWidth(400);

        // --- 3. Save Button ---
        Button saveBtn = new Button("Save Changes");
        saveBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        saveBtn.setPrefWidth(150);

        Label statusLabel = new Label();

        saveBtn.setOnAction(e -> {
            StudentDAO dao = new StudentDAO();

            // Pass all 5 fields to the DAO
            boolean success = dao.updateStudentProfile(
                    student.getStudentId(),
                    fNameField.getText(),
                    lNameField.getText(),
                    emailField.getText(), // New
                    passField.getText(),  // New
                    newImagePath.toString()
            );

            if (success) {
                statusLabel.setText("Saved! Please Logout to see changes.");
                statusLabel.setTextFill(Color.GREEN);
            } else {
                statusLabel.setText("Error: Email might already represent another user.");
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
                lblP, passField,
                new Region(), // Spacer
                saveBtn,
                statusLabel
        );
        return content;
    }

    // =========================================
    // NEW: My Courses View (TableView)
    // =========================================
    private VBox createMyCoursesView(Student student, BorderPane root) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.TOP_LEFT);

        // 1. Title & Back Button
        Button backBtn = new Button("← Back");
        backBtn.setStyle(
                "-fx-background-color: #d3d3d3;" +
                        "-fx-text-fill: #2c3e50;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
        );        backBtn.setOnAction(e -> root.setCenter(createMainContent(student, root)));

        Label title = new Label("My Enrolled Courses");
        title.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 28));
        title.setTextFill(Color.web("#2c3e50"));

        // 2. The Table
        TableView<EnrolledCourse> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Standard Columns
        TableColumn<EnrolledCourse, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("courseCode"));

        TableColumn<EnrolledCourse, String> nameCol = new TableColumn<>("Course Name");
        nameCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("courseName"));

        TableColumn<EnrolledCourse, Integer> creditCol = new TableColumn<>("Credits");
        creditCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("credits"));

        TableColumn<EnrolledCourse, String> semCol = new TableColumn<>("Semester");
        semCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("semester"));

        TableColumn<EnrolledCourse, String> gradeCol = new TableColumn<>("Grade");
        gradeCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("gradeString"));

        // --- NEW: Action Column (Drop Button) ---
        TableColumn<EnrolledCourse, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button dropBtn = new Button("Drop");

            {
                dropBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
                dropBtn.setOnAction(event -> {
                    EnrolledCourse selectedCourse = getTableView().getItems().get(getIndex());

                    // Confirm before dropping
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to drop " + selectedCourse.getCourseCode() + "?", ButtonType.YES, ButtonType.NO);
                    alert.showAndWait();

                    if (alert.getResult() == ButtonType.YES) {
                        handleDropCourse(student, selectedCourse, root);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    // Only show Drop button if course is NOT graded yet (optional logic)
                    EnrolledCourse course = getTableView().getItems().get(getIndex());
                    if (course.getGrade() > 0) {
                        setGraphic(null); // Cannot drop completed courses
                    } else {
                        setGraphic(dropBtn);
                    }
                }
            }
        });

        // Add columns to table
        table.getColumns().addAll(codeCol, nameCol, creditCol, semCol, gradeCol, actionCol);

        // 3. Load Data
        StudentDAO dao = new StudentDAO();
        List<EnrolledCourse> data = dao.getEnrolledCourses(student.getStudentId());
        table.getItems().addAll(data);

        content.getChildren().addAll(backBtn, title, table);
        return content;
    }

    // Helper Logic for Dropping
    private void handleDropCourse(Student student, EnrolledCourse course, BorderPane root) {
        StudentDAO dao = new StudentDAO();
        boolean success = dao.dropCourse(student.getStudentId(), course.getCourseCode());

        if (success) {
            // 1. RE-LOGIN / REFRESH STUDENT (Crucial for updating financials)
            Student updatedStudent = dao.getStudentById(student.getStudentId());

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Course Dropped Successfully. Fees updated.");
            alert.showAndWait();

            // 2. Refresh View
            root.setCenter(createMyCoursesView(updatedStudent, root));
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to drop course.");
            alert.show();
        }
    }

    // =========================================
    // NEW: Registration View (Table with Buttons)
    // =========================================
    private VBox createRegistrationView(Student student, BorderPane root) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.TOP_LEFT);

        // 1. Header & Back Button
        Button backBtn = new Button("← Back");
        backBtn.setStyle(
                "-fx-background-color: #d3d3d3;" +
                        "-fx-text-fill: #2c3e50;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
        );
        backBtn.setOnAction(e -> root.setCenter(createMainContent(student, root)));

        Label title = new Label("Available Courses");
        title.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 28));
        title.setTextFill(Color.web("#2c3e50"));

        Label subTitle = new Label("Select a course to register for the upcoming semester.");
        subTitle.setTextFill(Color.GREY);

        // 2. The Table
        TableView<Course> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Standard Columns
        TableColumn<Course, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("courseCode"));

        TableColumn<Course, String> nameCol = new TableColumn<>("Course Name");
        nameCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("courseName"));

        TableColumn<Course, Integer> credCol = new TableColumn<>("Credits");
        credCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("credits"));

        // 3. THE ACTION COLUMN (Button Logic)
        TableColumn<Course, Void> actionCol = new TableColumn<>("Action");

        // Custom Cell Factory to render a Button inside the cell
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Register");
            {
                btn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
                btn.setOnAction(event -> {
                    // Get the course object for this row
                    Course course = getTableView().getItems().get(getIndex());
                    handleRegistration(student, course, root);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });

        table.getColumns().addAll(codeCol, nameCol, credCol, actionCol);

        // 4. Load Data
        StudentDAO dao = new StudentDAO();
        List<Course> courses = dao.getAvailableCourses(student.getStudentId());
        table.getItems().addAll(courses);

        // If empty, show a message
        if (courses.isEmpty()) {
            table.setPlaceholder(new Label("No available courses found."));
        }

        content.getChildren().addAll(backBtn, title, subTitle, table);
        return content;
    }

    // Helper to handle the click logic
    private void handleRegistration(Student student, Course course, BorderPane root) {
        StudentDAO dao = new StudentDAO();
        // Hardcoding Semester/Year for now, or you could add inputs for them
        boolean success = dao.registerCourse(student.getStudentId(), course.getCourseId(), "Spring", 2026);

        if (success) {
            Student updatedStudent = dao.getStudentById(student.getStudentId());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Successfully registered for " + course.getCourseCode());
            alert.showAndWait();

            // REFRESH the view (This removes the registered course from the list)
            root.setCenter(createRegistrationView(student, root));
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Could not register. You might already be enrolled.");
            alert.show();
        }
    }

    // =========================================
    // NEW: Weekly Schedule View
    // =========================================
    private VBox createScheduleView(Student student, BorderPane root) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.TOP_LEFT);

        // 1. Header
        Button backBtn = new Button("← Back");
        backBtn.setStyle(
                "-fx-background-color: #d3d3d3;" +
                        "-fx-text-fill: #2c3e50;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
        );
        backBtn.setOnAction(e -> root.setCenter(createMainContent(student, root)));

        Label title = new Label("My Class Schedule");
        title.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 28));
        title.setTextFill(Color.web("#2c3e50"));

        // 2. The Weekly Grid (5 Columns)
        HBox weekGrid = new HBox(15); // Spacing between days
        weekGrid.setAlignment(Pos.TOP_CENTER);

        // Create 5 Day Columns
        VBox monCol = createDayColumn("Monday");
        VBox tueCol = createDayColumn("Tuesday");
        VBox wedCol = createDayColumn("Wednesday");
        VBox thuCol = createDayColumn("Thursday");
        VBox friCol = createDayColumn("Friday");

        // 3. Fetch Data & Distribute Cards
        StudentDAO dao = new StudentDAO();
        List<EnrolledCourse> courses = dao.getEnrolledCourses(student.getStudentId());

        for (EnrolledCourse c : courses) {
            if (c.getDay() == null || c.getDay().isEmpty()) continue;
            if (c.getGrade() > 0) continue;

            Pane card = createClassCard(c,student);

            // Sort into correct column
            switch (c.getDay()) {
                case "Monday": monCol.getChildren().add(card); break;
                case "Tuesday": tueCol.getChildren().add(card); break;
                case "Wednesday": wedCol.getChildren().add(card); break;
                case "Thursday": thuCol.getChildren().add(card); break;
                case "Friday": friCol.getChildren().add(card); break;
            }
        }

        // Ensure columns grow evenly
        HBox.setHgrow(monCol, Priority.ALWAYS);
        HBox.setHgrow(tueCol, Priority.ALWAYS);
        HBox.setHgrow(wedCol, Priority.ALWAYS);
        HBox.setHgrow(thuCol, Priority.ALWAYS);
        HBox.setHgrow(friCol, Priority.ALWAYS);

        weekGrid.getChildren().addAll(monCol, tueCol, wedCol, thuCol, friCol);
        content.getChildren().addAll(backBtn, title, weekGrid);
        return content;
    }

    // Helper: Create a Column for a specific Day
    private VBox createDayColumn(String dayName) {
        VBox col = new VBox(10);
        col.setPadding(new Insets(10));
        col.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 5;"); // Light grey background
        col.setPrefHeight(500);

        Label header = new Label(dayName);
        header.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        header.setTextFill(Color.web("#7f8c8d"));
        header.setAlignment(Pos.CENTER);
        header.setMaxWidth(Double.MAX_VALUE);

        col.getChildren().add(header);
        return col;
    }

    // Helper: Create a specific Card for a Class
    private Pane createClassCard(EnrolledCourse c, Student student) {
        VBox card = new VBox(8); // Increased spacing slightly
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #3498db; -fx-border-width: 0 0 0 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Label code = new Label(c.getCourseCode());
        code.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        code.setTextFill(Color.web("#2c3e50"));

        Label name = new Label(c.getCourseName());
        name.setFont(Font.font("Segoe UI", 12));
        name.setWrapText(true);

        Label time = new Label("🕒 " + c.getStartTime() + " - " + c.getEndTime());
        time.setFont(Font.font("Segoe UI", 11));
        time.setTextFill(Color.web("#7f8c8d"));

        Label room = new Label("📍 " + c.getRoom());
        room.setFont(Font.font("Segoe UI", 11));
        room.setTextFill(Color.web("#7f8c8d"));

        // --- NEW: Report Issue Button ---
        Button reportBtn = new Button("⚠ Report Issue");
        reportBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #e74c3c; -fx-font-size: 11px; -fx-cursor: hand; -fx-padding: 0;");

        reportBtn.setOnAction(e -> {
            // check if hall exists
            if (c.getHallId() == 0) {
                new Alert(Alert.AlertType.ERROR, "Cannot report issue: No valid hall assigned.").show();
                return;
            }

            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Report Room Issue");
            dialog.setHeaderText("Report an issue with " + c.getRoom());
            dialog.setContentText("Describe the problem:");

            dialog.showAndWait().ifPresent(description -> {
                if (description.trim().isEmpty()) return;

                StudentDAO dao = new StudentDAO();
                boolean success = dao.reportIssue(student.getStudentId(), c.getHallId(), description);

                if (success) {
                    new Alert(Alert.AlertType.INFORMATION, "Issue reported successfully to Admin.").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to report issue.").show();
                }
            });
        });

        card.getChildren().addAll(code, name, new Separator(), time, room, reportBtn);
        return card;
    }

    // =========================================
    // NEW: Fees & Financials View
    // =========================================
    private VBox createFeesView(Student student, BorderPane root) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.TOP_LEFT);

        // 1. Header
        Button backBtn = new Button("← Back");
        backBtn.setStyle(
                "-fx-background-color: #d3d3d3;" +
                        "-fx-text-fill: #2c3e50;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
        );
        backBtn.setOnAction(e -> root.setCenter(createMainContent(student, root)));

        Label title = new Label("Tuition & Fees");
        title.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 28));
        title.setTextFill(Color.web("#2c3e50"));

        StudentDAO dao = new StudentDAO();
        Student updatedStudent1=dao.getStudentById(student.getStudentId());

        // 2. GET DATA SEPARATELY
        // Wallet = Money you HAVE.
        // AmountToBePaid = Money you OWE.
        double currentWallet = updatedStudent1.getWallet();
        double currentDebt = updatedStudent1.getAmountToBePaid();

        // 3. Financial Cards Row
        HBox financeRow = new HBox(20);
        financeRow.setAlignment(Pos.CENTER_LEFT);

        // --- BOX 1: WALLET (Available Funds) ---
        // Green if you have money, Grey if empty
        String walletColor = currentWallet > 0 ? "#2ecc71" : "#95a5a6";
        Pane walletCard = createStatCard("WALLET (AVAILABLE)", String.format("%,.0f EGP", currentWallet), "Pre-Paid", walletColor);
        walletCard.setPrefWidth(350);

        // --- BOX 2: AMOUNT TO BE PAID (Debt) ---
        // Red if you owe money, Green (Settled) if 0
        String payColor = currentDebt > 0 ? "#e74c3c" : "#2ecc71";
        String payBadge = currentDebt > 0 ? "Due Now" : "Settled";
        Pane amountCard = createStatCard("AMOUNT TO BE PAID", String.format("%,.0f EGP", currentDebt), payBadge, payColor);
        amountCard.setPrefWidth(350);

        financeRow.getChildren().addAll(walletCard, amountCard);

        // 4. Payment Action Section
        VBox paySection = new VBox(10);
        paySection.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        paySection.setMaxWidth(720);

        Label payTitle = new Label("Make a Payment (Deducts from Wallet)");
        payTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        TextField amountField = new TextField();
        amountField.setPromptText("Enter Amount (EGP)");
        amountField.setMaxWidth(300);

        Button payBtn = new Button("Pay Securely");
        payBtn.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        Label statusLabel = new Label();

        payBtn.setOnAction(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                if (amount <= 0) throw new NumberFormatException();

                // Perform Payment (Updates DB: Wallet decreases, Debt decreases)
                boolean success = dao.payTuition(student.getStudentId(), amount);

                if (success) {
                    // IMPORTANT: RELOAD STUDENT DATA
                    // The current 'student' object still has the old numbers.
                    // We must fetch the updated values from the database to see the changes.
                    Student updatedStudent = dao.getStudentById(student.getStudentId());

                    // Refresh view with the NEW student object
                    root.setCenter(createFeesView(updatedStudent, root));

                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Payment Successful! Wallet and Debt updated.");
                    alert.show();
                } else {
                    statusLabel.setText("Payment Failed. Check Wallet Balance.");
                    statusLabel.setTextFill(Color.RED);
                }
            } catch (NumberFormatException ex) {
                statusLabel.setText("Invalid Amount.");
                statusLabel.setTextFill(Color.RED);
            }
        });

        paySection.getChildren().addAll(payTitle, amountField, payBtn, statusLabel);

        content.getChildren().addAll(backBtn, title, financeRow, paySection);
        return content;
    }

    private VBox createBookingView(Student student, BorderPane root) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Book a Hall");
        title.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 28));
        title.setTextFill(Color.web("#2c3e50"));

        // Form
        GridPane grid = new GridPane();
        grid.setHgap(15); grid.setVgap(15);

        // 1. Select Hall
        StudentDAO dao = new StudentDAO();
        ComboBox<Hall> hallBox = new ComboBox<>();
        hallBox.getItems().addAll(dao.getAllHalls());
        hallBox.setPromptText("Select a Hall");
        hallBox.setPrefWidth(250);
        // Display Hall Name
        hallBox.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Hall h) { return h == null ? "" : h.getHallName() + " (" + h.getHallType() + ")"; }
            public Hall fromString(String s) { return null; }
        });

        // 2. Select Date
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Select Date");

        // 3. Time Inputs
        TextField startField = new TextField(); startField.setPromptText("09:00 AM");
        TextField endField = new TextField(); endField.setPromptText("10:30 AM");

        // 4. Purpose
        TextField purposeField = new TextField(); purposeField.setPromptText("Reason (e.g. Study Group)");
        purposeField.setPrefWidth(250);

        Button bookBtn = new Button("Confirm Booking");
        bookBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        // Add to Grid
        grid.add(new Label("Hall:"), 0, 0); grid.add(hallBox, 1, 0);
        grid.add(new Label("Date:"), 0, 1); grid.add(datePicker, 1, 1);
        grid.add(new Label("Start Time:"), 0, 2); grid.add(startField, 1, 2);
        grid.add(new Label("End Time:"), 0, 3); grid.add(endField, 1, 3);
        grid.add(new Label("Purpose:"), 0, 4); grid.add(purposeField, 1, 4);
        grid.add(bookBtn, 1, 5);

        // Logic
        bookBtn.setOnAction(e -> {
            Hall hall = hallBox.getValue();
            java.time.LocalDate date = datePicker.getValue();
            String start = startField.getText().trim();
            String end = endField.getText().trim();
            String purpose = purposeField.getText().trim();

            if (hall == null || date == null || start.isEmpty() || end.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Please fill all fields.").show();
                return;
            }

            // Call DAO
            boolean success = dao.bookHall(student.getStudentId(), hall.getHallId(), date, start, end, purpose);

            if (success) {
                new Alert(Alert.AlertType.INFORMATION, "Hall booked successfully!").showAndWait();
                // Clear fields
                startField.clear(); endField.clear(); purposeField.clear();
            } else {
                new Alert(Alert.AlertType.ERROR, "Booking Failed. Hall might be occupied or time format invalid (Use HH:mm AM).").show();
            }
        });

        content.getChildren().addAll(title, new Label("Reserve a hall during free time slots."), grid);
        return content;
    }

    private VBox createHallInfoView(Student student) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Halls & Schedules");
        title.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 28));
        title.setTextFill(Color.web("#2c3e50"));

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // --- TAB 1: My Bookings ---
        Tab myBookingsTab = new Tab("My Bookings");
        VBox myBookingsBox = new VBox(15);
        myBookingsBox.setPadding(new Insets(20));

        TableView<HallBooking> myTable = new TableView<>();
        myTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<HallBooking, String> hCol = new TableColumn<>("Hall");
        hCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("hallName"));

        TableColumn<HallBooking, String> dCol = new TableColumn<>("Date");
        dCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("bookingDate"));

        TableColumn<HallBooking, String> tCol = new TableColumn<>("Time");
        tCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getStartTime() + " - " + cell.getValue().getEndTime()
        ));

        TableColumn<HallBooking, String> sCol = new TableColumn<>("Status");
        sCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("status"));

        myTable.getColumns().addAll(hCol, dCol, tCol, sCol);

        // Load Data
        StudentDAO dao = new StudentDAO();
        myTable.getItems().addAll(dao.getStudentBookings(student.getStudentId()));

        myBookingsBox.getChildren().addAll(new Label("History of your requests:"), myTable);
        myBookingsTab.setContent(myBookingsBox);


        // --- TAB 2: Check Schedule ---
        Tab scheduleTab = new Tab("Check Hall Availability");
        VBox scheduleBox = new VBox(15);
        scheduleBox.setPadding(new Insets(20));

        // Filters
        HBox filters = new HBox(15);
        filters.setAlignment(Pos.CENTER_LEFT);

        ComboBox<Hall> hallPicker = new ComboBox<>();
        hallPicker.getItems().addAll(dao.getAllHalls());
        hallPicker.setPromptText("Select Hall");
        hallPicker.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Hall h) { return h == null ? "" : h.getHallName(); }
            public Hall fromString(String s) { return null; }
        });

        DatePicker datePicker = new DatePicker(java.time.LocalDate.now());
        Button searchBtn = new Button("Show Schedule");
        searchBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");

        filters.getChildren().addAll(new Label("Hall:"), hallPicker, new Label("Date:"), datePicker, searchBtn);

        // Schedule Table
        TableView<HallBooking> schedTable = new TableView<>();
        schedTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        schedTable.setPlaceholder(new Label("Select a Hall and Date to see busy slots"));

        TableColumn<HallBooking, String> timeCol = new TableColumn<>("Time Slot");
        timeCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getStartTime() + " - " + cell.getValue().getEndTime()
        ));

        TableColumn<HallBooking, String> infoCol = new TableColumn<>("Status / Class");
        infoCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("status"));

        schedTable.getColumns().addAll(timeCol, infoCol);

        // Search Logic
        searchBtn.setOnAction(e -> {
            schedTable.getItems().clear();
            if (hallPicker.getValue() != null && datePicker.getValue() != null) {
                List<HallBooking> schedule = dao.getHallSchedule(hallPicker.getValue().getHallId(), datePicker.getValue());
                if (schedule.isEmpty()) {
                    schedTable.setPlaceholder(new Label("Hall is completely free on this day!"));
                } else {
                    schedTable.getItems().addAll(schedule);
                }
            }
        });

        scheduleBox.getChildren().addAll(filters, schedTable);
        scheduleTab.setContent(scheduleBox);

        // Add Tabs
        tabs.getTabs().addAll(scheduleTab, myBookingsTab); // Schedule first as requested
        content.getChildren().addAll(title, tabs);
        return content;
    }

}