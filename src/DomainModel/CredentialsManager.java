package DomainModel;

import java.util.ArrayList;

public class CredentialsManager {
    private ArrayList<Credential> credentials;

    public CredentialsManager(){
        this.credentials = new ArrayList<Credential>();
    }

    public void addCredential(Credential credential){
        this.credentials.add(credential);
    }

    public ArrayList<Credential> getCredentials(){
        return this.credentials;
    }

    public boolean isEmpty(){
        return this.credentials.isEmpty();
    }

    public void clearCredentials(){
        this.credentials.clear();
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("CredentialsManager{");
        for (Credential credential : credentials) {
            sb.append(credential.toString());
        }
        sb.append("}");
        return sb.toString();
    }
}
