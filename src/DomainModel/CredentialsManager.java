package DomainModel;

import Scripts.Encryptor;
import Utilities.EncryptedFilesReader;
import Utilities.JsonParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CredentialsManager {
    private ArrayList<HashMap<String, String>> jsonList;
    private ArrayList<Domain> domains;
    private Encryptor encryptor;

    public CredentialsManager(){
        jsonList = new ArrayList<>(){};
        domains = new ArrayList<>(){};
        encryptor = new Encryptor("MASTER_KEY");
    }

    public CredentialsManager(JsonParser jp, String MK){
        jsonList = jp.getJsonList();
        domains = jp.getDomains();
        encryptor = new Encryptor(MK);
    }

    public void addCredential(Credential credential){
        PlainText pt = new PlainText(credential.getUsername(), credential.getPassword());
        try{
            encryptor.encrypt(pt);
        }catch (IOException ignored){}

        EncryptedFilesReader reader = new EncryptedFilesReader();
        reader.ReadFiles();
        String storableCreds = reader.getStorableCreds();

        System.out.println(storableCreds);

        domains.add(credential.getDomainObject());

        HashMap<String, String> jsonMap = new HashMap<>();
        StringBuilder sb = new StringBuilder();

        boolean key = true;

        int prevIndex = 0;

        for (int i = 0; i < storableCreds.length()-1; i++) {
            if (storableCreds.charAt(i) == '{') {
                String part = storableCreds.substring(prevIndex, i).trim();
                if (key) {
                    sb = new StringBuilder();
                    sb.append(part);
                    key = false;
                }
                prevIndex = i + 1;
            } else if (storableCreds.charAt(i) == '}') {
                String part = storableCreds.substring(prevIndex, i).trim();
                jsonMap.put(sb.toString(), part);
                prevIndex = i + 1;
                key = true;
            }
        }
        jsonList.add(jsonMap);
    }

    public void deleteCredential(String domain){
        for (int i = 0; i < domains.size(); i++) {
            if (domains.get(i).getDomain().equals(domain)) {
                domains.remove(i);
                jsonList.remove(i);
                break;
            }
        }
    }

    public ArrayList<HashMap<String, String>> getJsonList(){
        return this.jsonList;
    }

    public ArrayList<Domain> getDomains() {
        return domains;
    }

    public static void main(String[] args){
        Credential cred = new Credential(new Domain("youtube"), "Connor", "pass");

        CredentialsManager credentialsManager = new CredentialsManager();

        credentialsManager.addCredential(cred);
        credentialsManager.deleteCredential("youtube");
    }
}
