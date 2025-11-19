import Model.*;
import DAO.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        StudentDAO studentDAO = new StudentDAO();

        System.out.println("--- STUDENT LOGIN ---");
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        // Attempt Login
        Student currentUser = studentDAO.login(email, password);

        if (currentUser != null) {
            System.out.println("Login Successful!");
            System.out.println(currentUser.toString());

            // Here you would open your Student Dashboard Menu...
        } else {
            System.out.println("Invalid email or password.");
        }

        scanner.close();
    }
}