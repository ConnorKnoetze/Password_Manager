package Utilities;

public class PasswordGenerator {
    public static String generatePassword(){
        String[] CHAR_SETS = {
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ", // Uppercase letters
                "abcdefghijklmnopqrstuvwxyz", // Lowercase letters
                "0123456789",                 // Digits
                "!@#$%^&*-_=+?"  // Special characters
        };

        StringBuilder password = new StringBuilder();

        for (int i = 0; i <= 21; i++) {
            // Logic to generate password using CHAR_SETS
            int charSetIndex = (int) (Math.random() * CHAR_SETS.length);
            int charIndex = (int) (Math.random() * CHAR_SETS[charSetIndex].length());
            password.append(CHAR_SETS[charSetIndex].charAt(charIndex));
        }

        return password.toString();
    }
}
