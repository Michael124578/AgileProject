package DAO;

import Model.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO {

    // 1. LOGIN
    public Admin login(String username, String password) {
        // Note: Admins login with Username, not Email (based on DB design)
        String sql = "SELECT AdminID, Username, ProfilePicPath FROM Admins WHERE Username = ? AND PasswordHash = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Admin(rs.getInt("AdminID"), rs.getString("Username"),rs.getString("ProfilePicPath"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 2. ADD NEW TEACHER
    public boolean addTeacher(String fName, String lName, String email, String department, String password) {
        // Validation
        if (!Register.isValidFirstName(fName) || !Register.isValidLastName(lName)) {
            System.out.println("Invalid Name format.");
            return false;
        }
        if (!Register.isValidEmail(email)) {
            System.out.println("Invalid Email.");
            return false;
        }

        String sql = "INSERT INTO Teachers (FirstName, LastName, Email, Department, HireDate, Password) VALUES (?, ?, ?, ?, GETDATE(), ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fName);
            pstmt.setString(2, lName);
            pstmt.setString(3, email);
            pstmt.setString(4, department);
            pstmt.setString(5, password);

            pstmt.executeUpdate();
            System.out.println("Teacher added successfully.");
            return true;

        } catch (SQLException e) {
            System.out.println("Error adding teacher: " + e.getMessage());
            return false;
        }
    }

    // 3. ADD NEW COURSE
    public boolean addCourse(String courseCode, String courseName, int credits) {
        String sql = "INSERT INTO Courses (CourseCode, CourseName, Credits) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, courseCode);
            pstmt.setString(2, courseName);
            pstmt.setInt(3, credits);

            pstmt.executeUpdate();
            System.out.println("Course " + courseCode + " added.");
            return true;

        } catch (SQLException e) {
            System.out.println("Error adding course: " + e.getMessage());
            return false;
        }
    }

    // 4. ASSIGN TEACHER TO COURSE
    public boolean assignTeacherToCourse(int teacherId, String courseCode, String semester, int year) {
        // We need to find the CourseID based on the code first
        int courseId = getCourseId(courseCode);
        if (courseId == -1) {
            System.out.println("Course not found.");
            return false;
        }

        String sql = "INSERT INTO TeacherAssignments (TeacherID, CourseID, Semester, Year) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, teacherId);
            pstmt.setInt(2, courseId);
            pstmt.setString(3, semester);
            pstmt.setInt(4, year);

            pstmt.executeUpdate();
            System.out.println("Assignment successful.");
            return true;

        } catch (SQLException e) {
            System.out.println("Error assigning teacher: " + e.getMessage());
            return false;
        }
    }

    // Helper for AdminDAO
    private int getCourseId(String courseCode) {
        String sql = "SELECT CourseID FROM Courses WHERE CourseCode = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, courseCode);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("CourseID");
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    // 5. update profile picture
    public boolean updateProfilePic(int studentId, String imagePath) {
        String sql = "UPDATE Students SET ProfilePicPath = ? WHERE StudentID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, imagePath);
            pstmt.setInt(2, studentId);

            int rows = pstmt.executeUpdate();
            if(rows > 0) {
                System.out.println("Profile picture updated!");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error updating pic: " + e.getMessage());
        }
        return false;
    }
}