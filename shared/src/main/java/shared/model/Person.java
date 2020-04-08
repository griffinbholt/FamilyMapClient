package shared.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * An abstract superclass to represent persons
 *
 * @author griffinbholt
 */
public abstract class Person implements Serializable {
    /**
     * A unique person ID
     */
    protected String personID;

    /**
     * The first name of the person
     */
    private final String firstName;

    /**
     * The last name of the person
     */
    private final String lastName;

    /**
     * The gender of the person
     */
    private final Gender gender;

    public Person(String personID, String firstName, String lastName, Gender gender) {
        this.personID = personID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
    }


    public Person(String firstName, String lastName, Gender gender) {
        this.personID = null;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
    }

    /**
     * Tests if the input <code>Person</code> object is equal to the current instance
     *
     * @param o Input <code>Object</code> to be tested for equality with the current <code>Person</code> instance
     * @return true, if the two persons have all of the same information; false, if otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return getPersonID().equals(person.getPersonID()) &&
                getFirstName().equals(person.getFirstName()) &&
                getLastName().equals(person.getLastName()) &&
                getGender() == person.getGender();
    }

    /**
     * Generates the hashcode of the <code>Person</code> object
     *
     * @return The hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(getPersonID(), getFirstName(), getLastName(), getGender());
    }

    // Getters
    public String getPersonID() {
        return this.personID;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Gender getGender() {
        return this.gender;
    }

    /**
     * Gets the gender of the person (represented as an abbreviated string)
     * @return "m" if the person is MALE; "f" if the person is FEMALE
     */
    public String getGenderAbbrev() {
        return this.gender.toAbbreviation();
    }
}
