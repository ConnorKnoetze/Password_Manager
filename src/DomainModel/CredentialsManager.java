package DomainModel;

import Scripts.DataReader;
import Scripts.Encryptor;
import Scripts.Stego;
import Utilities.EncryptedFilesReader;
import Utilities.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CredentialsManager {
    private final ArrayList<HashMap<String, String>> jsonList;
    private final ArrayList<Domain> domains;
    private ArrayList<Domain> searchedDomains = new ArrayList<>(){};
    private ArrayList<HashMap<String, String>> searchedJsonList = new ArrayList<>(){};
    private final Encryptor encryptor;

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

    public void setSearched(ArrayList<Domain> searchedDomains, ArrayList<HashMap<String, String>> searchedJsonList) {
        this.searchedDomains = searchedDomains;
        this.searchedJsonList = searchedJsonList;
    }

    public ArrayList<Domain> getSearchedDomains(){
        return this.searchedDomains;
    }

    public ArrayList<HashMap<String, String>> getSearchedJsonList(){
        return this.searchedJsonList;
    }

    public static void main(String[] args){
        DataReader dataReader = new DataReader();
        String jsonContents = dataReader.readEncryptedCredentials();
        JsonParser jsonParser = new JsonParser(jsonContents);
        CredentialsManager credentialsManager = new CredentialsManager(jsonParser, Stego.extractString());


    }
}
