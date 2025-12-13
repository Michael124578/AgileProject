package Model;

public class Parent {
    private int parentId;
    private String firstName;
    private String lastName;
    private String email;
    private int studentId; // The child's ID
    private String studentName;

    public Parent(int parentId, String firstName, String lastName, String email, int studentId, String studentName) {
        this.parentId = parentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.studentId = studentId;
        this.studentName = studentName;
    }

    public int getParentId() { return parentId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public int getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }

    @Override
    public String toString() { return firstName + " " + lastName; }
}