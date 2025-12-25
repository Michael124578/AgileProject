package DAO;


import Model.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
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
        String sql = "SELECT StudentID, FirstName, LastName, Email, ProfilePicPath, GPA, creditHours, weeks, Password, Wallet, AmountToBePaid , CreditsToBePaid " +
                     "FROM Students WHERE Email = ? AND Password = CONVERT(NVARCHAR(64), HASHBYTES('SHA2_256', ?), 2)";
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

    public Student getStudentById(int studentId) {
        String sql = "SELECT StudentID, FirstName, LastName, Email, ProfilePicPath, GPA, creditHours, weeks, Password, Wallet, AmountToBePaid , CreditsToBePaid " +
                "FROM Students WHERE StudentID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);

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

    public boolean verifyPassword(int studentId, String oldPassword) {
        String sql = "SELECT 1 FROM Students WHERE StudentID = ? AND Password = CONVERT(NVARCHAR(64), HASHBYTES('SHA2_256', ?), 2)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setString(2, oldPassword);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. UPDATED: Update Profile
    public boolean updateStudentProfile(int studentId, String fName, String lName, String email, String newPassword, String imagePath) {
        String sql = "UPDATE Students SET FirstName = ?, LastName = ?, Email = ?, " +
                "Password = CASE WHEN ? = '' THEN Password ELSE CONVERT(NVARCHAR(64), HASHBYTES('SHA2_256', ?), 2) END, " +
                "ProfilePicPath = ? WHERE StudentID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fName);
            pstmt.setString(2, lName);
            pstmt.setString(3, email);
            pstmt.setString(4, newPassword);
            pstmt.setString(5, newPassword);
            pstmt.setString(6, imagePath);
            pstmt.setInt(7, studentId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
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

    // 1. Get All Halls (For the dropdown)
    public List<Hall> getAllHalls() {
        List<Hall> list = new ArrayList<>();
        String sql = "SELECT * FROM Halls WHERE IsActive = 1";
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

    // 2. Book a Hall (Includes Conflict Check)
    public boolean bookHall(int studentId, int hallId, LocalDate date, String start, String end, String purpose) {
        // A. Validate Time Format
        if (!isValidTime(start) || !isValidTime(end)) {
            System.out.println("Invalid time format. Use HH:mm AM/PM (e.g., 09:30 AM)");
            return false;
        }

        // B. Check Availability
        if (!isHallFree(hallId, date, start, end)) {
            System.out.println("Hall is occupied at this time.");
            return false;
        }

        // C. Insert Booking
        String sql = "INSERT INTO HallBookings (HallID, StudentID, BookingDate, StartTime, EndTime, Purpose) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, hallId);
            pstmt.setInt(2, studentId);
            pstmt.setDate(3, java.sql.Date.valueOf(date));
            pstmt.setString(4, start);
            pstmt.setString(5, end);
            pstmt.setString(6, purpose);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper: Check if Hall is Free
    private boolean isHallFree(int hallId, LocalDate date, String newStartStr, String newEndStr) {
        LocalTime newStart = parseTime(newStartStr);
        LocalTime newEnd = parseTime(newEndStr);
        if(newStart == null || newEnd == null) return false;

        // 1. Check against COURSES (Weekly Schedule)
        // SQL Server's DATENAME returns 'Monday', 'Tuesday', etc.
        String dayOfWeek = date.getDayOfWeek().name(); // Returns "MONDAY", need to fix case to match DB if needed
        // Assuming DB stores "Monday", "Tuesday" (Title case)
        dayOfWeek = dayOfWeek.charAt(0) + dayOfWeek.substring(1).toLowerCase();

        String courseSql = "SELECT StartTime, EndTime FROM Courses WHERE HallID = ? AND DayOfWeek = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(courseSql)) {
            pstmt.setInt(1, hallId);
            pstmt.setString(2, dayOfWeek);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                if (isOverlapping(newStart, newEnd, rs.getString("StartTime"), rs.getString("EndTime"))) return false;
            }
        } catch (SQLException e) { e.printStackTrace(); return false; }

        // 2. Check against OTHER BOOKINGS (Specific Date)
        String bookingSql = "SELECT StartTime, EndTime FROM HallBookings WHERE HallID = ? AND BookingDate = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(bookingSql)) {
            pstmt.setInt(1, hallId);
            pstmt.setDate(2, java.sql.Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                if (isOverlapping(newStart, newEnd, rs.getString("StartTime"), rs.getString("EndTime"))) return false;
            }
        } catch (SQLException e) { e.printStackTrace(); return false; }

        return true;
    }

    // Helper: Time Overlap Logic
    private boolean isOverlapping(LocalTime start1, LocalTime end1, String start2Str, String end2Str) {
        LocalTime start2 = parseTime(start2Str);
        LocalTime end2 = parseTime(end2Str);
        if (start2 == null || end2 == null) return false; // Ignore bad data
        // Overlap condition: (Start1 < End2) AND (Start2 < End1)
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    // Helper: Parse Time String "09:00 AM" -> LocalTime
    private LocalTime parseTime(String timeStr) {
        try {
            // Try standard formats. "hh:mm a" is for 09:00 AM
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);
            return LocalTime.parse(timeStr.toUpperCase(), formatter); // Ensure AM/PM is upper
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private boolean isValidTime(String timeStr) {
        return parseTime(timeStr) != null;
    }

    // ==========================================
    // 6. HALL SCHEDULE & BOOKINGS
    // ==========================================

    // Get all bookings made by this specific student
    public List<HallBooking> getStudentBookings(int studentId) {
        List<HallBooking> list = new ArrayList<>();
        String sql = "SELECT B.BookingID, B.HallID, H.HallName, B.BookingDate, B.StartTime, B.EndTime, B.Purpose, B.Status " +
                "FROM HallBookings B " +
                "JOIN Halls H ON B.HallID = H.HallID " +
                "WHERE B.StudentID = ? " +
                "ORDER BY B.BookingDate DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(new HallBooking(
                        rs.getInt("BookingID"),
                        rs.getInt("HallID"),
                        rs.getString("HallName"),
                        studentId,
                        rs.getDate("BookingDate"),
                        rs.getString("StartTime"),
                        rs.getString("EndTime"),
                        rs.getString("Purpose"),
                        rs.getString("Status")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Get the full schedule (Classes + Bookings) for a specific Hall on a specific Date
    public List<HallBooking> getHallSchedule(int hallId, LocalDate date) {
        List<HallBooking> list = new ArrayList<>();

        // 1. Fetch Regular Bookings
        String sqlBookings = "SELECT StartTime, EndTime, Status FROM HallBookings WHERE HallID = ? AND BookingDate = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlBookings)) {
            pstmt.setInt(1, hallId);
            pstmt.setDate(2, java.sql.Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new HallBooking(
                        0, hallId, "Unknown", 0, java.sql.Date.valueOf(date),
                        rs.getString("StartTime"),
                        rs.getString("EndTime"),
                        "Reserved Booking",
                        "Occupied (" + rs.getString("Status") + ")"
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }

        // 2. Fetch Recurring Courses (e.g., if date is a Monday, get Monday classes)
        String dayName = date.getDayOfWeek().name(); // "MONDAY"
        dayName = dayName.charAt(0) + dayName.substring(1).toLowerCase(); // "Monday"

        String sqlCourses = "SELECT CourseCode, StartTime, EndTime FROM Courses WHERE HallID = ? AND DayOfWeek = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlCourses)) {
            pstmt.setInt(1, hallId);
            pstmt.setString(2, dayName);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new HallBooking(
                        0, hallId, "Unknown", 0, java.sql.Date.valueOf(date),
                        rs.getString("StartTime"),
                        rs.getString("EndTime"),
                        "University Class",
                        "Class: " + rs.getString("CourseCode")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }

        return list;
    }

    public List<EnrolledCourse> getCompletedCourses(int studentId) {
        List<EnrolledCourse> list = new ArrayList<>();
        // Only fetch courses where Grade IS NOT NULL
        String sql = "SELECT C.CourseCode, C.CourseName, C.Credits, E.Semester, E.Grade " +
                "FROM Enrollments E " +
                "JOIN Courses C ON E.CourseID = C.CourseID " +
                "WHERE E.StudentID = ? AND E.Grade IS NOT NULL " +
                "ORDER BY E.Year ASC, E.Semester ASC"; // Ordered chronologically

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // We use the existing EnrolledCourse model, passing dummy values for schedule info
                    list.add(new EnrolledCourse(
                            rs.getString("CourseCode"),
                            rs.getString("CourseName"),
                            rs.getInt("Credits"),
                            rs.getString("Semester"),
                            rs.getDouble("Grade"),
                            "", "", "", "", 0 // Dummy schedule data
                    ));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

}