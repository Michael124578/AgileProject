package Model;

public class Teacher {
    private int teacherId;
    private String firstName;
    private String lastName;
    private String email;
    private String department;

    public Teacher(int teacherId, String firstName, String lastName, String email, String department) {
        this.teacherId = teacherId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.department = department;
    }

    public int getTeacherId() { return teacherId; }
    public String getFirstName() { return firstName; }
    // ... add other getters if needed

    @Override
    public String toString() { return "Prof. " + lastName + " (" + department + ")"; }
}
