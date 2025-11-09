package Scripts;

import Utilities.FileReader;

public class DataReader extends FileReader {
    private static final String CREDS_PATH = System.getProperty("user.dir") + "\\creds.json";
    public DataReader() {}

    public String readEncryptedCredentials() {
        return super.readFile(new java.io.File(CREDS_PATH));
    }

}
