package UI;

import DAO.AdminDAO;
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
import javafx.util.StringConverter;

import java.io.InputStream;

public class AdminDashboard {

    public void show(Stage stage, Admin admin) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f4f6f9;");

        root.setTop(createTopBar());
        root.setLeft(createSidebar(admin, root));
        root.setCenter(createTeachersView(root)); // Default View

        Scene scene = new Scene(root, 1200, 700);
        stage.setTitle("Admin Dashboard - " + admin.getFullName());
        stage.setScene(scene);
        stage.show();
    }

    // =========================================
    // 1. Top Bar
    // =========================================
    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(10, 20, 10, 20));
        topBar.setStyle("-fx-background-color: #2c3e50;");
        topBar.setAlignment(Pos.CENTER_LEFT);

        ImageView logo = loadImageView("/images/sis_logo.png", 30, 30);
        Label brandLabel = new Label("SIS Admin Panel");
        brandLabel.setTextFill(Color.WHITE);
        brandLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));

        topBar.getChildren().addAll(logo, brandLabel);
        return topBar;
    }

    // =========================================
    // 2. Sidebar
    // =========================================
    private VBox createSidebar(Admin admin, BorderPane root) {

        VBox sidebar = new VBox(15);
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: #34495e;");
        sidebar.setPadding(new Insets(30, 10, 20, 10));
        sidebar.setAlignment(Pos.TOP_CENTER);

        // --- Profile Pic ---
        ImageView profilePic = new ImageView();
        profilePic.setFitWidth(80); profilePic.setFitHeight(80);
        try {
            if (admin.getProfilePicPath() != null && !admin.getProfilePicPath().isEmpty())
                profilePic.setImage(new Image("file:///" + admin.getProfilePicPath().replace("\\", "/")));
            else
                profilePic.setImage(new Image(getClass().getResourceAsStream("/images/student_placeholder.png")));
        } catch (Exception e) {
            try { profilePic.setImage(new Image(getClass().getResourceAsStream("/images/student_placeholder.png"))); } catch(Exception ignored){}
        }
        Circle clip = new Circle(40, 40, 40);
        profilePic.setClip(clip);

        // --- Name ---
        String displayName = (admin.getFullName() != null && !admin.getFullName().isEmpty()) ? admin.getFullName() : admin.getUsername();
        Label nameLabel = new Label(displayName);
        nameLabel.setTextFill(Color.WHITE);
        nameLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        // --- Menu Buttons ---
        VBox navMenu = new VBox(5);
        Button teachBtn = createNavButton("Manage Teachers", true);
        Button hallBtn = createNavButton("Manage Halls", false); // NEW BUTTON
        Button courseBtn = createNavButton("Manage Courses", false);
        Button assignBtn = createNavButton("Assign Teachers", false);
        Button issueBtn = createNavButton("Reported Issues", false); // NEW BUTTON
        Button regBtn = createNavButton("Register Student/Parent", false);
        Button financeBtn = createNavButton("Student Finances/Deletion", false);
        Button profileBtn = createNavButton("My Profile", false);
        Button logoutBtn = createNavButton("Logout", false);

        // Actions
        teachBtn.setOnAction(e -> {
            root.setCenter(createTeachersView(root));
            setActive(teachBtn, hallBtn, courseBtn, assignBtn, issueBtn, regBtn, financeBtn, profileBtn, logoutBtn);
        });

        hallBtn.setOnAction(e -> {
            root.setCenter(createHallsView(root));
            setActive(hallBtn, teachBtn, courseBtn, assignBtn, issueBtn, regBtn, financeBtn, profileBtn, logoutBtn);
        });

        courseBtn.setOnAction(e -> {
            root.setCenter(createCoursesView(root));
            setActive(courseBtn, teachBtn, hallBtn, assignBtn, issueBtn, regBtn, financeBtn, profileBtn, logoutBtn);
        });

        assignBtn.setOnAction(e -> {
            root.setCenter(createAssignmentView(root));
            setActive(assignBtn, teachBtn, hallBtn, courseBtn, issueBtn, regBtn, financeBtn, profileBtn, logoutBtn);
        });

        issueBtn.setOnAction(e -> {
            root.setCenter(createIssuesView(root));
            setActive(issueBtn, teachBtn, hallBtn, courseBtn, assignBtn, regBtn, financeBtn, profileBtn, logoutBtn);
        });

        regBtn.setOnAction(e -> {
            root.setCenter(createStudentParentRegisterView(root));
            setActive(regBtn, teachBtn, hallBtn, courseBtn, assignBtn, issueBtn, financeBtn, profileBtn, logoutBtn);
        });

        financeBtn.setOnAction(e -> {
            root.setCenter(createFinancesView(root));
            setActive(financeBtn, teachBtn, hallBtn, courseBtn, assignBtn, issueBtn, regBtn, profileBtn, logoutBtn);
        });

        profileBtn.setOnAction(e -> {
            root.setCenter(createProfileView(admin, root));
            setActive(profileBtn, teachBtn, hallBtn, courseBtn, assignBtn, issueBtn, regBtn, financeBtn, logoutBtn);
        });

        logoutBtn.setOnAction(e -> {
            Stage current = (Stage) logoutBtn.getScene().getWindow();
            current.close();
            try { new LoginScreen().show(new Stage()); } catch (Exception ex) {}
        });

        navMenu.getChildren().addAll(teachBtn, hallBtn, courseBtn, issueBtn,regBtn, assignBtn, financeBtn, profileBtn, logoutBtn);
        sidebar.getChildren().addAll(profilePic, nameLabel, new Region(), navMenu);
        return sidebar;
    }

    // =========================================
    // VIEW A: Manage Teachers
    // =========================================
    private VBox createTeachersView(BorderPane root) {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label title = new Label("Manage Teachers");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));

        // 1. Add Teacher Form
        HBox form = new HBox(10);
        TextField fName = new TextField(); fName.setPromptText("First Name");
        TextField lName = new TextField(); lName.setPromptText("Last Name");
        TextField email = new TextField(); email.setPromptText("Email");
        TextField dept = new TextField(); dept.setPromptText("Department");
        PasswordField pass = new PasswordField(); pass.setPromptText("Password");

        Button addBtn = new Button("Add Teacher");
        addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");

        form.getChildren().addAll(fName, lName, email, dept, pass, addBtn);

        // 2. Teachers Table
        TableView<Teacher> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Teacher, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("teacherId"));

        TableColumn<Teacher, String> fnCol = new TableColumn<>("First Name");
        fnCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("firstName"));

        TableColumn<Teacher, String> lnCol = new TableColumn<>("Last Name");
        lnCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("lastName"));

        TableColumn<Teacher, String> emCol = new TableColumn<>("Email");
        emCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("email"));

        TableColumn<Teacher, Void> delCol = new TableColumn<>("Delete");
        delCol.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("X");
            {
                btn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                btn.setOnAction(e -> {
                    Teacher t = getTableView().getItems().get(getIndex());
                    new AdminDAO().deleteTeacher(t.getTeacherId());
                    root.setCenter(createTeachersView(root)); // Refresh
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        table.getColumns().addAll(idCol, fnCol, lnCol, emCol, delCol);

        // Load Data
        AdminDAO dao = new AdminDAO();
        table.getItems().addAll(dao.getAllTeachers());

        // Add Logic
        addBtn.setOnAction(e -> {
            if(dao.addTeacher(fName.getText(), lName.getText(), email.getText(), dept.getText(), pass.getText())) {
                root.setCenter(createTeachersView(root)); // Refresh
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to add teacher.").show();
            }
        });

        content.getChildren().addAll(title, form, table);
        return content;
    }

    // =========================================
    // VIEW B: Manage Halls (NEW)
    // =========================================
    private VBox createHallsView(BorderPane root) {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label title = new Label("Manage Halls");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));

        // 1. Add Hall Form
        HBox form = new HBox(10);
        TextField hallName = new TextField(); hallName.setPromptText("Hall Name (e.g. Room 101)");
        TextField capacity = new TextField(); capacity.setPromptText("Capacity");
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Classroom", "Lab", "Lecture Hall");
        typeBox.setPromptText("Type");
        typeBox.getSelectionModel().selectFirst();

        Button addBtn = new Button("Add Hall");
        addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");

        form.getChildren().addAll(hallName, capacity, typeBox, addBtn);

        // 2. Halls Table
        TableView<Hall> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Hall, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("hallId"));

        TableColumn<Hall, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("hallName"));

        TableColumn<Hall, Integer> capCol = new TableColumn<>("Capacity");
        capCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("capacity"));

        TableColumn<Hall, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("hallType"));

        TableColumn<Hall, Void> delCol = new TableColumn<>("Delete");
        delCol.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("X");
            {
                btn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                btn.setOnAction(e -> {
                    Hall h = getTableView().getItems().get(getIndex());
                    new AdminDAO().deleteHall(h.getHallId());
                    root.setCenter(createHallsView(root)); // Refresh
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        table.getColumns().addAll(idCol, nameCol, capCol, typeCol, delCol);

        // Load Data
        AdminDAO dao = new AdminDAO();
        table.getItems().addAll(dao.getAllHalls());

        // Add Logic
        addBtn.setOnAction(e -> {
            try {
                int cap = Integer.parseInt(capacity.getText());
                if(dao.addHall(hallName.getText(), cap, typeBox.getValue())) {
                    root.setCenter(createHallsView(root)); // Refresh
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to add hall. Name might be duplicate.").show();
                }
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Invalid Capacity").show();
            }
        });

        content.getChildren().addAll(title, form, table);
        return content;
    }

    // =========================================
    // VIEW C: Manage Courses (Updated with Hall Selection)
    // =========================================
    private VBox createCoursesView(BorderPane root) {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label title = new Label("Manage Courses");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));

        AdminDAO dao = new AdminDAO();

        // 1. Add Course Form
        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(10);

        TextField code = new TextField(); code.setPromptText("Course Code (CS101)");
        TextField name = new TextField(); name.setPromptText("Course Name");
        TextField cred = new TextField(); cred.setPromptText("Credits");

        ComboBox<String> dayBox = new ComboBox<>();
        dayBox.getItems().addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
        dayBox.setPromptText("Day");

        TextField start = new TextField(); start.setPromptText("Start (09:00 AM)");
        TextField end = new TextField(); end.setPromptText("End (10:30 AM)");

        // --- NEW: Hall Selection ---
        ComboBox<Hall> hallBox = new ComboBox<>();
        hallBox.getItems().addAll(dao.getAllHalls());
        hallBox.setPromptText("Select Hall");
        hallBox.setPrefWidth(150);
        // Converter to show Hall Name in dropdown
        hallBox.setConverter(new StringConverter<>() {
            @Override public String toString(Hall hall) { return hall == null ? "" : hall.getHallName(); }
            @Override public Hall fromString(String string) { return null; }
        });

        Button addBtn = new Button("Create Course");
        addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");

        form.addRow(0, code, name, cred, dayBox);
        form.addRow(1, start, end, hallBox, addBtn); // Replaced Room TextField with Hall ComboBox

        // 2. Courses Table
        TableView<Course> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Course, String> cCode = new TableColumn<>("Code");
        cCode.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("courseCode"));

        TableColumn<Course, String> cName = new TableColumn<>("Name");
        cName.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("courseName"));

        TableColumn<Course, Integer> cCred = new TableColumn<>("Cr");
        cCred.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("credits"));

        // Added Room Column
        TableColumn<Course, String> cRoom = new TableColumn<>("Room");
        cRoom.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("roomNumber")); // Displays Hall Name

        TableColumn<Course, Void> delCol = new TableColumn<>("Delete");
        delCol.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("X");
            {
                btn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                btn.setOnAction(e -> {
                    Course c = getTableView().getItems().get(getIndex());
                    new AdminDAO().deleteCourse(c.getCourseId());
                    root.setCenter(createCoursesView(root)); // Refresh
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        table.getColumns().addAll(cCode, cName, cCred, cRoom, delCol);

        table.getItems().addAll(dao.getAllCourses());

        // Logic
        addBtn.setOnAction(e -> {
            Hall selectedHall = hallBox.getValue();
            if (selectedHall == null) {
                new Alert(Alert.AlertType.ERROR, "Please select a Hall.").show();
                return;
            }
            try {
                int cr = Integer.parseInt(cred.getText());
                // Pass Hall ID to the DAO
                if(dao.addCourse(code.getText(), name.getText(), cr, dayBox.getValue(), start.getText(), end.getText(), selectedHall.getHallId())) {
                    root.setCenter(createCoursesView(root));
                }
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Invalid Data").show();
            }
        });

        content.getChildren().addAll(title, form, table);
        return content;
    }

    // =========================================
    // VIEW D: Assign Teachers
    // =========================================
    private VBox createAssignmentView(BorderPane root) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(40));
        content.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Assign Teacher to Course");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));

        AdminDAO dao = new AdminDAO();

        // 1. Teacher Dropdown
        ComboBox<Teacher> teacherBox = new ComboBox<>();
        teacherBox.getItems().addAll(dao.getAllTeachers());
        teacherBox.setPromptText("Select Teacher");
        teacherBox.setPrefWidth(300);

        teacherBox.setConverter(new StringConverter<>() {
            public String toString(Teacher t) { return t == null ? "" : t.getFirstName() + " " + t.getLastName(); }
            public Teacher fromString(String s) { return null; }
        });

        // 2. Course Dropdown
        ComboBox<Course> courseBox = new ComboBox<>();
        courseBox.getItems().addAll(dao.getAllCourses());
        courseBox.setPromptText("Select Course");
        courseBox.setPrefWidth(300);

        courseBox.setConverter(new StringConverter<>() {
            public String toString(Course c) { return c == null ? "" : c.getCourseCode() + " - " + c.getCourseName(); }
            public Course fromString(String s) { return null; }
        });

        TextField sem = new TextField(); sem.setPromptText("Semester (e.g. Spring)"); sem.setMaxWidth(300);
        TextField year = new TextField(); year.setPromptText("Year (e.g. 2026)"); year.setMaxWidth(300);

        Button assignBtn = new Button("Assign");
        assignBtn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold;");
        assignBtn.setPrefWidth(200);

        assignBtn.setOnAction(e -> {
            Teacher t = teacherBox.getValue();
            Course c = courseBox.getValue();
            if(t != null && c != null) {
                try {
                    int y = Integer.parseInt(year.getText());
                    if(dao.assignTeacherToCourse(t.getTeacherId(), c.getCourseId(), sem.getText(), y)) {
                        new Alert(Alert.AlertType.INFORMATION, "Assigned Successfully!").show();
                    }
                } catch (Exception ex) { new Alert(Alert.AlertType.ERROR, "Invalid Data").show(); }
            }
        });

        content.getChildren().addAll(title, new Label("Teacher:"), teacherBox, new Label("Course:"), courseBox, sem, year, assignBtn);
        return content;
    }

    // =========================================
    // VIEW E: Admin Profile
    // =========================================
    private VBox createProfileView(Admin admin, BorderPane root) {
        VBox content = new VBox(15);
        content.setPadding(new Insets(40));
        content.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Edit Admin Profile");
        title.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 28));
        title.setTextFill(Color.web("#2c3e50"));

        // --- Pic Section ---
        HBox picSection = new HBox(20);
        picSection.setAlignment(Pos.CENTER_LEFT);

        ImageView currentPic = new ImageView();
        currentPic.setFitWidth(100); currentPic.setFitHeight(100);
        Circle clip = new Circle(50, 50, 50);
        currentPic.setClip(clip);

        // Load Image
        try {
            if (admin.getProfilePicPath() != null && !admin.getProfilePicPath().isEmpty())
                currentPic.setImage(new Image("file:///" + admin.getProfilePicPath().replace("\\", "/")));
            else
                currentPic.setImage(new Image(getClass().getResourceAsStream("/images/student_placeholder.png")));
        } catch (Exception e) {
            try { currentPic.setImage(new Image(getClass().getResourceAsStream("/images/student_placeholder.png"))); } catch (Exception ignored) {}
        }

        Button uploadBtn = new Button("Change Picture");
        final StringBuilder newImagePath = new StringBuilder(admin.getProfilePicPath() == null ? "" : admin.getProfilePicPath());

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

        // --- Fields ---
        Label lblU = new Label("Username:");
        TextField userField = new TextField(admin.getUsername());
        userField.setMaxWidth(400);

        Label lblF = new Label("Full Name:");
        TextField nameField = new TextField(admin.getFullName() != null ? admin.getFullName() : "");
        nameField.setMaxWidth(400);

        Label lblOldP = new Label("Old Password (Required if changing password):");
        PasswordField oldPassField = new PasswordField();
        oldPassField.setMaxWidth(400);
        oldPassField.setPromptText("Enter Old Password");

        Label lblNewP = new Label("New Password (Leave empty to keep current):");
        PasswordField newPassField = new PasswordField();
        newPassField.setMaxWidth(400);
        newPassField.setPromptText("Enter New Password");

