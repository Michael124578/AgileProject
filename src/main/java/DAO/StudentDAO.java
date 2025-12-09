package DAO;


import Model.*;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentDAO {

    // ==========================================
    // 1. LOGIN
    // ==========================================
    public Student login(String email, String password) {
        // Added GPA to the SELECT list
        String sql = "SELECT StudentID, FirstName, LastName, Email, ProfilePicPath, GPA, creditHours, weeks, Password, Wallet, AmountToBePaid , CreditsToBePaid FROM Students WHERE Email = ? AND Password = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                            rs.getInt("StudentID"),
                            rs.getString("FirstName"),
                            rs.getString("LastName"),
                            rs.getString("Email"),
                            rs.getString("ProfilePicPath"),
                            rs.getDouble("GPA"),
                            rs.getInt("creditHours"),
                            rs.getInt("weeks"),
                            rs.getString("Password"),
                            rs.getDouble("Wallet"),
                            rs.getDouble("AmountToBePaid"),
                            rs.getInt("CreditsToBePaid")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ==========================================
    // 2. SIGNUP (Register)
    // ==========================================
    public boolean signup(String firstName, String lastName, String email, String password) {

        // A. Validate Inputs using Register Class
        if (!Register.isValidFirstName(firstName)) {
            System.out.println("Error: Invalid First Name (Must start with Capital).");
            return false;
        }
        if (!Register.isValidLastName(lastName)) {
            System.out.println("Error: Invalid Last Name.");
            return false;
        }
        if (!Register.isValidEmail(email)) {
            System.out.println("Error: Invalid Email Format.");
            return false;
        }
        if (!Register.isValidPassword(password)) {
            System.out.println("Error: Password must be 8+ chars, contain uppercase, lowercase, number, and special char.");
            return false;
        }

        // B. Insert into Database
        String sql = "INSERT INTO Students (FirstName, LastName, Email, Password, EnrollmentDate) VALUES (?, ?, ?, ?, GETDATE())";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, password);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; // Returns true if insert was successful

        } catch (SQLException e) {
            // Handle duplicate email error specifically
            if (e.getErrorCode() == 2627) {
                System.err.println("Error: This email is already registered.");
            } else {
                System.err.println("Signup Error: " + e.getMessage());
            }
            return false;
        }
    }

    // ==========================================
    // 3. ADD COURSE (Enroll)
    // ==========================================
    public boolean enrollCourse(int studentId, String courseCode, String semester, int year) {
        // Step 1: Get CourseID from the Code (e.g., 'CS101' -> 1)
        int courseId = getCourseId(courseCode);
        if (courseId == -1) {
            System.out.println("Error: Course code '" + courseCode + "' not found.");
            return false;
        }

        // Step 2: Insert into Enrollments
        String sql = "INSERT INTO Enrollments (StudentID, CourseID, Semester, Year, EnrollmentDate) VALUES (?, ?, ?, ?, GETDATE())";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            pstmt.setString(3, semester);
            pstmt.setInt(4, year);

            pstmt.executeUpdate();
            System.out.println("Successfully enrolled in " + courseCode);
            return true;

        } catch (SQLException e) {
            System.err.println("Enrollment Error: " + e.getMessage());
            return false;
        }
    }

    // ==========================================
    // 4. DROP COURSE
    // ==========================================
    public boolean dropCourse(int studentId, String courseCode) {
        // We need to find the CourseID first based on the Code
        String findIdSql = "SELECT CourseID FROM Courses WHERE CourseCode = ?";
        int courseId = -1;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(findIdSql)) {
            pstmt.setString(1, courseCode);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) courseId = rs.getInt("CourseID");
        } catch (SQLException e) { e.printStackTrace(); return false; }

        if (courseId == -1) return false;

        // Now Delete the Enrollment
        String deleteSql = "DELETE FROM Enrollments WHERE StudentID = ? AND CourseID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==========================================
    // HELPER: Get CourseID from CourseCode
    // ==========================================
    private int getCourseId(String courseCode) {
        String sql = "SELECT CourseID FROM Courses WHERE CourseCode = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, courseCode);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("CourseID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Not found
    }

    // ==========================================
    // 5. update profile picture
    // ==========================================
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

    public boolean updateStudentProfile(int studentId, String fName, String lName, String email, String password, String imagePath) {
        // Update SQL to include Email and Password
        String sql = "UPDATE Students SET FirstName = ?, LastName = ?, Email = ?, Password = ?, ProfilePicPath = ? WHERE StudentID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fName);
            pstmt.setString(2, lName);
            pstmt.setString(3, email);
            pstmt.setString(4, password);
            pstmt.setString(5, imagePath);
            pstmt.setInt(6, studentId);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Update Error: " + e.getMessage());
            return false;
        }
    }

    public List<EnrolledCourse> getEnrolledCourses(int studentId) {
        List<EnrolledCourse> list = new ArrayList<>();

        // UPDATED SQL: Joins Halls table to get HallName and HallID
        String sql = "SELECT C.CourseCode, C.CourseName, C.Credits, E.Semester, E.Grade, " +
                "C.DayOfWeek, C.StartTime, C.EndTime, H.HallName, H.HallID " +
                "FROM Enrollments E " +
                "JOIN Courses C ON E.CourseID = C.CourseID " +
                "LEFT JOIN Halls H ON C.HallID = H.HallID " +
                "WHERE E.StudentID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String roomName = rs.getString("HallName");
                    if (roomName == null) roomName = "TBA";

                    list.add(new EnrolledCourse(
                            rs.getString("CourseCode"),
                            rs.getString("CourseName"),
                            rs.getInt("Credits"),
                            rs.getString("Semester"),
                            rs.getDouble("Grade"),
                            rs.getString("DayOfWeek"),
                            rs.getString("StartTime"),
                            rs.getString("EndTime"),
                            roomName,           // Pass HallName
                            rs.getInt("HallID") // Pass HallID
                    ));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Course> getAvailableCourses(int studentId) {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM Courses WHERE CourseID NOT IN (SELECT CourseID FROM Enrollments WHERE StudentID = ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Course(
                            rs.getInt("CourseID"),
                            rs.getString("CourseCode"),
                            rs.getString("CourseName"),
                            rs.getInt("Credits")
                    ));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean registerCourse(int studentId, int courseId, String semester, int year) {
        // Insert with NULL grade (since they just started)
        String sql = "INSERT INTO Enrollments (StudentID, CourseID, Semester, Year, Grade) VALUES (?, ?, ?, ?, NULL)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            pstmt.setString(3, semester);
            pstmt.setInt(4, year);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public double getTotalPaid(int studentId) {
        String sql = "SELECT SUM(Amount) AS TotalPaid FROM Payments WHERE StudentID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("TotalPaid"); // Returns 0.0 if null
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }

    // 2. Make a Payment
    public boolean payTuition(int studentId, double amount) {
        // STEP 1: Check if Wallet has enough money first!
        double currentWalletBalance = getWalletBalance(studentId); // Helper method below

        if (currentWalletBalance < amount) {
            System.out.println("Transaction Failed: Insufficient funds in Wallet.");
            return false;
        }

        // STEP 2: The Payment Logic
        // Wallet goes DOWN (You spent money)
        // AmountToBePaid goes DOWN (You paid off debt)
        String sql = "UPDATE Students SET Wallet = Wallet - ?, AmountToBePaid = AmountToBePaid - ?, CreditsToBePaid = 0 WHERE StudentID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, amount); // Decrease Wallet
            pstmt.setDouble(2, amount); // Decrease Debt
            pstmt.setInt(3, studentId);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper to check balance before paying
    public double getWalletBalance(int studentId) {
        String sql = "SELECT Wallet FROM Students WHERE StudentID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getDouble("Wallet");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }

    // Add money to the Wallet (e.g., via Bank Transfer / Credit Card)
    public boolean depositMoney(int studentId, double amount) {
        // Only Wallet increases. Debt stays the same until you choose to "Pay" it.
        String sql = "UPDATE Students SET Wallet = Wallet + ? WHERE StudentID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, amount);
            pstmt.setInt(2, studentId);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==========================================
    // NEW: Report Hall Issue
    // ==========================================
    public boolean reportIssue(int studentId, int hallId, String description) {
        String sql = "INSERT INTO HallIssues (HallID, ReporterID, ReporterType, IssueDescription, Status, ReportedDate) " +
                "VALUES (?, ?, 'Student', ?, 'Open', GETDATE())";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, hallId);
            pstmt.setInt(2, studentId);
            pstmt.setString(3, description);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}