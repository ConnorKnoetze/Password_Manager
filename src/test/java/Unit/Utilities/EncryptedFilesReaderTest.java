package Unit.Utilities;

import Utilities.EncryptedFilesReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class EncryptedFilesReaderTest {
    private static final String TEXT_FILES_PATH = System.getProperty("user.dir") + "\\textfiles\\";
    private EncryptedFilesReader reader;

    @BeforeEach
    void setUp(){
        reader = new EncryptedFilesReader();
    }

    @Test
    void ensureFilepathsExist(){
        boolean keyExists = new File(TEXT_FILES_PATH + "\\key.txt").exists();
        boolean keyIvExists = new File(TEXT_FILES_PATH + "\\key_iv.txt").exists();
        boolean passIvExists = new File(TEXT_FILES_PATH + "\\pass_iv.txt").exists();
        boolean passwordExists = new File(TEXT_FILES_PATH + "\\password.txt").exists();

        assertTrue(keyExists);
        assertTrue(keyIvExists);
        assertTrue(passIvExists);
        assertTrue(passwordExists);
    }

    @Test
    void containsAllItemsInStorableCreds(){
        reader.ReadFiles();
        String storableCreds = reader.getStorableCreds();
        boolean containsKey = storableCreds.contains("key{");
        boolean containsKeyIv = storableCreds.contains("key_iv{");
        boolean containsPassIv = storableCreds.contains("pass_iv{");
        boolean containsPassword = storableCreds.contains("password{");

        assertTrue(containsKey);
        assertTrue(containsKeyIv);
        assertTrue(containsPassIv);
        assertTrue(containsPassword);
    }
}
