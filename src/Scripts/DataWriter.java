package Scripts;

import DomainModel.*;
import Utilities.EncryptedFilesReader;
import Utilities.FileWriter;
import Utilities.JsonParser;


import java.util.ArrayList;
import java.io.*;
import java.util.HashMap;

public class DataWriter extends FileWriter {
    private Encryptor encryptor;
    private static final String CREDS_PATH = System.getProperty("user.dir") + "\\creds.json";
    private String credsToWrite;

    public DataWriter(){}

    public void writeJson(CredentialsManager credentialsManager) {
        ArrayList<Domain> domains = credentialsManager.getDomains();
        ArrayList<HashMap<String, String>> jsonList = credentialsManager.getJsonList();

        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Domain domain : domains) {
            HashMap<String, String> jsonMap = jsonList.get(i++);
            sb.append(domain.getDomain()).append("{\n");
            for (String key : jsonMap.keySet()) {
                sb.append(key).append("{").append(jsonMap.get(key)).append("}\n");
            }
            sb.append("}");
            if (i < domains.size()) {
                sb.append(";\n");
            }
        }
        super.write(CREDS_PATH, sb.toString());
    }
}
