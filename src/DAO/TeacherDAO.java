package DAO;
import Model.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TeacherDAO {

    // 1. LOGIN
    public Teacher login(String email, String password) {
        String sql = "SELECT TeacherID, FirstName, LastName, Email, Department FROM Teachers WHERE Email = ? AND Password = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Teacher(
                            rs.getInt("TeacherID"),
                            rs.getString("FirstName"),
                            rs.getString("LastName"),
                            rs.getString("Email"),
                            rs.getString("Department")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 2. VIEW SCHEDULE (Courses taught by this teacher)
    public void printTeacherSchedule(int teacherId) {
        String sql = "SELECT c.CourseCode, c.CourseName, ta.Semester, ta.Year " +
                "FROM TeacherAssignments ta " +
                "JOIN Courses c ON ta.CourseID = c.CourseID " +
                "WHERE ta.TeacherID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, teacherId);

            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("--- Your Teaching Schedule ---");
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    System.out.println(rs.getString("CourseCode") + ": " +
                            rs.getString("CourseName") +
                            " [" + rs.getString("Semester") + " " + rs.getInt("Year") + "]");
                }
                if (!found) System.out.println("No courses assigned yet.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
