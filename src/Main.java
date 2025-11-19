import Model.*;
import DAO.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        //StudentDAO studentDAO = new StudentDAO();

        // Inside your main method:
        StudentDAO dao = new StudentDAO();

        // 1. Signup Test
//        boolean isRegistered = dao.signup("John", "Doe", "john.doe@gmail.com", "P@ssword123");
//        if (isRegistered) System.out.println("Signup Success!");

        // 2. Login Test
        Student student = dao.login("john.doe@gmail.com", "P@ssword123");

        if (student != null) {
            // 3. Add Course (Assuming CS101 exists in DB)
            //dao.enrollCourse(student.getStudentId(), "CS101", "Spring", 2025);

            // 4. Drop Course
            dao.dropCourse(student.getStudentId(), "CS101");
        }
    }
}