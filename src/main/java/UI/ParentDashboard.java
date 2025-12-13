package UI;

import DAO.ParentDAO;
import DAO.StudentDAO;
import Model.EnrolledCourse;
import Model.Message;
import Model.Parent;
import Model.Teacher;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.List;

public class ParentDashboard {

    public void show(Stage stage, Parent parent) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f4f6f9;");

        // Top Bar
        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(10, 20, 10, 20));
        topBar.setStyle("-fx-background-color: #8e44ad;"); // Purple for Parents
        Label title = new Label("Parent Portal - Welcome " + parent.getFirstName());
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        topBar.getChildren().add(title);
        root.setTop(topBar);

        // Sidebar
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #2c3e50;");
        sidebar.setPrefWidth(200);

        Button progressBtn = navButton("Child's Progress");
        Button messagesBtn = navButton("Message Teachers");
        Button logoutBtn = navButton("Logout");

        sidebar.getChildren().addAll(progressBtn, messagesBtn, new Separator(), logoutBtn);
        root.setLeft(sidebar);

        // Default View
        root.setCenter(createProgressView(parent));

        // Actions
        progressBtn.setOnAction(e -> root.setCenter(createProgressView(parent)));
        messagesBtn.setOnAction(e -> root.setCenter(createMessagingView(parent)));
        logoutBtn.setOnAction(e -> {
            stage.close();
            new LoginScreen().show(new Stage());
        });

        Scene scene = new Scene(root, 1000, 600);
        stage.setTitle("Parent Dashboard");
        stage.setScene(scene);
        stage.show();
    }

    private Button navButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-alignment: CENTER-LEFT;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #3e5871; -fx-text-fill: white; -fx-font-size: 14px; -fx-alignment: CENTER-LEFT;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-alignment: CENTER-LEFT;"));
        return btn;
    }

    // View 1: Child's Progress (Reusing StudentDAO)
    private VBox createProgressView(Parent parent) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));

        Label header = new Label("Academic Progress");
        header.setFont(Font.font(24));

        TableView<EnrolledCourse> table = new TableView<>();
        TableColumn<EnrolledCourse, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("courseCode"));

        TableColumn<EnrolledCourse, String> nameCol = new TableColumn<>("Course");
        nameCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("courseName"));

        TableColumn<EnrolledCourse, String> gradeCol = new TableColumn<>("Grade");
        gradeCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("gradeString"));

        table.getColumns().addAll(codeCol, nameCol, gradeCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Fetch data using child's ID
        StudentDAO dao = new StudentDAO();
        table.getItems().addAll(dao.getEnrolledCourses(parent.getStudentId()));

        content.getChildren().addAll(header, table);
        return content;
    }

    // View 2: Messaging
    private HBox createMessagingView(Parent parent) {
        HBox content = new HBox(10);
        content.setPadding(new Insets(20));

        // Left: Teacher List
        ListView<Teacher> teacherList = new ListView<>();
        ParentDAO dao = new ParentDAO();
        teacherList.getItems().addAll(dao.getChildsTeachers(parent.getStudentId()));
        teacherList.setPrefWidth(250);

        // Right: Chat Area
        VBox chatBox = new VBox(10);
        chatBox.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #ccc;");
        HBox.setHgrow(chatBox, Priority.ALWAYS);

        TextArea conversation = new TextArea();
        conversation.setEditable(false);
        conversation.setWrapText(true);
        VBox.setVgrow(conversation, Priority.ALWAYS);

        HBox inputBox = new HBox(10);
        TextField messageField = new TextField();
        HBox.setHgrow(messageField, Priority.ALWAYS);
        Button sendBtn = new Button("Send");
        sendBtn.setDisable(true); // Disable until teacher selected

        inputBox.getChildren().addAll(messageField, sendBtn);
        chatBox.getChildren().addAll(new Label("Conversation"), conversation, inputBox);

        // Logic
        teacherList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, teacher) -> {
            if (teacher != null) {
                sendBtn.setDisable(false);
                refreshChat(conversation, parent.getParentId(), teacher.getTeacherId());
            }
        });

        sendBtn.setOnAction(e -> {
            Teacher selected = teacherList.getSelectionModel().getSelectedItem();
            if (selected != null && !messageField.getText().trim().isEmpty()) {
                dao.sendMessage(parent.getParentId(), selected.getTeacherId(), messageField.getText(), "Parent");
                messageField.clear();
                refreshChat(conversation, parent.getParentId(), selected.getTeacherId());
            }
        });

        content.getChildren().addAll(teacherList, chatBox);
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
        area.setScrollTop(Double.MAX_VALUE); // Scroll to bottom
    }
}