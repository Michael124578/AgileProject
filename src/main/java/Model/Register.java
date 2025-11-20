package Model;

public class Register {
    String firstName;
    String lastName;
    String username;
    String password;
    String email;

    public Register() {
    }

    public Register(String lastName, String firstName, String username, String password, String email) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public static boolean isValidFirstName(String firstName){
        String regexFN = "(^[A-Z][a-z]+$)";
        return firstName.matches(regexFN);
    }

    public static boolean isValidLastName(String lastName){
        String regexLN = "(^[A-Z][a-z]+$)";
        return lastName.matches(regexLN);
    }

    public static boolean isValidUsername(String username){
        String regexUN = "^[A-Za-z0-9@_-]{4,20}$";
        return username.matches(regexUN);
    }

    public static boolean isValidEmail(String email){
        String regexEm = "^[A-Za-z0-9._%+-]+@(gmail|yahoo|hotmail)\\.com$";
        return email.matches(regexEm);
    }

    public static boolean isValidPassword(String password) {
        int MIN_LENGTH = 8;
        String SPECIAL_CHARS = "@!#$%^&*()+\\-_";
        String regexPass = "(?=.*[A-Z])" + "(?=.*[a-z])" + "(?=.*[0-9])" + "(?=.*[" + SPECIAL_CHARS + "])" + "[A-Za-z0-9" + SPECIAL_CHARS + "]{" + MIN_LENGTH + ",}";
        return password.matches(regexPass);
    }


}


