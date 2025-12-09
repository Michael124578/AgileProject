package DAO;

import Model.Admin;
import Model.Course;
import Model.Hall;
import Model.Teacher;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO {

    // 1. LOGIN
    public Admin login(String username, String password) {
        String sql = "SELECT AdminID, Username,FullName, PasswordHash, ProfilePicPath FROM Admins WHERE Username = ? AND Password = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Admin(rs.getInt("AdminID"),
                        rs.getString("Username"),
                        rs.getString("FullName"),
                        rs.getString("PasswordHash"),
                        rs.getString("ProfilePicPath"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // =========================
    // TEACHER MANAGEMENT
    // =========================

    public List<Teacher> getAllTeachers() {
        List<Teacher> list = new ArrayList<>();
        String sql = "SELECT * FROM Teachers";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Teacher(
                        rs.getInt("TeacherID"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("Email"),
                        rs.getString("Password"),
                        rs.getString("Department"),
                        rs.getString("ProfilePicPath")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addTeacher(String fName, String lName, String email, String dept, String password) {
        String sql = "INSERT INTO Teachers (FirstName, LastName, Email, Department, Password, HireDate) VALUES (?, ?, ?, ?, ?, GETDATE())";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fName);
            pstmt.setString(2, lName);
            pstmt.setString(3, email);
            pstmt.setString(4, dept);
            pstmt.setString(5, password);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Add Teacher Error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteTeacher(int teacherId) {
        String sql = "DELETE FROM Teachers WHERE TeacherID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teacherId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // =========================
    // HALL (ROOM) MANAGEMENT  <-- NEW SECTION
    // =========================

    // Get All Halls
    public List<Hall> getAllHalls() {
        List<Hall> list = new ArrayList<>();
        String sql = "SELECT * FROM Halls";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Hall(
                        rs.getInt("HallID"),
                        rs.getString("HallName"),
                        rs.getInt("Capacity"),
                        rs.getBoolean("IsActive"),
                        rs.getString("HallType")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Add Hall
    public boolean addHall(String name, int capacity, String type) {
        String sql = "INSERT INTO Halls (HallName, Capacity, HallType, IsActive) VALUES (?, ?, ?, 1)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, capacity);
            pstmt.setString(3, type);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Add Hall Error: " + e.getMessage());
            return false;
        }
    }

    // Delete Hall
    public boolean deleteHall(int hallId) {
        // Note: Check constraints (Courses linked to Hall) before deleting in a real app
        String sql = "DELETE FROM Halls WHERE HallID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, hallId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // =========================
    // COURSE MANAGEMENT
    // =========================

    // Get All Courses (Updated to JOIN with Halls)
    public List<Course> getAllCourses() {
        List<Course> list = new ArrayList<>();
        // Fetch HallName and HallID via JOIN
        String sql = "SELECT C.CourseID, C.CourseCode, C.CourseName, C.Credits, " +
                "C.DayOfWeek, C.StartTime, C.EndTime, H.HallName, H.HallID " +
                "FROM Courses C " +
                "LEFT JOIN Halls H ON C.HallID = H.HallID";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                // Determine Room Name (Use "Unassigned" if HallID is null)
                String roomName = rs.getString("HallName");
                if (roomName == null) roomName = "Unassigned";

                list.add(new Course(
                        rs.getInt("CourseID"),
                        rs.getString("CourseCode"),
                        rs.getString("CourseName"),
                        rs.getInt("Credits"),
                        rs.getString("DayOfWeek"),
                        rs.getString("StartTime"),
                        rs.getString("EndTime"),
                        roomName,           // Pass Hall Name as "RoomNumber" for display
                        rs.getInt("HallID") // Pass the ID for logic
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Add Course (Updated to take hallId instead of String room)
    public boolean addCourse(String code, String name, int credits, String day, String start, String end, int hallId) {
        String sql = "INSERT INTO Courses (CourseCode, CourseName, Credits, DayOfWeek, StartTime, EndTime, HallID) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, code);
            pstmt.setString(2, name);
            pstmt.setInt(3, credits);
            pstmt.setString(4, day);
            pstmt.setString(5, start);
            pstmt.setString(6, end);
            pstmt.setInt(7, hallId); // Use the Foreign Key
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Add Course Error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteCourse(int courseId) {
        String sql = "DELETE FROM Courses WHERE CourseID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // =========================
    // ASSIGNMENTS & PROFILE
    // =========================
    public boolean assignTeacherToCourse(int teacherId, int courseId, String semester, int year) {
        String sql = "INSERT INTO TeacherAssignments (TeacherID, CourseID, Semester, Year) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teacherId);
            pstmt.setInt(2, courseId);
            pstmt.setString(3, semester);
            pstmt.setInt(4, year);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Assign Error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateProfile(int adminId, String username, String fullName, String password, String picPath) {
        String sql = "UPDATE Admins SET Username = ?, FullName = ?, PasswordHash = ?, ProfilePicPath = ? WHERE AdminID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, fullName);
            pstmt.setString(3, password);
            pstmt.setString(4, picPath);
            pstmt.setInt(5, adminId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}