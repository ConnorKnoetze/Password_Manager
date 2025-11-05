package Scripts;

import DomainModel.*;
import Utilities.EncryptedFilesReader;
import Utilities.FileWriter;
import Utilities.JsonParser;


import java.util.ArrayList;
import java.io.*;
import java.util.HashMap;

public class DataWriter extends FileWriter {
    private CredentialsManager credentialsManager;
    private Encryptor encryptor;
    private static final String CREDS_PATH = System.getProperty("user.dir") + "\\creds.json";
    private String credsToWrite;

    public DataWriter(){}

    public DataWriter(CredentialsManager credentialsManager, String MasterKey) {
        this.credentialsManager = credentialsManager;
        this.encryptor = new Encryptor(MasterKey);
    }

    public String EncryptCredentials(String JSONContents) {
        boolean newCred = this.credentialsManager.getCredentials().size() > 1;
        if (!JSONContents.isEmpty()){
            JSONContents = JSONContents + ";\n";
        }
        StringBuilder sb = new StringBuilder(JSONContents);
        for (Credential credential : this.credentialsManager.getCredentials()) {
            // Encrypt each credential
            PlainText plainText = new PlainText(credential.getUsername(), credential.getPassword());

            try {
                this.encryptor.encrypt(plainText);
                System.out.println("Encryption successful");
            }
            catch (IOException e) {
                System.err.println("Encryption failed: " + e.getMessage());
            }

            EncryptedFilesReader reader = new EncryptedFilesReader();
            reader.ReadFiles();
            String storableCreds = reader.getStorableCreds();

            sb.append(credential.getDomain()).append("{\n");
            sb.append(storableCreds).append("}");

            if (newCred){
                sb.append(";");
            }
        }

        this.credsToWrite = sb.toString();
        return this.credsToWrite;
    }

    public void writeCipherTexts() {
        super.write(CREDS_PATH, this.credsToWrite);
    }

    public void writeJson(JsonParser jsonParser) {
        DomainsList domains = jsonParser.getDomains();
        ArrayList<HashMap<String, String>> jsonList = jsonParser.getJsonList();

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
