package DAO;

import Model.Admin;
import Model.Course;
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
        String sql = "SELECT AdminID, Username, ProfilePicPath, FullName, PasswordHash FROM Admins WHERE Username = ? AND PasswordHash = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Admin(rs.getInt("AdminID"),
                                rs.getString("Username"),
                                rs.getString("ProfilePicPath"),
                                rs.getString("FullName"),
                                rs.getString("PasswordHash"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // =========================
    // TEACHER MANAGEMENT
    // =========================

    // Get All Teachers
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
                        rs.getString("Department"),
                        rs.getString("Password"), // Assuming password column exists
                        rs.getString("ProfilePicPath")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Add Teacher
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

    // Delete Teacher
    public boolean deleteTeacher(int teacherId) {
        // Note: This might fail if teacher has assignments.
        // You should delete assignments first or use CASCADE in SQL.
        String sql = "DELETE FROM Teachers WHERE TeacherID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teacherId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // =========================
    // COURSE MANAGEMENT
    // =========================

    // Get All Courses
    public List<Course> getAllCourses() {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM Courses";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                // Using Constructor 2 from Course.java
                list.add(new Course(
                        rs.getInt("CourseID"),
                        rs.getString("CourseCode"),
                        rs.getString("CourseName"),
                        rs.getInt("Credits"),
                        rs.getString("DayOfWeek"),
                        rs.getString("StartTime"),
                        rs.getString("EndTime"),
                        rs.getString("RoomNumber")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Add Course
    public boolean addCourse(String code, String name, int credits, String day, String start, String end, String room) {
        String sql = "INSERT INTO Courses (CourseCode, CourseName, Credits, DayOfWeek, StartTime, EndTime, RoomNumber) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, code);
            pstmt.setString(2, name);
            pstmt.setInt(3, credits);
            pstmt.setString(4, day);
            pstmt.setString(5, start);
            pstmt.setString(6, end);
            pstmt.setString(7, room);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Add Course Error: " + e.getMessage());
            return false;
        }
    }

    // Delete Course
    public boolean deleteCourse(int courseId) {
        String sql = "DELETE FROM Courses WHERE CourseID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // =========================
    // ASSIGNMENTS
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