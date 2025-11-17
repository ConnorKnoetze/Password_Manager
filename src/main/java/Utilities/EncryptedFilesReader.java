package Utilities;

import java.io.File;
import java.util.ArrayList;

public class EncryptedFilesReader extends FileReader {
    private static final String TEXT_FILES_PATH = System.getProperty("user.dir") + "\\textfiles\\";
    private String storableCreds="";

    public EncryptedFilesReader() {}

    public void ReadFiles() {
        ArrayList<File> textFiles = new ArrayList<File>();

        File keyObj = new File(TEXT_FILES_PATH + "\\key.txt");
        textFiles.add(keyObj);
        File keyIvObj = new File(TEXT_FILES_PATH + "\\key_iv.txt");
        textFiles.add(keyIvObj);
        File passIvObj = new File(TEXT_FILES_PATH + "\\pass_iv.txt");
        textFiles.add(passIvObj);
        File passwordObj = new File(TEXT_FILES_PATH + "\\password.txt");
        textFiles.add(passwordObj);

        StringBuilder content = new StringBuilder();

        for (File file : textFiles) {
            String fileContent = super.readFile(file);
            String type = file.getName().replace(".txt", "");
            content.append(type).append("{").append(fileContent).append("}\n");
        }
        this.storableCreds = content.toString();
    }

    public String getStorableCreds() {
        return storableCreds;
    }
}