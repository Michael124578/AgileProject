package DAO;
import Model.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TeacherDAO {

    public Teacher login(String email, String password) {
        String sql = "SELECT TeacherID, FirstName, LastName, Email, Department, Password, ProfilePicPath  FROM Teachers WHERE Email = ? AND Password = CONVERT(NVARCHAR(64), HASHBYTES('SHA2_256', ?), 2)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // FIX: Swapped Department and Password to match Model constructor order
                    return new Teacher(
                            rs.getInt("TeacherID"),
                            rs.getString("FirstName"),
                            rs.getString("LastName"),
                            rs.getString("Email"),
                            rs.getString("Password"),   // <--- Correct Order
                            rs.getString("Department"), // <--- Correct Order
                            rs.getString("ProfilePicPath")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Teacher getTeacherById(int teacherId) {
        String sql = "SELECT * FROM Teachers WHERE TeacherID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, teacherId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Teacher(
                        rs.getInt("TeacherID"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("Email"),
                        rs.getString("Password"),
                        rs.getString("Department"),
                        rs.getString("ProfilePicPath")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean verifyPassword(int teacherId, String oldPassword) {
        String sql = "SELECT 1 FROM Teachers WHERE TeacherID = ? AND Password = CONVERT(NVARCHAR(64), HASHBYTES('SHA2_256', ?), 2)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teacherId);
            pstmt.setString(2, oldPassword);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProfile(int teacherId, String fName, String lName, String email, String newPassword, String dept, String picPath) {
        String sql = "UPDATE Teachers SET FirstName=?, LastName=?, Email=?, " +
                "Password=CASE WHEN ? = '' THEN Password ELSE CONVERT(NVARCHAR(64), HASHBYTES('SHA2_256', ?), 2) END, " +
                "Department=?, ProfilePicPath=? WHERE TeacherID=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fName);
            pstmt.setString(2, lName);
            pstmt.setString(3, email);
            pstmt.setString(4, newPassword);
            pstmt.setString(5, newPassword);
            pstmt.setString(6, dept);
            pstmt.setString(7, picPath);
            pstmt.setInt(8, teacherId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }    // 2. Get Courses Taught by this Teacher

    public List<Course> getTeacherCourses(int teacherId) {
        List<Course> list = new ArrayList<>();

        // UPDATED SQL: Joins Halls table
        String sql = "SELECT C.CourseID, C.CourseCode, C.CourseName, C.Credits, " +
                "C.DayOfWeek, C.StartTime, C.EndTime, H.HallName, H.HallID " +
                "FROM Courses C " +
                "JOIN TeacherAssignments TA ON C.CourseID = TA.CourseID " +
                "LEFT JOIN Halls H ON C.HallID = H.HallID " +
                "WHERE TA.TeacherID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, teacherId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String roomName = rs.getString("HallName");
                if (roomName == null) roomName = "TBA";

                list.add(new Course(
                        rs.getInt("CourseID"),
                        rs.getString("CourseCode"),
                        rs.getString("CourseName"),
                        rs.getInt("Credits"),
                        rs.getString("DayOfWeek"),
                        rs.getString("StartTime"),
                        rs.getString("EndTime"),
                        roomName,           // Pass HallName
                        rs.getInt("HallID") // Pass HallID
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean reportIssue(int teacherId, int hallId, String description) {
        String sql = "INSERT INTO HallIssues (HallID, ReporterID, ReporterType, IssueDescription, Status, ReportedDate) " +
                "VALUES (?, ?, 'Teacher', ?, 'Open', GETDATE())";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, hallId);
            pstmt.setInt(2, teacherId);
            pstmt.setString(3, description);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

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

    public int[] getGradeDistribution(int courseId) {
        int[] stats = new int[5]; // 0=A, 1=B, 2=C, 3=D, 4=F

        // Efficient SQL to categorize grades in one go
        String sql = "SELECT " +
                "SUM(CASE WHEN Grade >= 90 THEN 1 ELSE 0 END) AS A, " +
                "SUM(CASE WHEN Grade >= 80 AND Grade < 90 THEN 1 ELSE 0 END) AS B, " +
                "SUM(CASE WHEN Grade >= 70 AND Grade < 80 THEN 1 ELSE 0 END) AS C, " +
                "SUM(CASE WHEN Grade >= 60 AND Grade < 70 THEN 1 ELSE 0 END) AS D, " +
                "SUM(CASE WHEN Grade < 60 THEN 1 ELSE 0 END) AS F " +
                "FROM Enrollments WHERE CourseID = ? AND Grade IS NOT NULL";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                stats[0] = rs.getInt("A");
                stats[1] = rs.getInt("B");
                stats[2] = rs.getInt("C");
                stats[3] = rs.getInt("D");
                stats[4] = rs.getInt("F");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

}
