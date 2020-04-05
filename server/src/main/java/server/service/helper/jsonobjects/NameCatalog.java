package server.service.helper.jsonobjects;

import java.util.List;

/**
 * A Plain Old Java Object used for storing the sample name data in the JSON file for the
 * {@link server.service.helper.FamilyDataGenerator FamilyDataGenerator}
 */
public class NameCatalog {
    private final List<String> names;

    /**
     * Constructor
     * @param names A list of sample names
     */
    public NameCatalog(List<String> names) {
        this.names = names;
    }

    // Getter
    public List<String> getNames() {
        return names;
    }
}
