package server.service;

import server.exception.DataAccessException;
import shared.model.AuthToken;
import shared.model.ServerPerson;
import shared.result.FamilyMembersResult;

import java.util.List;

/**
 * A <code>FamilyMembersService</code> object manages the requests to get all family members for a user
 * @author griffinbholt
 */
public final class FamilyMembersService extends PersonsOnlyService {
    /**
     * Returns ALL family members of the user associated with the specified authorization token.
     * @param authToken The authorization token of the party requesting the
     *                  {@link ServerPerson Person} objects
     * @return The result of the request (containing the requested family member
     *         {@link ServerPerson Person} objects if successful)
     */
    public FamilyMembersResult getAllFamilyMembers(AuthToken authToken) {
        try {
            super.openConnection();
            List<ServerPerson> familyMembers = this.getFamilyMembersFromDatabase(authToken);
            super.commitChanges();
            FamilyMembersResult success = FamilyMembersResult.newSuccess(familyMembers);
            super.reportSuccess(success);
            return success;
        } catch (DataAccessException e) {
            FamilyMembersResult failure = this.reportError(e);
            super.rollbackChanges();
            return failure;
        } finally {
            super.closeConnection();
        }
    }

    private List<ServerPerson> getFamilyMembersFromDatabase(AuthToken authToken) throws DataAccessException {
        return this.personDao.getAncestorsFromAuthToken(authToken);
    }

    @Override
    protected FamilyMembersResult reportError(Exception e) {
        String errorMessage = "Error: Family member persons request failed. " + e.getMessage();
        logError(errorMessage);
        return FamilyMembersResult.newFailure(errorMessage);
    }
}
