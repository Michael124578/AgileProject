package UI;

import DAO.ParentDAO;
import DAO.StudentDAO;
import DAO.TeacherDAO;
import Model.*;
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
        Button scheduleBtn = createNavButton("My Schedule", false);
        Button lmsBtn = createNavButton("LMS ->", false);
        Button parentmsgBtn = createNavButton("Parent Messages", false);
        Button profileBtn = createNavButton("My Profile", false); // NEW
        Button logoutBtn = createNavButton("Logout", false);

        // Actions
        dashBtn.setOnAction(e -> {
            root.setCenter(createMainContent(teacher, root));
            setActive(dashBtn, profileBtn, logoutBtn);
        });

        parentmsgBtn.setOnAction(e -> {
            root.setCenter(createParentMessagesView(teacher));
            setActive(parentmsgBtn, dashBtn, scheduleBtn, profileBtn, logoutBtn);
        });

        scheduleBtn.setOnAction(e -> {
            root.setCenter(createScheduleView(teacher, root));
            setActive(scheduleBtn, dashBtn, profileBtn, logoutBtn);
        });
        lmsBtn.setOnAction(e -> {
            root.setCenter(new LMSTeacherView().createView(teacher));
            setActive(lmsBtn, dashBtn, scheduleBtn, profileBtn, logoutBtn);
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

        navMenu.getChildren().addAll(dashBtn,scheduleBtn,lmsBtn,parentmsgBtn, profileBtn, logoutBtn);
        sidebar.getChildren().addAll(profilePic, nameLabel, deptLabel, new Region(), navMenu);
        return sidebar;
    }

    private HBox createParentMessagesView(Teacher teacher) {
        HBox content = new HBox(10);
        content.setPadding(new Insets(20));

        // Left: Parent List
        ListView<Parent> parentList = new ListView<>();
        ParentDAO dao = new ParentDAO();
        parentList.getItems().addAll(dao.getParentsContactingTeacher(teacher.getTeacherId()));
        parentList.setPrefWidth(250);

        // --- NEW CODE START: Format the list items ---
        parentList.setCellFactory(param -> new ListCell<Parent>() {
            @Override
            protected void updateItem(Parent item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // Format: "George John (Fady John)"
                    setText(item.getFirstName() + " " + item.getLastName() + " (" + item.getStudentName() + ")");
                }
            }
        });
        // --- NEW CODE END ---

        // Right: Chat
        VBox chatBox = new VBox(10);
        chatBox.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #ccc;");
        HBox.setHgrow(chatBox, Priority.ALWAYS);

        TextArea conversation = new TextArea();
        conversation.setEditable(false);
        VBox.setVgrow(conversation, Priority.ALWAYS);

        HBox inputBox = new HBox(10);
        TextField messageField = new TextField();
        HBox.setHgrow(messageField, Priority.ALWAYS);
        Button sendBtn = new Button("Reply");
        sendBtn.setDisable(true);

        inputBox.getChildren().addAll(messageField, sendBtn);
        chatBox.getChildren().addAll(new Label("Chat with Parent"), conversation, inputBox);

        parentList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, parent) -> {
            if (parent != null) {
                sendBtn.setDisable(false);
                refreshChat(conversation, parent.getParentId(), teacher.getTeacherId());
            }
        });

        sendBtn.setOnAction(e -> {
            Parent selected = parentList.getSelectionModel().getSelectedItem();
            if (selected != null && !messageField.getText().trim().isEmpty()) {
                dao.sendMessage(selected.getParentId(), teacher.getTeacherId(), messageField.getText(), "Teacher");
                messageField.clear();
                refreshChat(conversation, selected.getParentId(), teacher.getTeacherId());
            }
        });

        // Use a VBox for the list side to keep the label above the list
        VBox listSide = new VBox(5);
        listSide.getChildren().addAll(new Label("Select Parent:"), parentList);

        content.getChildren().addAll(listSide, chatBox);
        return content;
    }

    private void refreshChat(TextArea area, int pid, int tid) {
        ParentDAO dao = new ParentDAO();
        List<Message> msgs = dao.getConversation(pid, tid);
        StringBuilder sb = new StringBuilder();
        for (Message m : msgs) {
            sb.append(m.getFormatted()).append("\n");
        }
        area.setText(sb.toString());
    }

    private VBox createScheduleView(Teacher teacher, BorderPane root) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.TOP_LEFT);

        // Header
        Label title = new Label("My Class Schedule");
        title.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 28));
        title.setTextFill(Color.web("#2c3e50"));

        // The Weekly Grid (5 Columns)
        HBox weekGrid = new HBox(15);
        weekGrid.setAlignment(Pos.TOP_CENTER);

        VBox monCol = createDayColumn("Monday");
        VBox tueCol = createDayColumn("Tuesday");
        VBox wedCol = createDayColumn("Wednesday");
        VBox thuCol = createDayColumn("Thursday");
        VBox friCol = createDayColumn("Friday");

        // Fetch Data
        TeacherDAO dao = new TeacherDAO();
        List<Course> courses = dao.getTeacherCourses(teacher.getTeacherId());

        for (Course c : courses) {
            if (c.getDayOfWeek() == null || c.getDayOfWeek().isEmpty()) continue;

            Pane card = createClassCard(c,teacher);

            // Sort into correct column
            switch (c.getDayOfWeek()) {
                case "Monday": monCol.getChildren().add(card); break;
                case "Tuesday": tueCol.getChildren().add(card); break;
                case "Wednesday": wedCol.getChildren().add(card); break;
                case "Thursday": thuCol.getChildren().add(card); break;
                case "Friday": friCol.getChildren().add(card); break;
            }
        }

        HBox.setHgrow(monCol, Priority.ALWAYS);
        HBox.setHgrow(tueCol, Priority.ALWAYS);
        HBox.setHgrow(wedCol, Priority.ALWAYS);
        HBox.setHgrow(thuCol, Priority.ALWAYS);
        HBox.setHgrow(friCol, Priority.ALWAYS);

        weekGrid.getChildren().addAll(monCol, tueCol, wedCol, thuCol, friCol);
        content.getChildren().addAll(title, weekGrid);
        return content;
    }

    // Helper: Create Column
    private VBox createDayColumn(String dayName) {
        VBox col = new VBox(10);
        col.setPadding(new Insets(10));
        col.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 5;");
        col.setPrefHeight(500);

        Label header = new Label(dayName);
        header.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        header.setTextFill(Color.web("#7f8c8d"));
        header.setAlignment(Pos.CENTER);
        header.setMaxWidth(Double.MAX_VALUE);

        col.getChildren().add(header);
        return col;
    }

    // Helper: Create Card
    // Helper: Create Card (UPDATED)
    private Pane createClassCard(Course c, Teacher teacher) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(15));
        // Green border for teachers
        card.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #27ae60; -fx-border-width: 0 0 0 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Label code = new Label(c.getCourseCode());
        code.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        code.setTextFill(Color.web("#2c3e50"));

        Label name = new Label(c.getCourseName());
        name.setFont(Font.font("Segoe UI", 12));
        name.setWrapText(true);

        Label time = new Label("🕒 " + c.getStartTime() + " - " + c.getEndTime());
        time.setFont(Font.font("Segoe UI", 11));
        time.setTextFill(Color.web("#7f8c8d"));

        Label room = new Label("📍 " + c.getRoomNumber());
        room.setFont(Font.font("Segoe UI", 11));
        room.setTextFill(Color.web("#7f8c8d"));

        // --- NEW: Report Issue Button ---
        Button reportBtn = new Button("⚠ Report Issue");
        reportBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #e74c3c; -fx-font-size: 11px; -fx-cursor: hand; -fx-padding: 0;");

        reportBtn.setOnAction(e -> {
            if (c.getHallId() == 0) {
                new Alert(Alert.AlertType.ERROR, "Cannot report issue: No valid hall assigned.").show();
                return;
            }

            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Report Room Issue");
            dialog.setHeaderText("Report an issue with " + c.getRoomNumber());
            dialog.setContentText("Describe the problem:");

            dialog.showAndWait().ifPresent(description -> {
                if (description.trim().isEmpty()) return;

                TeacherDAO dao = new TeacherDAO();
                // Pass teacher ID and Hall ID
                boolean success = dao.reportIssue(teacher.getTeacherId(), c.getHallId(), description);

                if (success) {
                    new Alert(Alert.AlertType.INFORMATION, "Issue reported successfully.").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to report issue.").show();
                }
            });
        });

        card.getChildren().addAll(code, name, new Separator(), time, room, reportBtn);
        return card;
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
        card.setPrefSize(260, 180); // Made slightly taller for 2 buttons
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Label code = new Label(course.getCourseCode());
        code.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        code.setTextFill(Color.web("#3498db"));

        Label name = new Label(course.getCourseName());
        name.setWrapText(true);
        name.setFont(Font.font("Segoe UI", 14));

        // --- BUTTONS ---
        HBox btnBox = new HBox(10);
        btnBox.setAlignment(Pos.CENTER);

        Button gradeBtn = new Button("Grades");
        gradeBtn.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-cursor: hand;");
        gradeBtn.setOnAction(e -> root.setCenter(createGradingView(teacher, course, root)));

        Button statsBtn = new Button("Stats");
        statsBtn.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-cursor: hand;"); // Orange button
        statsBtn.setOnAction(e -> root.setCenter(createStatisticsView(teacher, course, root)));

        btnBox.getChildren().addAll(gradeBtn, statsBtn);

        card.getChildren().addAll(code, name, new Separator(), btnBox);
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

    // =========================================
    // 7. NEW: Statistics View (Pie Chart)
    // =========================================
    private VBox createStatisticsView(Teacher teacher, Course course, BorderPane root) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.TOP_CENTER);

        // Header with Back Button
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        Button backBtn = new Button("← Back");
        backBtn.setStyle(
                "-fx-background-color: #d3d3d3;" +
                        "-fx-text-fill: #2c3e50;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
        );        backBtn.setOnAction(e -> root.setCenter(createMainContent(teacher, root)));

        Label title = new Label("Analytics: " + course.getCourseCode());
        title.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 28));
        title.setTextFill(Color.web("#2c3e50"));

        header.getChildren().addAll(backBtn, title);

        // Fetch Data from DAO
        TeacherDAO dao = new TeacherDAO();
        int[] stats = dao.getGradeDistribution(course.getCourseId());

        // Check if class is empty
        int totalStudents = stats[0] + stats[1] + stats[2] + stats[3] + stats[4];
        if (totalStudents == 0) {
            content.getChildren().addAll(header, new Label("No graded students yet."));
            return content;
        }

        // Create Pie Chart Data
        javafx.scene.chart.PieChart.Data d1 = new javafx.scene.chart.PieChart.Data("A (90-100)", stats[0]);
        javafx.scene.chart.PieChart.Data d2 = new javafx.scene.chart.PieChart.Data("B (80-89)", stats[1]);
        javafx.scene.chart.PieChart.Data d3 = new javafx.scene.chart.PieChart.Data("C (70-79)", stats[2]);
        javafx.scene.chart.PieChart.Data d4 = new javafx.scene.chart.PieChart.Data("D (60-69)", stats[3]);
        javafx.scene.chart.PieChart.Data d5 = new javafx.scene.chart.PieChart.Data("F (<60)", stats[4]);

        javafx.scene.chart.PieChart chart = new javafx.scene.chart.PieChart();
        chart.getData().addAll(d1, d2, d3, d4, d5);
        chart.setTitle("Grade Distribution");
        chart.setLegendSide(javafx.geometry.Side.RIGHT);

        // Summary Text
        Label summary = new Label("Total Graded Students: " + totalStudents);
        summary.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        content.getChildren().addAll(header, chart, summary);
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