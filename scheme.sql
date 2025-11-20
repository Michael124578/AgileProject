-- =============================================
-- SQL Server: Student Information System (SIS)
-- =============================================

-- 1. Create the Database
CREATE DATABASE SIS;
GO

-- 2. Use the Database
USE SIS;
GO

-- 3. Table Definitions

-- 3.1 Admins Table
CREATE TABLE Admins (
                        AdminID INT PRIMARY KEY IDENTITY(1,1),
                        Username NVARCHAR(50) UNIQUE NOT NULL,
                        PasswordHash NVARCHAR(255) NOT NULL,
                        FullName NVARCHAR(100),
                        CreatedAt DATETIME DEFAULT GETDATE()
);

-- 3.2 Students Table
CREATE TABLE Students (
                          StudentID INT PRIMARY KEY IDENTITY(1,1),
                          FirstName NVARCHAR(50) NOT NULL,
                          LastName NVARCHAR(50) NOT NULL,
                          Email NVARCHAR(100) UNIQUE,
                          DateOfBirth DATE,
                          PhoneNumber NVARCHAR(20),
                          EnrollmentDate DATETIME DEFAULT GETDATE(),
                          GPA DECIMAL(3,2) DEFAULT 0.00,
                          CreditsToBePaid INT DEFAULT 0,
                          AmountToBePaid DECIMAL(10,2) DEFAULT 0
);

-- 3.3 Teachers Table
CREATE TABLE Teachers (
                          TeacherID INT PRIMARY KEY IDENTITY(1,1),
                          FirstName NVARCHAR(50) NOT NULL,
                          LastName NVARCHAR(50) NOT NULL,
                          Email NVARCHAR(100) UNIQUE,
                          Department NVARCHAR(50),
                          HireDate DATE
);

-- 3.4 Courses Table
CREATE TABLE Courses (
                         CourseID INT PRIMARY KEY IDENTITY(1,1),
                         CourseCode NVARCHAR(10) UNIQUE NOT NULL,
                         CourseName NVARCHAR(100) NOT NULL,
                         Credits INT,
                         Description NVARCHAR(MAX),
                         DayOfWeek NVARCHAR(15),
                         StartTime NVARCHAR(20),
                         EndTime NVARCHAR(20),
                         RoomNumber NVARCHAR(20)
);

-- 3.5 TeacherAssignments Table
CREATE TABLE TeacherAssignments (
                                    AssignmentID INT PRIMARY KEY IDENTITY(1,1),
                                    TeacherID INT,
                                    CourseID INT,
                                    Semester NVARCHAR(20),
                                    Year INT,
                                    FOREIGN KEY (TeacherID) REFERENCES Teachers(TeacherID),
                                    FOREIGN KEY (CourseID) REFERENCES Courses(CourseID)
);

-- 3.6 Enrollments Table
CREATE TABLE Enrollments (
                             EnrollmentID INT PRIMARY KEY IDENTITY(1,1),
                             StudentID INT,
                             CourseID INT,
                             EnrollmentDate DATETIME DEFAULT GETDATE(),
                             Grade DECIMAL(5,2),
                             Semester NVARCHAR(20),
                             Year INT,
                             FOREIGN KEY (StudentID) REFERENCES Students(StudentID),
                             FOREIGN KEY (CourseID) REFERENCES Courses(CourseID)
);

-- 3.7 Payments Table
CREATE TABLE Payments (
                          PaymentID INT PRIMARY KEY IDENTITY(1,1),
                          StudentID INT,
                          Amount DECIMAL(10,2),
                          PaymentDate DATETIME DEFAULT GETDATE(),
                          FOREIGN KEY (StudentID) REFERENCES Students(StudentID)
);

-- 4. Sample Data Inserts

-- 4.1 Admins
INSERT INTO Admins (Username, PasswordHash, FullName)
VALUES ('admin01', 'hashed_secret_123', 'System Administrator');

-- 4.2 Teachers
INSERT INTO Teachers (FirstName, LastName, Email, Department)
VALUES
    ('John', 'Smith', 'john.smith@school.edu', 'Computer Science'),
    ('Sarah', 'Connor', 'sarah.connor@school.edu', 'Mathematics');

-- 4.3 Students
INSERT INTO Students (FirstName, LastName, Email, DateOfBirth)
VALUES
    ('Alice', 'Wonderland', 'alice@student.edu', '2005-03-15'),
    ('Bob', 'Builder', 'bob@student.edu', '2004-07-22');

