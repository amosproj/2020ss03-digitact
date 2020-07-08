package Util.Dictionary;

import java.util.Map;
import java.util.TreeMap;

public class BasicInfoDictionary implements IDictionary {
    private Map<String, String> dictionary = new TreeMap<String, String>(); // <English, German>

    public BasicInfoDictionary() {
        init();
    }

    private void init() {
        if (dictionary.size() == 0) {
            // add all values
            dictionary.put("login", "Anmeldung");
            dictionary.put("title", "Anrede");
            dictionary.put("welcome", "Willkommen");
            dictionary.put("firstName", "Vorname");
            dictionary.put("lastName", "Nachname");
            dictionary.put("mr", "Herr");
            dictionary.put("mrs", "Frau");
        }
    }

    public Map<String, String> getDictionary() {
        return dictionary;
    }
}
