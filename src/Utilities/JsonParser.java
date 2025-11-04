package Utilities;

import DomainModel.Domain;
import DomainModel.DomainsList;

import java.util.ArrayList;
import java.util.HashMap;

public class JsonParser {
    private ArrayList<HashMap<String, String>> jsonList = new ArrayList<>();
    private DomainsList domains = new DomainsList();
    private HashMap<String, String> jsonMap = new HashMap<>();


    public JsonParser(String jsonContents) {
        readJson(jsonContents);
    }

    public void readJson(String jsonContents) {
        StringBuilder sb = new StringBuilder();

        String[] arr = jsonContents.split(";");

        int prevIndex;
        boolean key = true;
        if (jsonContents.isEmpty()){
            return;
        }
        for (String jsonContent : arr){
            System.out.println("JSON Content: " + jsonContent);
            int firstBraceIndex = jsonContent.indexOf('{');
            String domain = jsonContent.substring(0, firstBraceIndex++).trim();
            domains.add(new Domain(domain));
            prevIndex = firstBraceIndex;

            for (int i = firstBraceIndex; i < jsonContent.length()-1; i++) {
                if (jsonContent.charAt(i) == '{') {
                    String part = jsonContent.substring(prevIndex, i).trim();
                    if (key) {
                        sb = new StringBuilder();
                        sb.append(part);
                        key = false;
                    }
                    prevIndex = i + 1;
                } else if (jsonContent.charAt(i) == '}') {
                    String part = jsonContent.substring(prevIndex, i).trim();
                    jsonMap.put(sb.toString(), part);
                    prevIndex = i + 1;
                    key = true;
                }
            }
            jsonList.add(jsonMap);
            jsonMap = new HashMap<>();
            key = true;
        }

        System.out.println("JSON Contents: " + jsonList.toString());
    }

    public ArrayList<HashMap<String, String>> getJsonList() {
        return jsonList;
    }
    public DomainsList getDomains() {
        return domains;
    }
}
