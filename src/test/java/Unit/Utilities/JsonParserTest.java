package Unit.Utilities;

import DomainModel.Domain;
import Utilities.JsonParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class JsonParserTest {
    private JsonParser parser;

    @BeforeEach
    void setUp(){
        String jsonContents = "test{key{key}key_iv{key_iv}pass_iv{pass_iv}password{password}}";
        parser = new JsonParser(jsonContents);
    }

    @Test
    void containsDomainItems(){
        ArrayList<HashMap<String, String>> jsonList = parser.getJsonList();

        HashMap<String, String> jsonContents = jsonList.get(0);

        assertTrue(jsonContents.containsKey("key"));
        assertTrue(jsonContents.containsKey("key_iv"));
        assertTrue(jsonContents.containsKey("pass_iv"));
        assertTrue(jsonContents.containsKey("password"));

        assertEquals("key", jsonContents.get("key"));
        assertEquals("key_iv", jsonContents.get("key_iv"));
        assertEquals("pass_iv", jsonContents.get("pass_iv"));
        assertEquals("password", jsonContents.get("password"));
    }

    @Test
    void containsTestDomain(){
        Domain testDomain = parser.getDomains().get(0);
        assertEquals("test", testDomain.getDomain());
    }

}

