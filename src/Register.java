public class Register {
    String username;
    String password;

    public Register() {
    }

    public Register(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Boolean isValidUsername(String username){
        String regex = "^[A-Za-z0-9_-]{4,20}$";
        return username.matches(regex);
    }

    public Boolean isValidPassword(String password) {
        int MIN_LENGTH = 8;
        String SPECIAL_CHARS = "@!#$%^&*()+\\-_";
        String regex = "(?=.*[A-Z])" + "(?=.*[a-z])" + "(?=.*[0-9])" + "(?=.*[" + SPECIAL_CHARS + "])" + "[A-Za-z0-9" + SPECIAL_CHARS + "]{" + MIN_LENGTH + ",}";
        return password.matches(regex);
    }
}


