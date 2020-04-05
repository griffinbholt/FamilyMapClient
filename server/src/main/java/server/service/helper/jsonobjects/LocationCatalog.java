package server.service.helper.jsonobjects;

import java.util.List;

/**
 * A Plain Old Java Object used for storing the sample location data in the JSON file for the
 * {@link server.service.helper.FamilyDataGenerator FamilyDataGenerator}
 */
public class LocationCatalog {
    private final List<Location> locations;

    /**
     * Constructor
     * @param locations A list of sample locations
     */
    public LocationCatalog(List<Location> locations) {
        this.locations = locations;
    }

    // Getter
    public List<Location> getLocations() {
        return locations;
    }
}
