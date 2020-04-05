package shared.result;

import shared.model.ServerPerson;

/**
 * The result of a person request
 * Inherits from {@link Result Result}.
 * @author griffinbholt
 */
public final class PersonResult extends Result {
    private String associatedUsername;
    private String personID;
    private String firstName;
    private String lastName;
    private String gender;
    private String fatherID;
    private String motherID;
    private String spouseID;

    /**
     * Factory method that creates a new <code>PersonResult</code> object,
     * with <code>success</code> set to <code>true</code>
     * @param person The {@link ServerPerson Person} object that was successfully found
     * @return The new success <code>PersonResult</code> object
     */
    public static PersonResult newSuccess(ServerPerson person) {
        return new PersonResult(person);
    }

    private PersonResult(ServerPerson person) {
        this(person, "Successfully found person: {" + person.getPersonID() + "}.", true);
    }

    /**
     * Factory method that creates a new <code>PersonResult</code> object,
     * with <code>success</code> set to <code>false</code>
     * @param errorMessage The error message accounting for the reason behind the failed clear of the database
     * @return  The new failure <code>PersonResult</code> object
     */
    public static PersonResult newFailure(String errorMessage) {
        return new PersonResult(errorMessage);
    }

    private PersonResult(String errorMessage) {
        this(null, errorMessage, false);
    }

    private PersonResult(ServerPerson person, String message, boolean success) {
        super(message, success);

        if (null != person) {
            this.associatedUsername = person.getAssociatedUsername();
            this.personID = person.getPersonID();
            this.firstName = person.getFirstName();
            this.lastName = person.getLastName();
            this.gender = person.getGenderAbbrev();
            this.fatherID = person.getFatherID();
            this.motherID = person.getMotherID();
            this.spouseID = person.getSpouseID();
        }
    }

    // Getters
    public ServerPerson getPersonObj() {
        return (null != personID) ?
                new ServerPerson(personID, associatedUsername, firstName, lastName, gender, fatherID, motherID, spouseID) :
                null;
    }
}
