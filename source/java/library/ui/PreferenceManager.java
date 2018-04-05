package library.ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class PreferenceManager {
    private final static String SEPARATOR = "|||";
    private final Map<String, String> preferences;

    public PreferenceManager(Path dataFilePath) throws IOException {
        Path preferenceFile = dataFilePath.resolve("preferences.txt");
        if (!Files.exists(preferenceFile)) {
            Files.createFile(preferenceFile);
        }
        preferences = new HashMap<>();

        Files.lines(preferenceFile).forEach((curLine) -> {
            int separatorIndex = curLine.lastIndexOf(SEPARATOR);
            int valueStartIndex = separatorIndex + SEPARATOR.length();

            String value = curLine.substring(valueStartIndex).trim();
        });
    }

    public String getValue(String key) {
        return preferences.get(key);
    }

    public void setValue(String key, int value) {
        //Convert an int primitive into a String object
        setValue(key, Integer.toString(value));
    }

    public void setValue(String key, String value) {
        preferences.put(key, value);
    }

    public int getValueAsInt(String key) {
        //Use Integer.parseInt to covert the string value into an int primitive
        return Integer.parseInt(preferences.get(key));
    }
}
