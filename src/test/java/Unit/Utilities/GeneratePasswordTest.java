package Unit.Utilities;

import Utilities.PasswordGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GeneratePasswordTest {
    @Test
    void generatesDifferentPasswords() {
        String pass1 = PasswordGenerator.generatePassword();
        String pass2 = PasswordGenerator.generatePassword();

        assertNotEquals(pass1, pass2);
    }
    @Test
    void passwordCorrectLength(){
        int passLen = PasswordGenerator.generatePassword().length();
        assertEquals(22, passLen);
    }
}