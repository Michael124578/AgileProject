public class Main {
    public static void main(String[] args) {
        Register registerHandler = new Register();
        Boolean s = registerHandler.isValidUsername("Michael2");
        Boolean p = registerHandler.isValidPassword("dsfda221sa@M");

        if(p && s){
            System.out.println("Valid Entry");
        }
        else{
            System.out.println("Invalid Entry");
        }
    }
}