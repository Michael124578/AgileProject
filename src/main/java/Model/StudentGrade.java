package Model;

public class StudentGrade {
    private int studentId;
    private String fullName;
    private double grade;

    public StudentGrade(int studentId, String fullName, double grade) {
        this.studentId = studentId;
        this.fullName = fullName;
        this.grade = grade;
    }

    public int getStudentId() { return studentId; }
    public String getFullName() { return fullName; }
    public double getGrade() { return grade; }

    // Helper for display
    public String getGradeString() {
        return grade == 0.0 ? "-" : String.valueOf(grade);
    }
}