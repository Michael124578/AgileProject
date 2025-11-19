package Model;

public class Admin {
    private int adminId;
    private String username;

    public Admin(int adminId, String username) {
        this.adminId = adminId;
        this.username = username;
    }

    public String getUsername() { return username; }
}