package shared.model;

import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

/**
 * A Plain Old Java Object to represent a person with his/her genealogical data utilized by the server
 * @author griffinbholt
 */
public class ServerPerson extends Person {
    /**
     * The username of the user, with whom the <code>Person</code> object is associated
     * (i.e., the user is either a descendant of the person or is the person himself/herself)
     */
    private final String associatedUsername;

    /**
     * The unique person ID of the person's father (if he exists in the database)
     */
    private String fatherID; // Possibly null

    /**
     * The unique person ID of the person's mother (if she exists in the database)
     */
    private String motherID; // Possibly null

    /**
     * The unique person ID of the person's spouse (if he/she exists in the database)
     */
    private String spouseID; // Possibly null

    /**
     * Creates a new <code>Person</code> object with the instance variable values input through the parameters
     * @param personID Input unique person ID
     * @param associatedUsername Input username of the user, with whom the <code>Person</code> object is associated
     * @param firstName Input first name of the person
     * @param lastName Input last name of the person
     * @param genderAbbrev Input genderAbbrev of the person ("m" for MALE; "f" for FEMALE)
     * @param fatherID Input person ID of the person's father (if it exists; null, if it doesn't)
     * @param motherID Input person ID of the person's mother (if it exists; null, if it doesn't)
     * @param spouseID Input person ID of the person's spouse (if it exists; null, if it doesn't)
     */
    public ServerPerson(String personID, String associatedUsername, String firstName, String lastName, String genderAbbrev,
                        String fatherID, String motherID, String spouseID) {
        this(personID, associatedUsername, firstName, lastName, Gender.generate(genderAbbrev),
                fatherID, motherID, spouseID);
    }

    /**
     * Creates a new <code>Person</code> object with the instance variable values input through the parameters.
     * The personID will be generated for the person, and the fatherID, motherID, and spouseID will be set to null.
     * @param associatedUsername Input username of the user, with whom the <code>Person</code> object is associated
     * @param firstName Input first name of the person
     * @param lastName Input last name of the person
     * @param gender Input gender of the person, in the form of a {@link Gender Gender} enum
     */
    public ServerPerson(String associatedUsername, String firstName, String lastName, Gender gender) {
        this(null, associatedUsername, firstName, lastName, gender, null, null, null);
    }

    /**
     * Creates a new <code>Person</code> object with the instance variable values input through the parameters
     * @param personID Input unique person ID
     * @param associatedUsername Input username of the user, with whom the <code>Person</code> object is associated
     * @param firstName Input first name of the person
     * @param lastName Input last name of the person
     * @param gender Input gender of the person, in the form of a {@link Gender Gender} enum
     * @param fatherID Input person ID of the person's father (if it exists; null, if it doesn't)
     * @param motherID Input person ID of the person's mother (if it exists; null, if it doesn't)
     * @param spouseID Input person ID of the person's spouse (if it exists; null, if it doesn't)
     */
    public ServerPerson(String personID, String associatedUsername, String firstName, String lastName, Gender gender,
                        String fatherID, String motherID, String spouseID) {
        super(firstName, lastName, gender);
        this.associatedUsername = associatedUsername;
        this.fatherID = fatherID;
        this.motherID = motherID;
        this.spouseID = spouseID;

        this.personID = (null == personID) ?
                generateUniquePersonID(associatedUsername, firstName, lastName, gender) : personID;
    }

    private String generateUniquePersonID(String username, String firstName, String lastName, Gender gender) {
        final int HASH_FACTOR = 31;
        int hash = username.hashCode() + firstName.hashCode() + lastName.hashCode() + gender.ordinal();
        hash *= LocalTime.now().toSecondOfDay();
        hash *= HASH_FACTOR;
        return (hash + "-" + UUID.randomUUID());
    }

    /**
     * Generates a string representation of the person and his/her genealogical data
     * @return The string representation of the person and his/her genealogical data
     */
    @Override
    public String toString() {
        return "Person{" +
                "associatedUsername='" + associatedUsername + '\'' +
                ", personID='" + personID + '\'' +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", gender=" + getGender() +
                ", fatherID='" + fatherID + '\'' +
                ", motherID='" + motherID + '\'' +
                ", spouseID='" + spouseID + '\'' +
                '}';
    }

    /**
     * Tests if the input <code>ServerPerson</code> object is equal to the current instance
     * @param o Input <code>Object</code> to be tested for equality with the current <code>ServerPerson</code> instance
     * @return true, if the two persons have all of the same information; false, if otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServerPerson)) return false;
        ServerPerson person = (ServerPerson) o;
        return super.equals(o) &&
                getAssociatedUsername().equals(person.getAssociatedUsername()) &&
                Objects.equals(getFatherID(), person.getFatherID()) &&
                Objects.equals(getMotherID(), person.getMotherID()) &&
                Objects.equals(getSpouseID(), person.getSpouseID());
    }

    /**
     * Generates the hashcode of the <code>Person</code> object
     * @return The hashcode
     */
    @Override
    public int hashCode() {
        return (31 * super.hashCode() + (Objects.hash(getAssociatedUsername(),
                getFatherID(), getMotherID(), getSpouseID())));
    }

    // Getters
    public String getAssociatedUsername() {
        return this.associatedUsername;
    }

    public String getFatherID() {
        return this.fatherID;
    }

    public String getMotherID() {
        return this.motherID;
    }

    public String getSpouseID() {
        return this.spouseID;
    }

    // Setters
    public void setFatherID(String fatherID) {
        this.fatherID = fatherID;
    }

    public void setMotherID(String motherID) {
        this.motherID = motherID;
    }

    public void setSpouseID(String spouseID) {
        this.spouseID = spouseID;
    }
}
