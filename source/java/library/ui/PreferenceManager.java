package library.ui;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains, reads, and writes preference data. Used to manage saved preferences, set the values of preferences,
 * and retrieve saved preference values.
 */
public class PreferenceManager {
    /**
     * The separator used between key and value pairs
     */
    private final static String SEPARATOR = " ||| ";
    private final Map<String, String> preferences = new HashMap<>();

    /**
     * Creates a new instance of a PreferenceManager that loads preferences from the given path
     *
     * @param dataFilePath The path to store a preferences file into
     *
     * @throws IOException If the preferences file is unable to be read or written to, an IOException will be thrown
     */
    public PreferenceManager(Path dataFilePath) throws IOException {
        //Locate the preferences.txt file located inside the data path
        Path preferenceFile = dataFilePath.resolve("preferences.txt");
        //If the file doesn't exist, create it
        if (!Files.exists(preferenceFile)) {
            Files.createFile(preferenceFile);
        }

        //Iterate over each line in the string and call parePreference for each of the lines
        Files.lines(preferenceFile).forEach(this::parsePreference);
    }

    /**
     * Creates a empty preference manager. Should be called when no existing preferences exist or a new library is
     * created.
     */
    public PreferenceManager() {
        //empty constructor; used when no existing preferences exist
    }

    /**
     * Saves the preferences file with the currently present preferences
     *
     * @param dataPath The path to save the preferences to
     *
     * @throws IOException If the file cannot be opened or created, an IOException will be thrown
     */
    public void savePreferences(Path dataPath) throws IOException {
        //Locate the preferences.txt file located inside the data path
        Path preferenceFile = dataPath.resolve("preferences.txt");

        //Create a writer for the preferences file
        BufferedWriter fileWriter = Files.newBufferedWriter(preferenceFile);
        for (Map.Entry<String, String> e : preferences.entrySet()) {
            //Write each entry in the map with the separator between the key and value; use the system line separator to maintain cross-compatibility
            fileWriter.write(e.getKey() + SEPARATOR + e.getValue() + System.lineSeparator());
        }
        fileWriter.close();
    }

    /**
     * Removes the preference from the in-memory store. To update preferences on disk, call {@link #savePreferences(Path)}.
     *
     * @param key The preference key to remove.
     */
    public void deletePreference(String key) {
        preferences.remove(key);
    }

    /**
     * Parses the given line, and adds it to the preferences Map
     *
     * @param line The line to parse, must NOT be null
     */
    private void parsePreference(String line) {
        //SeparatorIndex is the index at the start of the SEPARATOR sequence
        int separatorIndex = line.lastIndexOf(SEPARATOR);
        //valueStatIndex is the index at the start of the encoded value; it is 1 + index of separator end
        int valueStartIndex = separatorIndex + SEPARATOR.length();

        //The preference key, which is from the start of the string up to the beginning of the separator
        String key = line.substring(0, separatorIndex);
        //The preference value, which is from the end of the separator (exclusive) to the end of the string
        String value = line.substring(valueStartIndex);

        preferences.put(key, value);
    }

    /**
     * Stores the specified int value into the preferences file
     *
     * @param key          The name of the preference to retrieve
     * @param defaultValue The default value to return if the specified key does not exist
     *
     * @return A String value as stored in the preferences file
     */
    public String getValue(String key, String defaultValue) {
        return preferences.getOrDefault(key, defaultValue);
    }

    /**
     * Stores the specified int value into the preferences file
     *
     * @param key   The name of the preference
     * @param value The String value to store under the specified key
     */
    public void setValue(String key, String value) {
        preferences.put(key, value);
    }

    /**
     * Stores the specified int value into the preferences file
     *
     * @param key   The name of the preference
     * @param value The integer value to store under the specified key
     */
    public void setValue(String key, int value) {
        //Convert an int primitive into a String object
        setValue(key, Integer.toString(value));
    }

    /**
     * Stores the specified int value into the preferences file
     *
     * @param key          The name of the preference to retrieve
     * @param defaultValue The default value to return if the specified key does not exist
     *
     * @return An integer value as stored in the preferences file
     */
    public int getValueAsInt(String key, int defaultValue) {
        //Use Integer.parseInt to covert the string value into an int primitive
        if (!preferences.containsKey(key)) {
            return defaultValue;
        }
        return Integer.parseInt(preferences.get(key));
    }

    /**
     * Stores the specified boolean value into the preferences file
     *
     * @param key   The name of the preference
     * @param value The boolean value to store under the specified key
     */
    public void setValue(String key, boolean value) {
        //Convert an boolean primitive into a String object
        setValue(key, Boolean.toString(value));
    }

    /**
     * Stores the specified boolean value into the preferences file
     *
     * @param key          The name of the preference to retrieve
     * @param defaultValue The default value to return if the specified key does not exist
     *
     * @return An boolean value as stored in the preferences file
     */
    public boolean getValueAsBoolean(String key, boolean defaultValue) {
        //Use Boolean.parseBoolean to covert the string value into an boolean primitive
        if (!preferences.containsKey(key)) {
            return defaultValue;
        }
        return Boolean.parseBoolean(preferences.get(key));
    }
}
