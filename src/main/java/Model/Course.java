package Model;

public class Course {
    private int courseId;
    private String courseCode;
    private String courseName;
    private int credits;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private String roomNumber;

    public Course(int courseId, String courseCode, String courseName, int credits) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
    }
    public Course(int courseId, String courseCode, String courseName, int credits, String day, String start, String end, String room) {
        this(courseId, courseCode, courseName, credits); // Call the first constructor
        this.dayOfWeek = day;
        this.startTime = start;
        this.endTime = end;
        this.roomNumber = room;
    }

    // Getters are required for the TableView
    public int getCourseId() { return courseId; }
    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public int getCredits() { return credits; }
    public String getDayOfWeek() { return dayOfWeek; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getRoomNumber() { return roomNumber; }
}