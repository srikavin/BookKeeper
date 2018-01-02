package library.data;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Library {
    /**
     * Used to separate different data types (patrons, books, etc.) in the data file.
     */
    private final static String dataTypeSeparator = "--------";
    private List<Patron> patrons = new ArrayList<>();
    private List<PatronType> patronTypes = new ArrayList<>();

    public Library(Path dataFilePath) throws IOException {
        //Load the data file
        Path dataFile = dataFilePath.resolve("data.txt");

        //Check if the file exists
        boolean fileExists = Files.isRegularFile(dataFile);
        if (fileExists) {
            AtomicReference<String> current = new AtomicReference<>("");
            Files.lines(dataFile).forEach((line) -> {
                if (line.startsWith(dataTypeSeparator)) {
                    current.set(line.substring(dataTypeSeparator.length()));
                    return;
                }

                String[] data = line.split(",");

                switch (current.get()) {
                    case "TYPES":
                        patronTypes.add(new PatronType(data));
                        break;
                    case "PATRONS":
                        Patron patron = new Patron(data, this);
                        patrons.add(patron);
                        break;
                }
            });
        } else {
            try {
                //If the file doesn't exist, create it
                Files.createFile(dataFile);
            } catch (IOException e) {
                //Throw an unchecked exception with the same contents as the exception
                throw new RuntimeException(e);
            }
        }
    }

    public List<Patron> getPatrons() {
        return patrons;
    }

    public List<PatronType> getPatronTypes() {
        return patronTypes;
    }

    /**
     * Resolves a {@link PatronType} from a specified identifier
     *
     * @param id The identifier to resolve
     * @return The {@linkplain PatronType} object represented by the specified identifier or null, if not found
     */
    public PatronType getPatronTypeFromId(Identifier id) {
        return patronTypes.get(id.get());
    }

    /**
     * Resolves a {@link PatronType} from a specified name
     * @param name Name of the PatronType
     * @return The {@linkplain PatronType} object represented by the specified name or null, if not found
     */
    public PatronType getPatronTypeFromName(String name){
        for(PatronType e: patronTypes){
            if(e.getName().equals(name)){
                return e;
            }
        }
        return null;
    }

    public void saveToFile(FileWriter fileWriter) throws IOException {
        fileWriter.write(dataTypeSeparator + "TYPE");
        for (Patron patron : patrons) {
            String[] data = patron.asData();
            for (String e : data) {
                fileWriter.write(e);
            }
        }
    }

}
