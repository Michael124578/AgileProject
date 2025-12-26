package Model;

public class EnrolledCourse {
    private String courseCode;
    private String courseName;
    private int credits;
    private String semester;
    private double grade;
    private String day;
    private String startTime;
    private String endTime;
    private String room;
    private int hallId;

    public EnrolledCourse(String code, String name, int cred, String sem, double gr, String day, String start, String end, String room, int hallId) {
        this.courseCode = code;
        this.courseName = name;
        this.credits = cred;
        this.semester = sem;
        this.grade = gr;
        this.day = day;
        this.startTime = start;
        this.endTime = end;
        this.room = room;
        this.hallId = hallId;
    }

    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public int getCredits() { return credits; }
    public String getSemester() { return semester; }
    public double getGrade() { return grade; }
    public String getDay() { return day; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getRoom() { return room; }
    public int getHallId() { return hallId; }

}