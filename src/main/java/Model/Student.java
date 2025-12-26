package Model;

public class Student {
    private int studentId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String profilePicPath;
    private double gpa;
    private int creditHours;
    private int weeks;
    private double Wallet=0.0;
    private double AmountToBePaid;
    private int creditsToBePaid;

    public Student(int studentId, String firstName, String lastName, String email, String profilePicPath, double gpa, int creditHours, int weeks, String password, double Wallet, double AmountToBePaid, int creditsToBePaid) {
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.profilePicPath = profilePicPath;
        this.gpa = gpa;
        this.creditHours = creditHours;
        this.weeks = weeks;
        this.Wallet = Wallet;
        this.AmountToBePaid = AmountToBePaid;
        this.creditsToBePaid = creditsToBePaid;
    }

    public int getStudentId() { return studentId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getProfilePicPath() {return profilePicPath;}
    public double getGpa() { return gpa; }
    public int getCreditHours() { return creditHours; }
    public int getWeeks() { return weeks; }
    public String getEmail() {return email;}
    public String getPassword() { return password; }
    public double getWallet() { return Wallet; }
    public double getAmountToBePaid() { return AmountToBePaid; }
    public int getCreditsToBePaid() { return creditsToBePaid; }

    @Override
    public String toString() {
        return "Welcome, " + firstName + " " + lastName + "!";
    }
}
