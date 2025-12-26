package DAO;

import Model.Parent;
import Model.Teacher;
import Model.Message;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParentDAO {

    public Parent login(String email, String password) {
        String sql = "SELECT p.ParentID, p.FirstName, p.LastName, p.Email, p.StudentID, " +
                "s.FirstName AS ChildFirst, s.LastName AS ChildLast " +
                "FROM Parents p " +
                "JOIN Students s ON p.StudentID = s.StudentID " +
                "WHERE p.Email = ? AND p.Password = CONVERT(NVARCHAR(64), HASHBYTES('SHA2_256', ?), 2)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String childName = rs.getString("ChildFirst") + " " + rs.getString("ChildLast");
                return new Parent(
                        rs.getInt("ParentID"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("Email"),
                        rs.getInt("StudentID"),
                        childName // <--- Pass Child Name
                );
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Teacher> getChildsTeachers(int studentId) {
        List<Teacher> list = new ArrayList<>();
        String sql = "SELECT DISTINCT t.TeacherID, t.FirstName, t.LastName, t.Email, t.Department " +
                "FROM Teachers t " +
                "JOIN TeacherAssignments ta ON t.TeacherID = ta.TeacherID " +
                "JOIN Enrollments e ON ta.CourseID = e.CourseID " +
                "WHERE e.StudentID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Teacher(
                        rs.getInt("TeacherID"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("Email"),
                        "", // Password not needed here
                        rs.getString("Department"),
                        ""  // Pic not needed
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean sendMessage(int parentId, int teacherId, String text, String senderType) {
        String sql = "INSERT INTO Messages (ParentID, TeacherID, SenderType, MessageText, SentDate) VALUES (?, ?, ?, ?, GETDATE())";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, parentId);
            pstmt.setInt(2, teacherId);
            pstmt.setString(3, senderType);
            pstmt.setString(4, text);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<Message> getConversation(int parentId, int teacherId) {
        List<Message> list = new ArrayList<>();
        String sql = "SELECT SenderType, MessageText, SentDate FROM Messages " +
                "WHERE ParentID = ? AND TeacherID = ? ORDER BY SentDate ASC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, parentId);
            pstmt.setInt(2, teacherId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Message(
                        rs.getString("SenderType"),
                        rs.getString("MessageText"),
                        rs.getTimestamp("SentDate")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Parent> getParentsContactingTeacher(int teacherId) {
        List<Parent> list = new ArrayList<>();
        String sql = "SELECT DISTINCT p.ParentID, p.FirstName, p.LastName, p.Email, p.StudentID, " +
                "s.FirstName AS ChildFirst, s.LastName AS ChildLast " +
                "FROM Parents p " +
                "JOIN Students s ON p.StudentID = s.StudentID " +
                "JOIN Enrollments e ON s.StudentID = e.StudentID " +
                "JOIN TeacherAssignments ta ON e.CourseID = ta.CourseID " +
                "WHERE ta.TeacherID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teacherId);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                String childName = rs.getString("ChildFirst") + " " + rs.getString("ChildLast");
                list.add(new Parent(
                        rs.getInt("ParentID"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("Email"),
                        rs.getInt("StudentID"),
                        childName // <--- Pass Child Name
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
