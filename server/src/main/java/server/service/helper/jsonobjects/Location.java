package server.service.helper.jsonobjects;

/**
 * A Plain Old Java Object used for parsing the sample location data in the JSON file for the
 * {@link server.service.helper.FamilyDataGenerator FamilyDataGenerator}
 */
public class Location {
    private final String country;
    private final String city;
    private final double latitude;
    private final double longitude;

    /**
     * Constructor
     * @param country Sample country
     * @param city Sample city
     * @param latitude Latitude of the city
     * @param longitude Longitude of the city
     */
    public Location(String country, String city, double latitude, double longitude) {
        this.country = country;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters
    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
