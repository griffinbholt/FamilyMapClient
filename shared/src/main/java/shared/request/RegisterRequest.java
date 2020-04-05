package shared.request;

import shared.model.Gender;

import java.util.Objects;

/**
 * A Plain Old Java Object representing a request to register a new user
 * @author griffinbholt
 */
public class RegisterRequest {
    /**
     * The username entered by the new user
     */
    private final String userName;

    /**
     * The password entered by the new user
     */
    private final String password;

    /**
     * The new user's email address
     */
    private final String email;

    /**
     * The new user's first name
     */
    private final String firstName;

    /**
     * The new user's last name
     */
    private final String lastName;

    /**
     * The new user's gender
     */
    private final String gender;

    /**
     * Creates a new <code>RegisterRequest</code> object with the instance variable values input through the parameters
     * @param userName Input username for the new user
     * @param password Input password for the new user
     * @param email Input email address for the new user
     * @param firstName Input first name for the new user
     * @param lastName Input last name for the new user
     * @param gender Input gender for the new user
     */
    public RegisterRequest(String userName, String password, String email, String firstName, String lastName,
                           String gender) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
    }

    /**
     * Generates a string representation of the register request
     * @return A string representation of the register request
     */
    @Override
    public String toString() {
        return "RegisterRequest{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender=" + gender +
                '}';
    }

    /**
     * Tests if the input <code>RegisterRequest</code> object is equal to the current instance
     * @param o Input <code>Object</code> to be tested for equality
     *          with the current <code>RegisterRequest</code> instance
     * @return true, if the two register requests are have the same username and number of generations to be generated;
     *         false, if otherwise
     */
    @SuppressWarnings("OverlyComplexBooleanExpression")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RegisterRequest)) return false;
        RegisterRequest that = (RegisterRequest) o;
        return getUserName().equals(that.getUserName()) &&
                getPassword().equals(that.getPassword()) &&
                getEmail().equals(that.getEmail()) &&
                getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getGender() == that.getGender();
    }

    /**
     * Generates the hashcode for the fill request
     * @return The hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(getUserName(), getPassword(), getEmail(), getFirstName(), getLastName(), getGender());
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

    public Gender getGender() {
        return Gender.generate(gender);
    }
}
