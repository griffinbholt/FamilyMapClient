package shared.model;

import java.util.Objects;

/**
 * A Plain Old Java Object to represent a user and his/her information utilized by the server
 * @author griffinbholt
 */
public class User {
    /**
     * The user's username
     */
    private final String userName;

    /**
     * The user's password
     */
    private final String password;

    /**
     * The user's email address
     */
    private final String email;

    /**
     * The user's first name
     */
    private final String firstName;

    /**
     * The user's last name
     */
    private final String lastName;

    /**
     * The user's gender
     */
    private final Gender gender;

    /**
     * The unique person ID of the user's {@link server.model.Person Person} object in the database
     */
    private final String personID;

    /**
     * A reference to the user's {@link server.model.Person Person} object
     */
    private final ServerPerson personObj;

    /**
     * Creates a new <code>User</code> object with the instance variable values input through the parameters
     * @param userName Input username of the user
     * @param password Input password of the user
     * @param email Input email address of the user
     * @param firstName Input first name of the user
     * @param lastName Input last name of the user
     * @param genderAbbrev Input genderAbbrev of the user ("m" for MALE; "f" for FEMALE)
     * @param personID Input ID of the user's {@link server.model.Person Person} object
     * @param personObj A reference to the user's {@link server.model.Person Person} object
     */
    public User(String userName, String password, String email, String firstName, String lastName,
                String genderAbbrev, String personID, ServerPerson personObj) {
        this(userName, password, email, firstName, lastName, Gender.generate(genderAbbrev), personID, personObj);
    }

    /**
     * Creates a new <code>User</code> object with the instance variable values input through the parameters.
     * The personID and {@link server.model.Person Person} object will be generated for the user.
     * @param userName Input username of the user
     * @param password Input password of the user
     * @param email Input email address of the user
     * @param firstName Input first name of the user
     * @param lastName Input last name of the user
     * @param gender Input gender of the user, in the form of a {@link server.model.Gender Gender} enum
     */
    public User(String userName, String password, String email, String firstName, String lastName, Gender gender)
    {
        this(userName, password, email, firstName, lastName, gender, null, null);
    }

    private User(String userName, String password, String email, String firstName, String lastName,
                 Gender gender, String personID, ServerPerson personObj) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;

        if (null == personID) {
            this.personObj = new ServerPerson(userName, firstName, lastName, gender);
            this.personID = this.personObj.getPersonID();
        } else {
            this.personID = personID;
            this.personObj = personObj;
        }
    }

    /**
     * Generates a string representation of the user and his/her information
     * @return The string representation of the user and his/her information
     */
    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender=" + gender +
                ", personID='" + personID + '\'' +
                ", personObj=" + personObj +
                '}';
    }

    /**
     * Tests if the input <code>User</code> object is equal to the current instance
     * @param o Input <code>Object</code> to be tested for equality with the current <code>User</code> instance
     * @return true, if the two users have all of the same information; false, if otherwise
     */
    @SuppressWarnings("OverlyComplexBooleanExpression")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return getUserName().equals(user.getUserName()) &&
                getPassword().equals(user.getPassword()) &&
                getEmail().equals(user.getEmail()) &&
                getFirstName().equals(user.getFirstName()) &&
                getLastName().equals(user.getLastName()) &&
                getGender() == user.getGender() &&
                getPersonID().equals(user.getPersonID()) &&
                Objects.equals(getPersonObj(), user.getPersonObj());
    }

    /**
     * Generates the hashcode of the <code>User</code> object
     * @return The hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(getUserName(), getPassword(), getEmail(), getFirstName(), getLastName(), getGender(),
                getPersonID(), getPersonObj());
    }

    // Getters
    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    /**
     * Gets the gender of the user (represented as an enumeration)
     * @return A {@link server.model.Gender Gender} enumeration
     */
    public Gender getGender() {
        return gender;
    }

    /**
     * Gets the gender of the user (represented as an abbreviated string)
     * @return "m" if the person is MALE; "f" if the person is FEMALE
     */
    public String getGenderAbbr() {
        return gender.toAbbreviation();
    }

    public String getPersonID() {
        return personID;
    }

    public ServerPerson getPersonObj() {
        return personObj;
    }
}