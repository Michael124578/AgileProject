package DAO;
import Model.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TeacherDAO {

    // 1. LOGIN
    public Teacher login(String email, String password) {
        String sql = "SELECT TeacherID, FirstName, LastName, Email, Department, Password, ProfilePicPath  FROM Teachers WHERE Email = ? AND Password = ?";

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
                            rs.getString("Department"),
                            rs.getString("Password"),
                            rs.getString("ProfilePicPath")
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

    // 3. update profile picture
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

    //3. update profile info
    public boolean updateProfile(int teacherId, String fName, String lName, String email, String password, String dept, String picPath) {
        String sql = "UPDATE Teachers SET FirstName=?, LastName=?, Email=?, Password=?, Department=?, ProfilePicPath=? WHERE TeacherID=?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fName);
            pstmt.setString(2, lName);
            pstmt.setString(3, email);
            pstmt.setString(4, password);
            pstmt.setString(5, dept);
            pstmt.setString(6, picPath);
            pstmt.setInt(7, teacherId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // 2. Get Courses Taught by this Teacher
    public List<Course> getTeacherCourses(int teacherId) {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.* FROM Courses c JOIN TeacherAssignments ta ON c.CourseID = ta.CourseID WHERE ta.TeacherID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teacherId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Course(
                        rs.getInt("CourseID"),
                        rs.getString("CourseCode"),
                        rs.getString("CourseName"),
                        rs.getInt("Credits")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // 3. Get Students Enrolled in a specific Course
    public List<StudentGrade> getStudentsInCourse(int courseId) {
        List<StudentGrade> list = new ArrayList<>();
        String sql = "SELECT s.StudentID, s.FirstName, s.LastName, e.Grade " +
                "FROM Enrollments e " +
                "JOIN Students s ON e.StudentID = s.StudentID " +
                "WHERE e.CourseID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String fullName = rs.getString("FirstName") + " " + rs.getString("LastName");
                list.add(new StudentGrade(
                        rs.getInt("StudentID"),
                        fullName,
                        rs.getDouble("Grade") // Returns 0.0 if null
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // 4. Update Grade (THE MAGIC BUTTON)
    public boolean updateGrade(int studentId, int courseId, double newGrade) {
        // This update will trigger your SQL 'trg_AutoCalculateGPA' automatically!
        String sql = "UPDATE Enrollments SET Grade = ? WHERE StudentID = ? AND CourseID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newGrade);
            pstmt.setInt(2, studentId);
            pstmt.setInt(3, courseId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

}
