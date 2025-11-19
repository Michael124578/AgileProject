import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner cin = new Scanner(System.in);
        Register registerHandler = new Register();

        System.out.println("Enter your First Name: ");
        String firstName = cin.nextLine();
        System.out.println("Enter your Last Name: ");
        String lastName = cin.nextLine();
        System.out.println("Enter your UserName: ");
        String userName = cin.nextLine();
        System.out.println("Enter your Email: ");
        String email = cin.nextLine();
        System.out.println("Enter your Password: ");
        String password = cin.nextLine();

        if(registerHandler.isValidFirstName(firstName) && registerHandler.isValidLastName(lastName) &&
                registerHandler.isValidUsername(userName) && registerHandler.isValidEmail(email) &&
                registerHandler.isValidPassword(password)){
            System.out.println("Valid Entry");
        }
        else{
            System.out.println("Invalid Entry");
        }
    }
}