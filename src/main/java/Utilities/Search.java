package Utilities;

import DomainModel.CredentialsManager;
import DomainModel.Domain;

import java.util.ArrayList;
import java.util.HashMap;

public class Search {
    public static void search(CredentialsManager credentialsManager, String query){
        ArrayList<Domain> domains = credentialsManager.getDomains();
        ArrayList<Domain> searchedDomains = new ArrayList<>(){};
        ArrayList<HashMap<String, String>> searchedJsonList = new ArrayList<>(){};

        int i=0;

        for(Domain domain : domains){
            if (domain.getDomain().toLowerCase().contains(query.toLowerCase())){
                searchedDomains.add(domain);
                searchedJsonList.add(credentialsManager.getJsonList().get(i));
            }
            i++;
        }

        credentialsManager.setSearched(searchedDomains, searchedJsonList);
    }
}
