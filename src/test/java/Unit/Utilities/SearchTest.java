package Unit.Utilities;

import DomainModel.CredentialsManager;
import DomainModel.Domain;
import Utilities.JsonParser;
import Utilities.Search;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class SearchTest {
    private CredentialsManager credentialsManager;

    @BeforeEach
    void setUp(){
        String jsonContents = "test1{key{key}key_iv{key_iv}pass_iv{pass_iv}password{password}};test2{key{key}key_iv{key_iv}pass_iv{pass_iv}password{password}};asdf{key{key}key_iv{key_iv}pass_iv{pass_iv}password{password}}";
        JsonParser parser = new JsonParser(jsonContents);
        credentialsManager = new CredentialsManager(parser, "MASTER_KEY");
    }

    @Test
    void returnsQueriedValue(){
        String query = "test1";
        Search.search(credentialsManager, query);
        String queriedDomain = credentialsManager.getSearchedDomains().get(0).getDomain();
        assertEquals(query, queriedDomain);
    }
    @Test
    void returnsAllSubStringsOfQuery(){
        String query = "test"; // Two creds with this substring
        Search.search(credentialsManager, query);
        ArrayList<Domain> queriedDomains = credentialsManager.getSearchedDomains();

        assertEquals(2, queriedDomains.size());
        for(Domain domain : queriedDomains){
            assertTrue(domain.getDomain().contains("test"));
        }
    }

    @Test
    void notInCredsReturnsEmptyArray(){
        String query = "qwerty"; // Not in credentials
        Search.search(credentialsManager, query);
        ArrayList<Domain> queriedDomains = credentialsManager.getSearchedDomains();
        assertTrue(queriedDomains.isEmpty());
    }

    @Test
    void ignoresValuesNotAssociatedWithQuery(){
        String query = "test";
        Search.search(credentialsManager, query);
        ArrayList<Domain> queriedDomains = credentialsManager.getSearchedDomains();
        ArrayList<String> queriedDomainStrings = new ArrayList<>(){};
        for(Domain domain : queriedDomains){
            queriedDomainStrings.add(domain.getDomain());
        }
        assertFalse(queriedDomainStrings.contains("asdf"));
    }
}
