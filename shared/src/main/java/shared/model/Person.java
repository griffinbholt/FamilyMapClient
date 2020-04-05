package shared.model;

import java.io.Serializable;

public class Person implements Serializable {
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
