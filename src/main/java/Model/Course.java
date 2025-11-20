package Model;

public class Course {
    private int courseId;
    private String courseCode;
    private String courseName;
    private int credits;

    public Course(int courseId, String courseCode, String courseName, int credits) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
    }

    // Getters are required for the TableView
    public int getCourseId() { return courseId; }
    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public int getCredits() { return credits; }
}