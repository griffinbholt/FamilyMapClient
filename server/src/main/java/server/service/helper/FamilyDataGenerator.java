package server.service.helper;

import com.google.gson.Gson;
import server.service.helper.jsonobjects.Location;
import server.service.helper.jsonobjects.LocationCatalog;
import server.service.helper.jsonobjects.NameCatalog;
import shared.model.ServerEvent;
import shared.model.ServerPerson;
import shared.model.User;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * A helper object that handles all of the logic for generating family and event data for a user
 * @author griffinbholt
 */
public class FamilyDataGenerator {
    /**
     * The default number of generations of ancestors and events to generate (if otherwise unspecified)
     */
    public static final int DEFAULT_NUM_GENERATIONS = 4;

    private static final String SAMPLE_DATA_DIR = "sample_data";

    private GenerateAncestorsGadget generateAncestorsGadget;
    private GenerateEventsGadget generateEventsGadget;

    /**
     * Default constructor
     */
    public FamilyDataGenerator() {
        setupGenerateGadgets();
    }

    private void setupGenerateGadgets() {
        setupAncestorsGadget();
        setupEventsGadget();
    }

    private void setupAncestorsGadget() {
        List<String> surnames = readInSurnames();
        List<String> femaleNames = readInFemaleNames();
        List<String> maleNames = readInMaleNames();

        this.generateAncestorsGadget = new GenerateAncestorsGadget(surnames, femaleNames, maleNames);
    }

    private List<String> readInSurnames() {
        final String SURNAMES_FILE = "snames.json";
        return readInNameCatalog(SURNAMES_FILE);
    }

    private List<String> readInFemaleNames() {
        final String FEMALE_NAMES_FILE = "fnames.json";
        return readInNameCatalog(FEMALE_NAMES_FILE);
    }

    private List<String> readInMaleNames() {
        final String MALE_NAMES_FILE = "mnames.json";
        return readInNameCatalog(MALE_NAMES_FILE);
    }

    private List<String> readInNameCatalog(String fileName) {
        NameCatalog nameCatalog = (NameCatalog) parseJson(fileName, NameCatalog.class);
        return nameCatalog.getNames();
    }

    private void setupEventsGadget() {
        List<Location> locations = readInLocations();
        this.generateEventsGadget = new GenerateEventsGadget(locations);
    }

    private List<Location> readInLocations() {
        final String LOCATIONS_FILE = "locations.json";

        LocationCatalog locationCatalog = (LocationCatalog) parseJson(LOCATIONS_FILE, LocationCatalog.class);

        return locationCatalog.getLocations();
    }

    private Object parseJson(String fileName, Class<?> jsonObjClass) {
        Path LOCATIONS_FILEPATH = Paths.get(SAMPLE_DATA_DIR, fileName);
        return parseJson(LOCATIONS_FILEPATH, jsonObjClass);
    }

    @SuppressWarnings("OverlyBroadCatchBlock")
    private static Object parseJson(Path filePath, Class<?> jsonClass) {
        File locationFile = filePath.toFile();

        try (FileReader fileReader = new FileReader(locationFile)) {
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            Gson gson = new Gson();
            return gson.fromJson(bufferedReader, jsonClass);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Generates ancestors for the input user for the specified number of generations
     * @param user Input {@link User}, for whom the ancestor data will be generated
     * @param numGenerations Specified number of generations of ancestors to be generated
     * @return A collection of {@link ServerPerson} objects, representing the generated ancestors
     */
    public Collection<ServerPerson> generateAncestors(User user, int numGenerations) {
        return this.generateAncestorsGadget.generateAncestors(user, numGenerations);
    }

    /**
     * Generates events for the user and all of his/her generated ancestors
     * @param user Input {@link User}, for whom the event data will be generated
     * @return A collection of {@link ServerEvent} objects, representing the generated events
     */
    public Collection<ServerEvent> generateEvents(User user) {
        Map<String, ServerPerson> generatedAncestors = this.generateAncestorsGadget.getGeneratedAncestors();
        return this.generateEventsGadget.generateEvents(user, generatedAncestors);
    }
}
