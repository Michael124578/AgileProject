package Model;

public class Admin {
    private int adminId;
    private String username;
    private String profilePicPath;

    public Admin(int adminId, String username, String  profilePicPath) {
        this.adminId = adminId;
        this.username = username;
        this.profilePicPath = profilePicPath;
    }

    public String getUsername() { return username; }
    public String getProfilePicPath() {return profilePicPath;}
}