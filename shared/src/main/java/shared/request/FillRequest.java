package shared.request;

import java.util.Objects;

/**
 * A Plain Old Java Object representing a request to fill the database with genealogical data for a specific user
 * @author griffinbholt
 */
public class FillRequest {
    /**
     * The username of the user for whom the data will be generated
     */
    private final String userName;

    /**
     * The number of generations of genealogical data to be generated
     */
    private final int numGenerations;

    /**
     * Constructor to create a fill request
     * @param userName The username of the user for whom the data will be generated
     * @param numGenerations The number of generations of genealogical data to be generated
     */
    public FillRequest(String userName, int numGenerations) {
        this.userName = userName;
        this.numGenerations = numGenerations;
    }

    /**
     * Generates a string representation of the fill request
     * @return A string representation of the fill request
     */
    @Override
    public String toString() {
        return "FillRequest{" +
                "userName='" + userName + '\'' +
                ", numGenerations=" + numGenerations +
                '}';
    }

    /**
     * Tests if the input <code>FillRequest</code> object is equal to the current instance
     * @param o Input <code>Object</code> to be tested for equality with the current <code>FillRequest</code> instance
     * @return true, if the two fill requests are have the same username and number of generations to be generated;
     *         false, if otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FillRequest)) return false;
        FillRequest that = (FillRequest) o;
        return getNumGenerations() == that.getNumGenerations() &&
                getUserName().equals(that.getUserName());
    }

    /**
     * Generates the hashcode for the fill request
     * @return The hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(getUserName(), getNumGenerations());
    }

    // Getters
    public String getUserName() {
        return this.userName;
    }

    public int getNumGenerations() {
        return this.numGenerations;
    }
}