//        Label lblP = new Label("New Password:");
//        PasswordField passField = new PasswordField();
//        //passField.setText(admin.getPassword());
//        passField.setMaxWidth(400);

        Button saveBtn = new Button("Save Changes");
        saveBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        Label statusLabel = new Label();

        // --- SAVE LOGIC ---
        saveBtn.setOnAction(e -> {
            String newPass = newPassField.getText();
            String oldPass = oldPassField.getText();
            AdminDAO dao = new AdminDAO();

            // LOGIC: Check if new pass is NOT empty
            if (!newPass.isEmpty()) {
                // 1. Check if Old Pass provided
                if (oldPass.isEmpty()) {
                    new Alert(Alert.AlertType.ERROR, "Please enter your Old Password to confirm changes.").show();
                    return;
                }
                // 2. Verify Old Pass
                if (!dao.verifyPassword(admin.getAdminId(), oldPass)) {
                    new Alert(Alert.AlertType.ERROR, "Incorrect Old Password.").show();
                    return;
                }
                // 3. Optional: Validate New Pass strength
                if (!Model.Register.isValidPassword(newPass)) {
                    new Alert(Alert.AlertType.ERROR, "New Password is too weak. but we will continue for ease of use now").show();
                    //return;
                }
            }
            if(newPass.isEmpty() && !oldPass.isEmpty()){
                new Alert(Alert.AlertType.ERROR, "Enter New Password.").show(); return;
            }

            // If we get here, either NewPass is empty (so we ignore it) OR NewPass is valid and verified.
            boolean success = dao.updateProfile(
                    admin.getAdminId(),
                    userField.getText(),
                    nameField.getText(),
                    newPass, // If empty, DAO keeps old password
                    newImagePath.toString()
            );

            if (success) {
                // 1. Alert and Wait
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Profile Updated!");
                alert.show();

                // 2. Fetch Updated Admin Data
                Admin updatedAdmin = dao.getAdminById(admin.getAdminId());

                // 3. Reload Dashboard
                if (updatedAdmin != null) {
                    Stage currentStage = (Stage) saveBtn.getScene().getWindow();
                    new AdminDashboard().show(currentStage, updatedAdmin);
                }

                oldPassField.clear();
                newPassField.clear();
            } else {
                new Alert(Alert.AlertType.ERROR, "Update Failed.").show();
            }
        });

        content.getChildren().addAll(title, picSection, lblU, userField, lblF, nameField, lblOldP, oldPassField, lblNewP, newPassField, new Region(), saveBtn, statusLabel);
        return content;
    }

    private VBox createStudentParentRegisterView(BorderPane root) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Register New Student & Parent");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));

        // --- Student Section ---
        Label lblStudent = new Label("Student Details");
        lblStudent.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        lblStudent.setTextFill(Color.web("#2980b9"));

        TextField sFName = new TextField(); sFName.setPromptText("Student First Name");
        TextField sLName = new TextField(); sLName.setPromptText("Student Last Name");
        TextField sEmail = new TextField(); sEmail.setPromptText("Student Email");
        PasswordField sPass = new PasswordField(); sPass.setPromptText("Student Password");

        GridPane sGrid = new GridPane();
        sGrid.setHgap(10); sGrid.setVgap(10);
        sGrid.addRow(0, sFName, sLName);
        sGrid.addRow(1, sEmail, sPass);

        // --- Parent Section ---
        Label lblParent = new Label("Parent Details");
        lblParent.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        lblParent.setTextFill(Color.web("#27ae60"));

        TextField pFName = new TextField(); pFName.setPromptText("Parent First Name");
        TextField pLName = new TextField(); pLName.setPromptText("Parent Last Name");
        TextField pEmail = new TextField(); pEmail.setPromptText("Parent Email");
        PasswordField pPass = new PasswordField(); pPass.setPromptText("Parent Password");

        GridPane pGrid = new GridPane();
        pGrid.setHgap(10); pGrid.setVgap(10);
        pGrid.addRow(0, pFName, pLName);
        pGrid.addRow(1, pEmail, pPass);

        // --- Action ---
        Button registerBtn = new Button("Create Accounts");
        registerBtn.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");

        Label statusLabel = new Label();

        registerBtn.setOnAction(e -> {
            // 1. Validate Input (Using your existing Register model logic)
            if (!Register.isValidFirstName(sFName.getText()) || !Register.isValidFirstName(pFName.getText())) {
                new Alert(Alert.AlertType.ERROR, "Invalid First Name(s). Must start with Capital.").show();
                return;
            }
            if (!Register.isValidLastName(sLName.getText()) || !Register.isValidLastName(pLName.getText())) {
                new Alert(Alert.AlertType.ERROR, "Invalid Last Name(s).").show();
                return;
            }
            if (!Register.isValidEmail(sEmail.getText()) || !Register.isValidEmail(pEmail.getText())) {
                new Alert(Alert.AlertType.ERROR, "Invalid Email format.").show();
                return;
            }
            if (!Register.isValidPassword(sPass.getText()) || !Register.isValidPassword(pPass.getText())) {
                new Alert(Alert.AlertType.ERROR, "Weak Password. Needs 8+ chars, Upper, Lower, Number, Special.").show();
                return;
            }
            if (sEmail.getText().equalsIgnoreCase(pEmail.getText())) {
                new Alert(Alert.AlertType.ERROR, "Student and Parent cannot share the same email.").show();
                return;
            }

            // 2. Call DAO
            AdminDAO dao = new AdminDAO();
            boolean success = dao.registerStudentAndParent(
                    sFName.getText(), sLName.getText(), sEmail.getText(), sPass.getText(),
                    pFName.getText(), pLName.getText(), pEmail.getText(), pPass.getText()
            );

            if (success) {
                new Alert(Alert.AlertType.INFORMATION, "Accounts created successfully!").show();
                // Clear fields
                sFName.clear(); sLName.clear(); sEmail.clear(); sPass.clear();
                pFName.clear(); pLName.clear(); pEmail.clear(); pPass.clear();
            } else {
                new Alert(Alert.AlertType.ERROR, "Registration Failed. Emails might be duplicates.").show();
            }
        });

        content.getChildren().addAll(title, lblStudent, sGrid, new Separator(), lblParent, pGrid, new Separator(), registerBtn, statusLabel);
        return content;
    }

    // Helpers
    private Button createNavButton(String text, boolean isActive) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(10));
        btn.setFont(Font.font("Segoe UI", 14));
        btn.setCursor(javafx.scene.Cursor.HAND);

        if (isActive) {
            btn.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-border-color: transparent transparent transparent #3498db; -fx-border-width: 0 0 0 4;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #bdc3c7; -fx-border-width: 0;");
        }

        btn.setOnMouseEntered(e -> {
            if (!btn.getStyle().contains("#2c3e50")) btn.setStyle("-fx-background-color: #3e5871; -fx-text-fill: white;");
        });
        btn.setOnMouseExited(e -> {
            if (!btn.getStyle().contains("#2c3e50")) btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #bdc3c7;");
        });

        return btn;
    }

    private void setActive(Button active, Button... others) {
        active.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-border-color: transparent transparent transparent #3498db; -fx-border-width: 0 0 0 4;");
        for (Button b : others) {
            b.setStyle("-fx-background-color: transparent; -fx-text-fill: #bdc3c7; -fx-border-width: 0;");
        }
    }

    private ImageView loadImageView(String path, double w, double h) {
        ImageView iv = new ImageView();
        try { InputStream is = getClass().getResourceAsStream(path); if(is!=null) iv.setImage(new Image(is)); } catch(Exception e){}
        iv.setFitWidth(w); iv.setFitHeight(h);
        return iv;
    }

    // =========================================
    // VIEW F: Manage Issues (NEW)
    // =========================================
    private VBox createIssuesView(BorderPane root) {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label title = new Label("Reported Room Issues");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));

        // Table
        TableView<HallIssue> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<HallIssue, String> hallCol = new TableColumn<>("Hall");
        hallCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("hallName"));

        TableColumn<HallIssue, String> descCol = new TableColumn<>("Issue Description");
        descCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("issueDescription"));

        TableColumn<HallIssue, String> reporterCol = new TableColumn<>("Reporter");
        reporterCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getReporterType() + " (ID: " + cell.getValue().getReporterId() + ")"
        ));

        TableColumn<HallIssue, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("formattedDate"));

        TableColumn<HallIssue, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("status"));
        // Color the status text
        statusCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("Open")) {
                        setTextFill(Color.RED);
                        setStyle("-fx-font-weight: bold;");
                    } else {
                        setTextFill(Color.GREEN);
                        setStyle("-fx-font-weight: normal;");
                    }
                }
            }
        });

        // Action Column (Resolve Button)
        TableColumn<HallIssue, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Resolve");
            {
                btn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 11px;");
                btn.setOnAction(e -> {
                    HallIssue issue = getTableView().getItems().get(getIndex());
                    AdminDAO dao = new AdminDAO();
                    if (dao.resolveIssue(issue.getIssueId())) {
                        new Alert(Alert.AlertType.INFORMATION, "Issue marked as Resolved.").show();
                        root.setCenter(createIssuesView(root)); // Refresh
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to update status.").show();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HallIssue currentIssue = getTableView().getItems().get(getIndex());
                    // Only show button if status is 'Open'
                    if ("Open".equals(currentIssue.getStatus())) {
                        setGraphic(btn);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        table.getColumns().addAll(hallCol, descCol, reporterCol, dateCol, statusCol, actionCol);

        // Load Data
        AdminDAO dao = new AdminDAO();
        table.getItems().addAll(dao.getAllIssues());

        content.getChildren().addAll(title, table);
        return content;
    }

    private VBox createFinancesView(BorderPane root) {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label title = new Label("Student Financial Status");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));

        // Table
        TableView<Student> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // 1. Basic Info Columns
        TableColumn<Student, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("studentId"));

        TableColumn<Student, String> nameCol = new TableColumn<>("Full Name");
        nameCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getFirstName() + " " + cell.getValue().getLastName()
        ));

        // 2. Wallet Balance Column
        TableColumn<Student, String> walletCol = new TableColumn<>("Wallet Balance");
        walletCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                String.format("%.2f EGP", cell.getValue().getWallet())
        ));

        // 3. Debt/Tuition Column
        TableColumn<Student, String> debtCol = new TableColumn<>("Tuition Debt");
        debtCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                String.format("%.2f EGP", cell.getValue().getAmountToBePaid())
        ));

        // 4. Status Column (Paid vs Unpaid)
        TableColumn<Student, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Student s = getTableView().getItems().get(getIndex());
                    if (s.getAmountToBePaid() <= 0) {
                        setText("PAID");
                        setTextFill(Color.GREEN);
                        setStyle("-fx-font-weight: bold;");
                    } else {
                        setText("UNPAID");
                        setTextFill(Color.RED);
                        setStyle("-fx-font-weight: bold;");
                    }
                }
            }
        });

        // 5. Action Column: Add Funds
        TableColumn<Student, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Add Funds");
            {
                btn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-cursor: hand;");
                btn.setOnAction(e -> {
                    Student s = getTableView().getItems().get(getIndex());
                    handleAddFunds(s, root);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
        // ---------------------------------------------------------
        // 6. NEW DELETE COLUMN
        // ---------------------------------------------------------
        TableColumn<Student, Void> delCol = new TableColumn<>("Delete");
        delCol.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("X");
            {
                btn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
                btn.setOnAction(e -> {
                    Student s = getTableView().getItems().get(getIndex());

                    // Confirm Deletion
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                            "Are you sure you want to delete student: " + s.getFirstName() + "?",
                            ButtonType.YES, ButtonType.NO);
                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            AdminDAO dao = new AdminDAO();
                            if (dao.deleteStudent(s.getStudentId())) {
                                // Refresh the view on success
                                root.setCenter(createFinancesView(root));
                            } else {
                                new Alert(Alert.AlertType.ERROR, "Failed to delete student.").show();
                            }
                        }
                    });
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        table.getColumns().addAll(idCol, nameCol, walletCol, debtCol, statusCol, actionCol,delCol);

        // Load Data
        AdminDAO dao = new AdminDAO();
        table.getItems().addAll(dao.getAllStudents());

        content.getChildren().addAll(title, table);
        return content;
    }

    // Helper: Logic to Add Funds via Dialog
    private void handleAddFunds(Student student, BorderPane root) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Funds");
        dialog.setHeaderText("Add money to " + student.getFirstName() + "'s Wallet");
        dialog.setContentText("Enter Amount (EGP):");

        dialog.showAndWait().ifPresent(amountStr -> {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    new Alert(Alert.AlertType.ERROR, "Amount must be positive.").show();
                    return;
                }

                AdminDAO dao = new AdminDAO();
                if (dao.addStudentFunds(student.getStudentId(), amount)) {
                    new Alert(Alert.AlertType.INFORMATION, "Funds added successfully!").show();
                    // Refresh View
                    root.setCenter(createFinancesView(root));
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to add funds.").show();
                }

            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "Invalid number format.").show();
            }
        });
    }


}