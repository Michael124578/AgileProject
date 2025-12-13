package DAO;

import Model.Material;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LMSDAO {

    public boolean uploadMaterial(int courseId, String fileName, String filePath) {
        String sql = "INSERT INTO Materials (CourseID, FileName, FilePath, UploadDate) VALUES (?, ?, ?, GETDATE())";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            pstmt.setString(2, fileName);
            pstmt.setString(3, filePath);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Material> getMaterialsByCourse(int courseId) {
        List<Material> list = new ArrayList<>();
        String sql = "SELECT * FROM Materials WHERE CourseID = ? ORDER BY UploadDate DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Material(
                        rs.getInt("MaterialID"),
                        rs.getInt("CourseID"),
                        rs.getString("FileName"),
                        rs.getString("FilePath"),
                        rs.getTimestamp("UploadDate")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean deleteMaterial(int materialId) {
        String sql = "DELETE FROM Materials WHERE MaterialID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, materialId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}