package Model;

public class Admin {
    private int adminId;
    private String username;
    private String fullName;
    private String password;
    private String profilePicPath;

    public Admin(int adminId, String username, String fullName, String password, String profilePicPath) {
        this.adminId = adminId;
        this.username = username;
        this.fullName = fullName;
        this.password = password;
        this.profilePicPath = profilePicPath;
    }

    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getPassword() { return password; }
    public String getProfilePicPath() {return profilePicPath;}
    public int getAdminId() { return adminId; }
}