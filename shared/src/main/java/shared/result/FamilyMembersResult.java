package shared.result;

import shared.model.ServerPerson;

import java.util.Collections;
import java.util.List;

/**
 * The result of a request for ALL family members of the current user
 * @author griffinbholt
 */
public final class FamilyMembersResult extends Result {
    /**
     * An array of {@link ServerPerson Person} objects, representing ALL family members
     * of the current user
     */
    private final List<ServerPerson> data;

    /**
     * Factory method that creates a new <code>FamilyMembersResult</code> object,
     * with <code>success</code> set to <code>true</code>
     * @param persons An array of {@link ServerPerson Person} objects, representing ALL
     *                family members found for the current user
     * @return The new success <code>FamilyMembersResult</code> object
     */
    public static FamilyMembersResult newSuccess(List<ServerPerson> persons) {
        return new FamilyMembersResult(persons);
    }

    private FamilyMembersResult(List<ServerPerson> persons) {
        this(persons, "Successfully found " + persons.size() + " persons for the input authorization token.", true);
    }

    /**
     * Factory method that creates a new <code>FamilyMembersResult</code> object,
     * with <code>success</code> set to <code>false</code>
     * @param errorMessage The error message accounting for the reason behind the failed family members query
     * @return  The new failure <code>FamilyMembersResult</code> object
     */
    public static FamilyMembersResult newFailure(String errorMessage) {
        return new FamilyMembersResult(errorMessage);
    }


    private FamilyMembersResult(String errorMessage) {
        this(null, errorMessage, false);
    }

    private FamilyMembersResult(List<ServerPerson> persons, String message, boolean success) {
        super(message, success);
        this.data = persons;
    }

    // Getter
    public List<ServerPerson> getData() {
        return (null != data) ? Collections.unmodifiableList(data) : null;
    }
}
