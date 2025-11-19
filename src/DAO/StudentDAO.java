package DAO;


import Model.*;
//import DAO.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentDAO {

    // Returns a Student object if login succeeds, null if it fails
    public Student login(String email, String password) {
        String sql = "SELECT StudentID, FirstName, LastName, Email FROM Students WHERE Email = ? AND Password = ?";

        // Try-with-resources ensures the Connection closes automatically
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set the parameters (the ? marks in the SQL)
            pstmt.setString(1, email);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Login Success! Map the database row to a Java Object
                    return new Student(
                            rs.getInt("StudentID"),
                            rs.getString("FirstName"),
                            rs.getString("LastName"),
                            rs.getString("Email")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Login Error: " + e.getMessage());
            e.printStackTrace();
        }

        return null; // Login failed
    }
}