-- 4.4 Courses
INSERT INTO Courses (CourseCode, CourseName, Credits, DayOfWeek, StartTime, EndTime, RoomNumber)
VALUES
    ('CS101', 'Intro to Programming', 3, 'Monday', '09:00 AM', '10:30 AM', 'Room 101'),
    ('MATH202', 'Calculus II', 4, 'Tuesday', '11:00 AM', '12:30 PM', 'Lab 2');

-- 4.5 Teacher Assignments
INSERT INTO TeacherAssignments (TeacherID, CourseID, Semester, Year)
VALUES
    (1, 1, 'Fall', 2025),
    (2, 2, 'Fall', 2025);

-- 4.6 Student Enrollments
INSERT INTO Enrollments (StudentID, CourseID, Semester, Year)
VALUES
    (1, 1, 'Fall', 2025),
    (2, 1, 'Fall', 2025),
    (2, 2, 'Fall', 2025);

-- 5. Functions

-- 5.1 GPA Calculation Function
CREATE FUNCTION dbo.GetQualityPoints(@Score DECIMAL(5,2))
    RETURNS DECIMAL(3,2)
AS
BEGIN
    IF @Score >= 93 RETURN 4.00;
    IF @Score >= 89 RETURN 3.70;
    IF @Score >= 84 RETURN 3.30;
    IF @Score >= 80 RETURN 3.00;
    IF @Score >= 76 RETURN 2.70;
    IF @Score >= 73 RETURN 2.30;
    IF @Score >= 70 RETURN 2.00;
    IF @Score >= 67 RETURN 1.70;
    IF @Score >= 64 RETURN 1.30;
    IF @Score >= 60 RETURN 1.00;
RETURN 0.00;
END;
GO

-- 6. Triggers

-- 6.1 Auto-update Fees on Enrollment Changes
IF OBJECT_ID('trg_AutoUpdateFeesOnEnrollment', 'TR') IS NOT NULL
DROP TRIGGER trg_AutoUpdateFeesOnEnrollment;
GO

CREATE TRIGGER trg_AutoUpdateFeesOnEnrollment
    ON Enrollments
    AFTER INSERT, UPDATE, DELETE
    AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @CostPerCredit DECIMAL(10,2) = 2500.00;

    DECLARE @CreditChanges TABLE (StudentID INT, NetChange INT);

INSERT INTO @CreditChanges (StudentID, NetChange)
SELECT
    COALESCE(I.StudentID, D.StudentID),
    ISNULL(SUM(I.Credits),0) - ISNULL(SUM(D.Credits),0)
FROM
    (SELECT E.StudentID, C.Credits
     FROM inserted E
              JOIN Courses C ON E.CourseID = C.CourseID) I
        FULL OUTER JOIN
    (SELECT E.StudentID, C.Credits
     FROM deleted E
              JOIN Courses C ON E.CourseID = C.CourseID) D
    ON I.StudentID = D.StudentID
GROUP BY COALESCE(I.StudentID, D.StudentID);

UPDATE S
SET
    CreditsToBePaid = ISNULL(S.CreditsToBePaid,0) + CC.NetChange,
    AmountToBePaid = ISNULL(S.AmountToBePaid,0) + (CC.NetChange * @CostPerCredit)
    FROM Students S
    INNER JOIN @CreditChanges CC ON S.StudentID = CC.StudentID;
END;
GO

-- 6.2 Auto-update GPA on Grade Changes
IF OBJECT_ID('trg_AutoCalculateGPA', 'TR') IS NOT NULL
DROP TRIGGER trg_AutoCalculateGPA;
GO

CREATE TRIGGER trg_AutoCalculateGPA
    ON Enrollments
    AFTER INSERT, UPDATE, DELETE
    AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @AffectedStudents TABLE (StudentID INT);

INSERT INTO @AffectedStudents
SELECT StudentID FROM inserted
UNION
SELECT StudentID FROM deleted;

UPDATE S
SET GPA = Calculations.NewGPA
    FROM Students S
    INNER JOIN (
        SELECT
            e.StudentID,
            CAST(
                SUM(dbo.GetQualityPoints(e.Grade) * c.Credits) / NULLIF(SUM(c.Credits),0)
            AS DECIMAL(3,2)) AS NewGPA
        FROM Enrollments e
        JOIN Courses c ON e.CourseID = c.CourseID
        WHERE e.Grade IS NOT NULL
        GROUP BY e.StudentID
    ) AS Calculations
ON S.StudentID = Calculations.StudentID
WHERE S.StudentID IN (SELECT StudentID FROM @AffectedStudents);
END;
GO

-- 7. Query: View Students
SELECT * FROM Students;
