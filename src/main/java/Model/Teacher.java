package Model;

public class Teacher {
    private int teacherId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String department;
    private String profilePicPath;

    public Teacher(int teacherId, String firstName, String lastName, String email,String password, String department, String profilePicPath) {
        this.teacherId = teacherId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password=password;
        this.department = department;
        this.profilePicPath = profilePicPath;
    }

    public int getTeacherId() { return teacherId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getProfilePicPath() {return profilePicPath;}
    public String getDepartment() { return department; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    // ... add other getters if needed

    @Override
    public String toString() { return "Prof. " + lastName + " (" + department + ")"; }
}
