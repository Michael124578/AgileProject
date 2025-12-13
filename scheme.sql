USE [master]
GO
/****** Object:  Database [SIS]    Script Date: 09-Dec-25 10:14:23 PM ******/
CREATE DATABASE [SIS]
 CONTAINMENT = NONE
 ON  PRIMARY
( NAME = N'SIS', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.MSSQLSERVER\MSSQL\DATA\SIS.mdf' , SIZE = 8192KB , MAXSIZE = UNLIMITED, FILEGROWTH = 65536KB )
 LOG ON
( NAME = N'SIS_log', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.MSSQLSERVER\MSSQL\DATA\SIS_log.ldf' , SIZE = 8192KB , MAXSIZE = 2048GB , FILEGROWTH = 65536KB )
 WITH CATALOG_COLLATION = DATABASE_DEFAULT, LEDGER = OFF
GO
ALTER DATABASE [SIS] SET COMPATIBILITY_LEVEL = 160
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [SIS].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [SIS] SET ANSI_NULL_DEFAULT OFF
GO
ALTER DATABASE [SIS] SET ANSI_NULLS OFF
GO
ALTER DATABASE [SIS] SET ANSI_PADDING OFF
GO
ALTER DATABASE [SIS] SET ANSI_WARNINGS OFF
GO
ALTER DATABASE [SIS] SET ARITHABORT OFF
GO
ALTER DATABASE [SIS] SET AUTO_CLOSE OFF
GO
ALTER DATABASE [SIS] SET AUTO_SHRINK OFF
GO
ALTER DATABASE [SIS] SET AUTO_UPDATE_STATISTICS ON
GO
ALTER DATABASE [SIS] SET CURSOR_CLOSE_ON_COMMIT OFF
GO
ALTER DATABASE [SIS] SET CURSOR_DEFAULT  GLOBAL
GO
ALTER DATABASE [SIS] SET CONCAT_NULL_YIELDS_NULL OFF
GO
ALTER DATABASE [SIS] SET NUMERIC_ROUNDABORT OFF
GO
ALTER DATABASE [SIS] SET QUOTED_IDENTIFIER OFF
GO
ALTER DATABASE [SIS] SET RECURSIVE_TRIGGERS OFF
GO
ALTER DATABASE [SIS] SET  ENABLE_BROKER
GO
ALTER DATABASE [SIS] SET AUTO_UPDATE_STATISTICS_ASYNC OFF
GO
ALTER DATABASE [SIS] SET DATE_CORRELATION_OPTIMIZATION OFF
GO
ALTER DATABASE [SIS] SET TRUSTWORTHY OFF
GO
ALTER DATABASE [SIS] SET ALLOW_SNAPSHOT_ISOLATION OFF
GO
ALTER DATABASE [SIS] SET PARAMETERIZATION SIMPLE
GO
ALTER DATABASE [SIS] SET READ_COMMITTED_SNAPSHOT OFF
GO
ALTER DATABASE [SIS] SET HONOR_BROKER_PRIORITY OFF
GO
ALTER DATABASE [SIS] SET RECOVERY FULL
GO
ALTER DATABASE [SIS] SET  MULTI_USER
GO
ALTER DATABASE [SIS] SET PAGE_VERIFY CHECKSUM
GO
ALTER DATABASE [SIS] SET DB_CHAINING OFF
GO
ALTER DATABASE [SIS] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF )
GO
ALTER DATABASE [SIS] SET TARGET_RECOVERY_TIME = 60 SECONDS
GO
ALTER DATABASE [SIS] SET DELAYED_DURABILITY = DISABLED
GO
ALTER DATABASE [SIS] SET ACCELERATED_DATABASE_RECOVERY = OFF
GO
EXEC sys.sp_db_vardecimal_storage_format N'SIS', N'ON'
GO
ALTER DATABASE [SIS] SET QUERY_STORE = ON
GO
ALTER DATABASE [SIS] SET QUERY_STORE (OPERATION_MODE = READ_WRITE, CLEANUP_POLICY = (STALE_QUERY_THRESHOLD_DAYS = 30), DATA_FLUSH_INTERVAL_SECONDS = 900, INTERVAL_LENGTH_MINUTES = 60, MAX_STORAGE_SIZE_MB = 1000, QUERY_CAPTURE_MODE = AUTO, SIZE_BASED_CLEANUP_MODE = AUTO, MAX_PLANS_PER_QUERY = 200, WAIT_STATS_CAPTURE_MODE = ON)
GO
USE [SIS]
GO
/****** Object:  UserDefinedFunction [dbo].[GetQualityPoints]    Script Date: 09-Dec-25 10:14:24 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- Create the Database
--CREATE DATABASE SIS;
--GO

---- Use the Database
--USE SIS;
--GO


---- 1. Admins Table
--CREATE TABLE Admins (
--    AdminID INT PRIMARY KEY IDENTITY(1,1),
--    Username NVARCHAR(50) UNIQUE NOT NULL,
--    PasswordHash NVARCHAR(255) NOT NULL, -- Always store hashed passwords, not plain text
--    FullName NVARCHAR(100),
--    CreatedAt DATETIME DEFAULT GETDATE()
--);

---- 2. Students Table
--CREATE TABLE Students (
--    StudentID INT PRIMARY KEY IDENTITY(1,1),
--    FirstName NVARCHAR(50) NOT NULL,
--    LastName NVARCHAR(50) NOT NULL,
--    Email NVARCHAR(100) UNIQUE,
--    DateOfBirth DATE,
--    PhoneNumber NVARCHAR(20),
--    EnrollmentDate DATETIME DEFAULT GETDATE()
--);

---- 3. Teachers Table
--CREATE TABLE Teachers (
--    TeacherID INT PRIMARY KEY IDENTITY(1,1),
--    FirstName NVARCHAR(50) NOT NULL,
--    LastName NVARCHAR(50) NOT NULL,
--    Email NVARCHAR(100) UNIQUE,
--    Department NVARCHAR(50),
--    HireDate DATE
--);

---- 4. Courses Table
--CREATE TABLE Courses (
--    CourseID INT PRIMARY KEY IDENTITY(1,1),
--    CourseCode NVARCHAR(10) UNIQUE NOT NULL, -- e.g., 'CS101'
--    CourseName NVARCHAR(100) NOT NULL,
--    Credits INT,
--    Description NVARCHAR(MAX)
--);


---- 5. TeacherCourseAssignments (Links Teachers to Courses)
---- This tracks WHO teaches WHAT and WHEN (Semester/Year)
--CREATE TABLE TeacherAssignments (
--    AssignmentID INT PRIMARY KEY IDENTITY(1,1),
--    TeacherID INT,
--    CourseID INT,
--    Semester NVARCHAR(20), -- e.g., 'Fall', 'Spring'
--    Year INT,
--    FOREIGN KEY (TeacherID) REFERENCES Teachers(TeacherID),
--    FOREIGN KEY (CourseID) REFERENCES Courses(CourseID)
--);

---- 6. Enrollments (Links Students to Courses)
---- This tracks WHICH student is in WHICH course and their Grade
--CREATE TABLE Enrollments (
--    EnrollmentID INT PRIMARY KEY IDENTITY(1,1),
--    StudentID INT,
--    CourseID INT,
--    EnrollmentDate DATETIME DEFAULT GETDATE(),
--    Grade DECIMAL(5, 2), -- e.g., 95.50
--    Semester NVARCHAR(20),
--    Year INT,
--    FOREIGN KEY (StudentID) REFERENCES Students(StudentID),
--    FOREIGN KEY (CourseID) REFERENCES Courses(CourseID)
--);


---- Insert Admins
--INSERT INTO Admins (Username, PasswordHash, FullName)
--VALUES ('admin01', 'hashed_secret_123', 'System Administrator');

---- Insert Teachers
--INSERT INTO Teachers (FirstName, LastName, Email, Department)
--VALUES
--('John', 'Smith', 'john.smith@school.edu', 'Computer Science'),
--('Sarah', 'Connor', 'sarah.connor@school.edu', 'Mathematics');

---- Insert Students
--INSERT INTO Students (FirstName, LastName, Email, DateOfBirth)
--VALUES
--('Alice', 'Wonderland', 'alice@student.edu', '2005-03-15'),
--('Bob', 'Builder', 'bob@student.edu', '2004-07-22');

---- Insert Courses
--INSERT INTO Courses (CourseCode, CourseName, Credits)
--VALUES
--('CS101', 'Intro to Programming', 3),
--('MATH202', 'Calculus II', 4);

---- Assign Teachers to Courses (John teaches CS101, Sarah teaches MATH202)
--INSERT INTO TeacherAssignments (TeacherID, CourseID, Semester, Year)
--VALUES
--(1, 1, 'Fall', 2025),
--(2, 2, 'Fall', 2025);

---- Enroll Students (Alice in CS101, Bob in CS101 and MATH202)
--INSERT INTO Enrollments (StudentID, CourseID, Semester, Year)
--VALUES
--(1, 1, 'Fall', 2025),
--(2, 1, 'Fall', 2025),
--(2, 2, 'Fall', 2025);

--select * from Students
CREATE FUNCTION [dbo].[GetQualityPoints](@Score DECIMAL(5,2))
RETURNS DECIMAL(3,2)
AS
BEGIN
    -- Standard 4.0 Scale
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

RETURN 0.00; -- Fail
END;

GO
/****** Object:  Table [dbo].[Admins]    Script Date: 09-Dec-25 10:14:24 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Admins](
    [AdminID] [int] IDENTITY(1,1) NOT NULL,
    [Username] [nvarchar](50) NOT NULL,
    [PasswordHash] [nvarchar](255) NOT NULL,
    [FullName] [nvarchar](100) NULL,
    [CreatedAt] [datetime] NULL,
    [ProfilePicPath] [nvarchar](500) NULL,
    PRIMARY KEY CLUSTERED
(
[AdminID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[Courses]    Script Date: 09-Dec-25 10:14:24 PM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[Courses](
    [CourseID] [int] IDENTITY(1,1) NOT NULL,
    [CourseCode] [nvarchar](10) NOT NULL,
    [CourseName] [nvarchar](100) NOT NULL,
    [Credits] [int] NULL,
    [Description] [nvarchar](max) NULL,
    [DayOfWeek] [nvarchar](15) NULL,
    [StartTime] [nvarchar](20) NULL,
    [EndTime] [nvarchar](20) NULL,
    [RoomNumber] [nvarchar](20) NULL,
    [HallID] [int] NULL,
    PRIMARY KEY CLUSTERED
(
[CourseID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[Enrollments]    Script Date: 09-Dec-25 10:14:24 PM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[Enrollments](
    [EnrollmentID] [int] IDENTITY(1,1) NOT NULL,
    [StudentID] [int] NULL,
    [CourseID] [int] NULL,
    [EnrollmentDate] [datetime] NULL,
    [Grade] [decimal](5, 2) NULL,
    [Semester] [nvarchar](20) NULL,
    [Year] [int] NULL,
    PRIMARY KEY CLUSTERED
(
[EnrollmentID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[HallBookings]    Script Date: 09-Dec-25 10:14:24 PM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[HallBookings](
    [BookingID] [int] IDENTITY(1,1) NOT NULL,
    [HallID] [int] NOT NULL,
    [StudentID] [int] NOT NULL,
    [BookingDate] [date] NOT NULL,
    [StartTime] [nvarchar](20) NOT NULL,
    [EndTime] [nvarchar](20) NOT NULL,
    [Purpose] [nvarchar](255) NULL,
    [Status] [nvarchar](20) NULL,
    [CreatedAt] [datetime] NULL,
    PRIMARY KEY CLUSTERED
(
[BookingID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[HallIssues]    Script Date: 09-Dec-25 10:14:24 PM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[HallIssues](
    [IssueID] [int] IDENTITY(1,1) NOT NULL,
    [HallID] [int] NOT NULL,
    [ReporterID] [int] NOT NULL,
    [ReporterType] [nvarchar](20) NOT NULL,
    [IssueDescription] [nvarchar](max) NOT NULL,
    [Status] [nvarchar](20) NULL,
    [ReportedDate] [datetime] NULL,
    PRIMARY KEY CLUSTERED
(
[IssueID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[Halls]    Script Date: 09-Dec-25 10:14:24 PM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[Halls](
    [HallID] [int] IDENTITY(1,1) NOT NULL,
    [HallName] [nvarchar](50) NOT NULL,
    [Capacity] [int] NULL,
    [IsActive] [bit] NULL,
    [HallType] [nvarchar](50) NULL,
    PRIMARY KEY CLUSTERED
(
[HallID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[Payments]    Script Date: 09-Dec-25 10:14:24 PM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[Payments](
    [PaymentID] [int] IDENTITY(1,1) NOT NULL,
    [StudentID] [int] NULL,
    [Amount] [decimal](10, 2) NULL,
    [PaymentDate] [datetime] NULL,
    PRIMARY KEY CLUSTERED
(
[PaymentID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[Students]    Script Date: 09-Dec-25 10:14:24 PM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[Students](
    [StudentID] [int] IDENTITY(1,1) NOT NULL,
    [FirstName] [nvarchar](50) NOT NULL,
    [LastName] [nvarchar](50) NOT NULL,
    [Email] [nvarchar](100) NULL,
    [PhoneNumber] [nvarchar](20) NULL,
    [EnrollmentDate] [datetime] NULL,
    [Password] [nvarchar](50) NULL,
    [ProfilePicPath] [nvarchar](500) NULL,
    [GPA] [float] NULL,
    [creditHours] [int] NULL,
    [weeks] [int] NULL,
    [Wallet] [decimal](10, 2) NULL,
    [AmountToBePaid] [nvarchar](15) NULL,
    [CreditsToBePaid] [varchar](5) NULL,
    PRIMARY KEY CLUSTERED
(
[StudentID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[TeacherAssignments]    Script Date: 09-Dec-25 10:14:24 PM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[TeacherAssignments](
    [AssignmentID] [int] IDENTITY(1,1) NOT NULL,
    [TeacherID] [int] NULL,
    [CourseID] [int] NULL,
    [Semester] [nvarchar](20) NULL,
    [Year] [int] NULL,
    PRIMARY KEY CLUSTERED
(
[AssignmentID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[Teachers]    Script Date: 09-Dec-25 10:14:24 PM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[Teachers](
    [TeacherID] [int] IDENTITY(1,1) NOT NULL,
    [FirstName] [nvarchar](50) NOT NULL,
    [LastName] [nvarchar](50) NOT NULL,
    [Email] [nvarchar](100) NULL,
    [Department] [nvarchar](50) NULL,
    [HireDate] [date] NULL,
    [Password] [nvarchar](50) NULL,
    [ProfilePicPath] [nvarchar](500) NULL,
    PRIMARY KEY CLUSTERED
(
[TeacherID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
    SET IDENTITY_INSERT [dbo].[Admins] ON

    INSERT [dbo].[Admins] ([AdminID], [Username], [PasswordHash], [FullName], [CreatedAt], [ProfilePicPath]) VALUES (1, N'a', N'aa', N'System Administrator', CAST(N'2025-11-19T17:58:43.973' AS DateTime), N'C:\Users\fadij\Desktop\Fady John Fayek\cert.png')
    SET IDENTITY_INSERT [dbo].[Admins] OFF
    GO
    SET IDENTITY_INSERT [dbo].[Courses] ON

    INSERT [dbo].[Courses] ([CourseID], [CourseCode], [CourseName], [Credits], [Description], [DayOfWeek], [StartTime], [EndTime], [RoomNumber], [HallID]) VALUES (1, N'CS101', N'Intro to Programming', 3, NULL, N'Monday', N'09:00 AM', N'10:30 AM', N'Room 101', 2)
    INSERT [dbo].[Courses] ([CourseID], [CourseCode], [CourseName], [Credits], [Description], [DayOfWeek], [StartTime], [EndTime], [RoomNumber], [HallID]) VALUES (2, N'MATH202', N'Calculus II', 4, NULL, N'Tuesday', N'11:00 AM', N'12:30 PM', N'Lab 2', 1)
    INSERT [dbo].[Courses] ([CourseID], [CourseCode], [CourseName], [Credits], [Description], [DayOfWeek], [StartTime], [EndTime], [RoomNumber], [HallID]) VALUES (3, N'CSE123', N'Database', 2, NULL, N'Wednesday', N'03:30 PM', N'05:30 PM', NULL, 2)
    SET IDENTITY_INSERT [dbo].[Courses] OFF
    GO
    SET IDENTITY_INSERT [dbo].[Enrollments] ON

    INSERT [dbo].[Enrollments] ([EnrollmentID], [StudentID], [CourseID], [EnrollmentDate], [Grade], [Semester], [Year]) VALUES (24, 4, 1, CAST(N'2025-11-20T03:03:41.217' AS DateTime), CAST(100.00 AS Decimal(5, 2)), N'Spring', 2026)
    INSERT [dbo].[Enrollments] ([EnrollmentID], [StudentID], [CourseID], [EnrollmentDate], [Grade], [Semester], [Year]) VALUES (25, 4, 2, CAST(N'2025-11-20T03:03:43.080' AS DateTime), CAST(95.00 AS Decimal(5, 2)), N'Spring', 2026)
    INSERT [dbo].[Enrollments] ([EnrollmentID], [StudentID], [CourseID], [EnrollmentDate], [Grade], [Semester], [Year]) VALUES (30, 1, 1, CAST(N'2025-11-20T18:48:47.743' AS DateTime), CAST(40.00 AS Decimal(5, 2)), N'Spring', 2026)
    INSERT [dbo].[Enrollments] ([EnrollmentID], [StudentID], [CourseID], [EnrollmentDate], [Grade], [Semester], [Year]) VALUES (31, 1, 2, CAST(N'2025-11-20T18:48:49.690' AS DateTime), CAST(90.00 AS Decimal(5, 2)), N'Spring', 2026)
    INSERT [dbo].[Enrollments] ([EnrollmentID], [StudentID], [CourseID], [EnrollmentDate], [Grade], [Semester], [Year]) VALUES (32, 9, 1, CAST(N'2025-11-20T21:32:10.013' AS DateTime), NULL, N'Spring', 2026)
    INSERT [dbo].[Enrollments] ([EnrollmentID], [StudentID], [CourseID], [EnrollmentDate], [Grade], [Semester], [Year]) VALUES (33, 9, 2, CAST(N'2025-11-20T21:32:11.610' AS DateTime), NULL, N'Spring', 2026)
    INSERT [dbo].[Enrollments] ([EnrollmentID], [StudentID], [CourseID], [EnrollmentDate], [Grade], [Semester], [Year]) VALUES (34, 10, 1, CAST(N'2025-11-22T11:54:39.800' AS DateTime), NULL, N'Spring', 2026)
    INSERT [dbo].[Enrollments] ([EnrollmentID], [StudentID], [CourseID], [EnrollmentDate], [Grade], [Semester], [Year]) VALUES (35, 10, 2, CAST(N'2025-11-22T12:17:09.533' AS DateTime), NULL, N'Spring', 2026)
    INSERT [dbo].[Enrollments] ([EnrollmentID], [StudentID], [CourseID], [EnrollmentDate], [Grade], [Semester], [Year]) VALUES (36, 1, 3, CAST(N'2025-12-09T21:46:24.403' AS DateTime), NULL, N'Spring', 2026)
    SET IDENTITY_INSERT [dbo].[Enrollments] OFF
    GO
    SET IDENTITY_INSERT [dbo].[HallBookings] ON

    INSERT [dbo].[HallBookings] ([BookingID], [HallID], [StudentID], [BookingDate], [StartTime], [EndTime], [Purpose], [Status], [CreatedAt]) VALUES (1, 1, 1, CAST(N'2026-01-14' AS Date), N'09:00 AM', N'10:30 AM', N'working on a project', N'Confirmed', CAST(N'2025-12-09T22:05:21.353' AS DateTime))
    SET IDENTITY_INSERT [dbo].[HallBookings] OFF
    GO
    SET IDENTITY_INSERT [dbo].[HallIssues] ON

    INSERT [dbo].[HallIssues] ([IssueID], [HallID], [ReporterID], [ReporterType], [IssueDescription], [Status], [ReportedDate]) VALUES (1, 1, 1, N'Student', N'Projector is flickering.', N'Resolved', CAST(N'2025-12-09T20:50:09.910' AS DateTime))
    INSERT [dbo].[HallIssues] ([IssueID], [HallID], [ReporterID], [ReporterType], [IssueDescription], [Status], [ReportedDate]) VALUES (2, 2, 1, N'Student', N'PC number 4 isn''t working', N'Open', CAST(N'2025-12-09T21:47:02.090' AS DateTime))
    SET IDENTITY_INSERT [dbo].[HallIssues] OFF
    GO
    SET IDENTITY_INSERT [dbo].[Halls] ON

    INSERT [dbo].[Halls] ([HallID], [HallName], [Capacity], [IsActive], [HallType]) VALUES (1, N'Lab 2', 30, 1, N'Classroom')
    INSERT [dbo].[Halls] ([HallID], [HallName], [Capacity], [IsActive], [HallType]) VALUES (2, N'Room 101', 30, 1, N'Classroom')
    INSERT [dbo].[Halls] ([HallID], [HallName], [Capacity], [IsActive], [HallType]) VALUES (3, N'Physics Lab', 25, 1, N'Lab')
    SET IDENTITY_INSERT [dbo].[Halls] OFF
    GO
    SET IDENTITY_INSERT [dbo].[Students] ON

    INSERT [dbo].[Students] ([StudentID], [FirstName], [LastName], [Email], [PhoneNumber], [EnrollmentDate], [Password], [ProfilePicPath], [GPA], [creditHours], [weeks], [Wallet], [AmountToBePaid], [CreditsToBePaid]) VALUES (1, N'Fady', N'John', N'f', NULL, CAST(N'2025-11-19T17:58:43.977' AS DateTime), N'f', N'C:\Users\fadij\Desktop\Fady John Fayek\graduation pic - Copy.jpg', 2.11, NULL, NULL, CAST(72000.00 AS Decimal(10, 2)), N'4500.00', N'2')
    INSERT [dbo].[Students] ([StudentID], [FirstName], [LastName], [Email], [PhoneNumber], [EnrollmentDate], [Password], [ProfilePicPath], [GPA], [creditHours], [weeks], [Wallet], [AmountToBePaid], [CreditsToBePaid]) VALUES (2, N'Bob', N'Builder', N'bob@student.edu', NULL, CAST(N'2025-11-19T17:58:43.977' AS DateTime), NULL, NULL, NULL, NULL, NULL, CAST(0.00 AS Decimal(10, 2)), N'0', N'0')
    INSERT [dbo].[Students] ([StudentID], [FirstName], [LastName], [Email], [PhoneNumber], [EnrollmentDate], [Password], [ProfilePicPath], [GPA], [creditHours], [weeks], [Wallet], [AmountToBePaid], [CreditsToBePaid]) VALUES (3, N'John', N'Doe', N'john.doe@gmail.com', NULL, CAST(N'2025-11-19T18:51:16.527' AS DateTime), N'P@ssword123', NULL, NULL, NULL, NULL, CAST(0.00 AS Decimal(10, 2)), N'0', N'0')
    INSERT [dbo].[Students] ([StudentID], [FirstName], [LastName], [Email], [PhoneNumber], [EnrollmentDate], [Password], [ProfilePicPath], [GPA], [creditHours], [weeks], [Wallet], [AmountToBePaid], [CreditsToBePaid]) VALUES (4, N'Michael', N'Nagi', N'm', NULL, CAST(N'2025-11-20T00:36:42.230' AS DateTime), N'm', NULL, 4, NULL, NULL, CAST(193500.00 AS Decimal(10, 2)), N'0.00', N'0')
    INSERT [dbo].[Students] ([StudentID], [FirstName], [LastName], [Email], [PhoneNumber], [EnrollmentDate], [Password], [ProfilePicPath], [GPA], [creditHours], [weeks], [Wallet], [AmountToBePaid], [CreditsToBePaid]) VALUES (9, N'Farid', N'Fahmy', N'Farid@gmail.com', NULL, CAST(N'2025-11-20T21:31:26.247' AS DateTime), N'Farid@12345', NULL, NULL, NULL, NULL, CAST(0.00 AS Decimal(10, 2)), N'17500.00', N'7')
    INSERT [dbo].[Students] ([StudentID], [FirstName], [LastName], [Email], [PhoneNumber], [EnrollmentDate], [Password], [ProfilePicPath], [GPA], [creditHours], [weeks], [Wallet], [AmountToBePaid], [CreditsToBePaid]) VALUES (10, N'Fady', N'John', N'fadijohn9@gmail.com', NULL, CAST(N'2025-11-22T11:54:03.267' AS DateTime), N'F@di12345', N'C:\Users\fadij\Desktop\Fady John Fayek\graduation pic - Copy.jpg', NULL, NULL, NULL, CAST(0.00 AS Decimal(10, 2)), N'17500.00', N'7')
    INSERT [dbo].[Students] ([StudentID], [FirstName], [LastName], [Email], [PhoneNumber], [EnrollmentDate], [Password], [ProfilePicPath], [GPA], [creditHours], [weeks], [Wallet], [AmountToBePaid], [CreditsToBePaid]) VALUES (11, N'Michael', N'Nagi', N'miky@gmail.com', NULL, CAST(N'2025-11-22T12:24:38.533' AS DateTime), N'123mnbMNB@', N'C:\Users\fadij\Desktop\Fady John Fayek\graduation pic - Copy.jpg', NULL, NULL, NULL, CAST(0.00 AS Decimal(10, 2)), NULL, NULL)
    SET IDENTITY_INSERT [dbo].[Students] OFF
    GO
    SET IDENTITY_INSERT [dbo].[TeacherAssignments] ON

    INSERT [dbo].[TeacherAssignments] ([AssignmentID], [TeacherID], [CourseID], [Semester], [Year]) VALUES (1, 1, 1, N'Fall', 2025)
    INSERT [dbo].[TeacherAssignments] ([AssignmentID], [TeacherID], [CourseID], [Semester], [Year]) VALUES (2, 2, 2, N'Fall', 2025)
    INSERT [dbo].[TeacherAssignments] ([AssignmentID], [TeacherID], [CourseID], [Semester], [Year]) VALUES (3, 1, 3, N'Spring 26', 2026)
    SET IDENTITY_INSERT [dbo].[TeacherAssignments] OFF
    GO
    SET IDENTITY_INSERT [dbo].[Teachers] ON

    INSERT [dbo].[Teachers] ([TeacherID], [FirstName], [LastName], [Email], [Department], [HireDate], [Password], [ProfilePicPath]) VALUES (1, N'John', N'Smith', N't', N'Computer Science', NULL, N't', N'C:\Users\fadij\Desktop\Fady John Fayek\graduation pic - Copy.jpg')
    INSERT [dbo].[Teachers] ([TeacherID], [FirstName], [LastName], [Email], [Department], [HireDate], [Password], [ProfilePicPath]) VALUES (2, N'Sarah', N'Connor', N'rr', N'Mathematics', NULL, N'rr', N'C:\Users\fadij\Desktop\Fady John Fayek\cert.png')
    INSERT [dbo].[Teachers] ([TeacherID], [FirstName], [LastName], [Email], [Department], [HireDate], [Password], [ProfilePicPath]) VALUES (3, N'Teacher1', N'T1', N'tt', N'Mechanical', CAST(N'2025-11-20' AS Date), N'tt', NULL)
    SET IDENTITY_INSERT [dbo].[Teachers] OFF
    GO
    SET ANSI_PADDING ON
    GO
/****** Object:  Index [UQ__Admins__536C85E46CF39788]    Script Date: 09-Dec-25 10:14:24 PM ******/
ALTER TABLE [dbo].[Admins] ADD UNIQUE NONCLUSTERED
    (
    [Username] ASC
    )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    GO
    SET ANSI_PADDING ON
    GO
/****** Object:  Index [UQ__Courses__FC00E00065FAFEEA]    Script Date: 09-Dec-25 10:14:24 PM ******/
ALTER TABLE [dbo].[Courses] ADD UNIQUE NONCLUSTERED
    (
    [CourseCode] ASC
    )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    GO
    SET ANSI_PADDING ON
    GO
/****** Object:  Index [UQ__Halls__C56C29DE37036951]    Script Date: 09-Dec-25 10:14:24 PM ******/
ALTER TABLE [dbo].[Halls] ADD UNIQUE NONCLUSTERED
    (
    [HallName] ASC
    )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    GO
    SET ANSI_PADDING ON
    GO
/****** Object:  Index [UQ__Students__A9D105347F1235C1]    Script Date: 09-Dec-25 10:14:24 PM ******/
ALTER TABLE [dbo].[Students] ADD UNIQUE NONCLUSTERED
    (
    [Email] ASC
    )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    GO
    SET ANSI_PADDING ON
    GO
/****** Object:  Index [UQ__Teachers__A9D10534953CA193]    Script Date: 09-Dec-25 10:14:24 PM ******/
ALTER TABLE [dbo].[Teachers] ADD UNIQUE NONCLUSTERED
    (
    [Email] ASC
    )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    GO
ALTER TABLE [dbo].[Admins] ADD  DEFAULT (getdate()) FOR [CreatedAt]
    GO
ALTER TABLE [dbo].[Enrollments] ADD  DEFAULT (getdate()) FOR [EnrollmentDate]
    GO
ALTER TABLE [dbo].[HallBookings] ADD  DEFAULT ('Confirmed') FOR [Status]
    GO
ALTER TABLE [dbo].[HallBookings] ADD  DEFAULT (getdate()) FOR [CreatedAt]
    GO
ALTER TABLE [dbo].[HallIssues] ADD  DEFAULT ('Open') FOR [Status]
    GO
ALTER TABLE [dbo].[HallIssues] ADD  DEFAULT (getdate()) FOR [ReportedDate]
    GO
ALTER TABLE [dbo].[Halls] ADD  DEFAULT ((30)) FOR [Capacity]
    GO
ALTER TABLE [dbo].[Halls] ADD  DEFAULT ((1)) FOR [IsActive]
    GO
ALTER TABLE [dbo].[Halls] ADD  DEFAULT ('Classroom') FOR [HallType]
    GO
ALTER TABLE [dbo].[Payments] ADD  DEFAULT (getdate()) FOR [PaymentDate]
    GO
ALTER TABLE [dbo].[Students] ADD  DEFAULT (getdate()) FOR [EnrollmentDate]
    GO
ALTER TABLE [dbo].[Students] ADD  DEFAULT ((0.00)) FOR [Wallet]
    GO
ALTER TABLE [dbo].[Courses]  WITH CHECK ADD  CONSTRAINT [FK_Courses_Halls] FOREIGN KEY([HallID])
    REFERENCES [dbo].[Halls] ([HallID])
    GO
ALTER TABLE [dbo].[Courses] CHECK CONSTRAINT [FK_Courses_Halls]
    GO
ALTER TABLE [dbo].[Enrollments]  WITH CHECK ADD FOREIGN KEY([CourseID])
    REFERENCES [dbo].[Courses] ([CourseID])
    GO
ALTER TABLE [dbo].[Enrollments]  WITH CHECK ADD FOREIGN KEY([StudentID])
    REFERENCES [dbo].[Students] ([StudentID])
    GO
ALTER TABLE [dbo].[HallBookings]  WITH CHECK ADD FOREIGN KEY([HallID])
    REFERENCES [dbo].[Halls] ([HallID])
    GO
ALTER TABLE [dbo].[HallBookings]  WITH CHECK ADD FOREIGN KEY([StudentID])
    REFERENCES [dbo].[Students] ([StudentID])
    GO
ALTER TABLE [dbo].[HallIssues]  WITH CHECK ADD FOREIGN KEY([HallID])
    REFERENCES [dbo].[Halls] ([HallID])
    GO
ALTER TABLE [dbo].[Payments]  WITH CHECK ADD FOREIGN KEY([StudentID])
    REFERENCES [dbo].[Students] ([StudentID])
    GO
ALTER TABLE [dbo].[TeacherAssignments]  WITH CHECK ADD FOREIGN KEY([CourseID])
    REFERENCES [dbo].[Courses] ([CourseID])
    GO
ALTER TABLE [dbo].[TeacherAssignments]  WITH CHECK ADD FOREIGN KEY([TeacherID])
    REFERENCES [dbo].[Teachers] ([TeacherID])
    GO
/****** Object:  Trigger [dbo].[trg_AutoCalculateGPA]    Script Date: 09-Dec-25 10:14:24 PM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO

CREATE TRIGGER [dbo].[trg_AutoCalculateGPA]
ON [dbo].[Enrollments]
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
ALTER TABLE [dbo].[Enrollments] ENABLE TRIGGER [trg_AutoCalculateGPA]
    GO
/****** Object:  Trigger [dbo].[trg_AutoUpdateFeesOnEnrollment]    Script Date: 09-Dec-25 10:14:24 PM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO

CREATE TRIGGER [dbo].[trg_AutoUpdateFeesOnEnrollment]
ON [dbo].[Enrollments]
AFTER INSERT, UPDATE, DELETE
    AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @CostPerCredit DECIMAL(10, 2) = 2500.00;

    -- 1. Calculate Net Change in Credits
    DECLARE @CreditChanges TABLE (StudentID INT, NetChange INT);

INSERT INTO @CreditChanges (StudentID, NetChange)
SELECT
    COALESCE(I.StudentID, D.StudentID),
    (ISNULL(SUM(I.Credits), 0) - ISNULL(SUM(D.Credits), 0))
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

-- 2. Update Students Table with ISNULL checks
UPDATE S
SET
    CreditsToBePaid = ISNULL(S.CreditsToBePaid, 0) + CC.NetChange,

    -- Add cost of NEW credits to existing Debt
    AmountToBePaid = ISNULL(S.AmountToBePaid, 0) + (CC.NetChange * @CostPerCredit)
    FROM Students S
    INNER JOIN @CreditChanges CC ON S.StudentID = CC.StudentID;
END;

GO
ALTER TABLE [dbo].[Enrollments] ENABLE TRIGGER [trg_AutoUpdateFeesOnEnrollment]
    GO
    USE [master]
    GO
ALTER DATABASE [SIS] SET  READ_WRITE
GO


USE SIS;
GO

-- Increase column size to accommodate SHA-256 hash (64 chars)
ALTER TABLE Students ALTER COLUMN Password NVARCHAR(64);
ALTER TABLE Teachers ALTER COLUMN Password NVARCHAR(64);
-- Admin table already has NVARCHAR(255) for PasswordHash, so it is fine.


USE [SIS]
GO

CREATE TABLE [dbo].[Materials](
    [MaterialID] [int] IDENTITY(1,1) NOT NULL,
    [CourseID] [int] NOT NULL,
    [FileName] [nvarchar](255) NOT NULL,
    [FilePath] [nvarchar](500) NOT NULL,
    [UploadDate] [datetime] DEFAULT GETDATE(),
    PRIMARY KEY CLUSTERED ([MaterialID] ASC),
    FOREIGN KEY ([CourseID]) REFERENCES [dbo].[Courses] ([CourseID])
    );
GO


USE [SIS]
GO

-- 1. Create Parents Table (Links to a Student)
CREATE TABLE Parents (
                         ParentID INT PRIMARY KEY IDENTITY(1,1),
                         FirstName NVARCHAR(50) NOT NULL,
                         LastName NVARCHAR(50) NOT NULL,
                         Email NVARCHAR(100) UNIQUE NOT NULL,
                         Password NVARCHAR(255) NOT NULL,
                         StudentID INT NOT NULL, -- The child
                         FOREIGN KEY (StudentID) REFERENCES Students(StudentID)
);

-- 2. Create Messages Table (Conversation between Parent and Teacher)
CREATE TABLE Messages (
                          MessageID INT PRIMARY KEY IDENTITY(1,1),
                          ParentID INT NOT NULL,
                          TeacherID INT NOT NULL,
                          SenderType NVARCHAR(10) NOT NULL, -- 'Parent' or 'Teacher'
                          MessageText NVARCHAR(MAX),
                          SentDate DATETIME DEFAULT GETDATE(),
                          FOREIGN KEY (ParentID) REFERENCES Parents(ParentID),
                          FOREIGN KEY (TeacherID) REFERENCES Teachers(TeacherID)
);

-- 3. Insert Dummy Parent (Linked to StudentID 1 'Fady John')
INSERT INTO Parents (FirstName, LastName, Email, Password, StudentID)
VALUES ('George', 'John', 'p', 'p', 1);
GO
