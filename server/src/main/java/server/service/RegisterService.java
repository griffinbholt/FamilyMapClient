package server.service;

import server.exception.DataAccessException;
import server.exception.UserAlreadyExistsException;
import shared.model.*;
import shared.request.RegisterRequest;
import shared.result.RegisterResult;
import shared.result.Result;
import server.service.helper.FamilyDataGenerator;
import server.service.helper.LoginManager;

/**
 * A <code>RegisterService</code> object manages the functionality to register new users
 * @author griffinbholt
 */
public final class RegisterService extends FillingService {
    private final LoginManager loginManager;

    /**
     * Default constructor
     */
    public RegisterService() {
        this.loginManager = new LoginManager(this.userDao);
    }

    /**
     * Creates a new user account, generates 4 generations of ancestor data for the new user, and logs the user in.
     * @param request A {@link shared.request.RegisterRequest RegisterRequest} object that represents
     *                the request to register the new user
     * @return The result of the register, represented as a
     *         {@link shared.result.RegisterResult RegisterResult} object
     */
    public RegisterResult register(RegisterRequest request) {
        try {
            this.openConnection();
            RegisterResult success = process(request);
            super.commitChanges();
            super.reportSuccess(success);
            return success;
        } catch (DataAccessException | UserAlreadyExistsException e) {
            RegisterResult failure = this.reportError(e);
            super.rollbackChanges();
            return failure;
        } finally {
            this.closeConnection();
        }
    }

    @Override
    void openConnection() throws DataAccessException {
        super.openConnection();
        this.loginManager.setConnection(dbConn);
    }

    private RegisterResult process(RegisterRequest request) throws DataAccessException, UserAlreadyExistsException {
        checkIfUsernameAlreadyExists(request.getUserName());

        UserDBWrapper newUser = addNewUserToDatabase(request);
        User userObj = newUser.getUser();

        generateFamilyData(newUser.getUserID(), userObj);

        AuthToken authToken = loginNewUser(newUser.getUser());

        return RegisterResult.newSuccess(authToken, userObj.getUserName(), userObj.getPersonID());
    }

    private void checkIfUsernameAlreadyExists(String username) throws UserAlreadyExistsException,
            DataAccessException {
        String userID = this.userDao.getUserIDFromUsername(username);

        if (null != userID) {
            throw new UserAlreadyExistsException();
        }
    }

    private UserDBWrapper addNewUserToDatabase(RegisterRequest request) throws DataAccessException {
        User newUser = createNewUserAccount(request);
        String userID = this.userDao.add(newUser);
        return new UserDBWrapper(userID, newUser);
    }

    private User createNewUserAccount(RegisterRequest request) {
        String username = request.getUserName();
        String password = request.getPassword();
        String emailAddress = request.getEmail();
        String firstName = request.getFirstName();
        String lastName = request.getLastName();
        Gender gender = request.getGender();

        return new User(username, password, emailAddress, firstName, lastName, gender);
    }

    private void generateFamilyData(String userID, User newUser) throws DataAccessException {
        generateAncestors(userID, newUser);
        super.generateEvents(newUser);
    }

    private void generateAncestors(String userID, User newUser) throws DataAccessException {
        super.generateAncestors(userID, newUser, FamilyDataGenerator.DEFAULT_NUM_GENERATIONS);
    }

    private AuthToken loginNewUser(User newUser) throws DataAccessException {
        return loginManager.loginUserForAuthToken(newUser.getUserName(), newUser.getPassword());
    }

    @Override
    void reportSuccess(Result result) {
        super.reportSuccess(result);
        result.setMessage(null);
    }

    @Override
    protected RegisterResult reportError(Exception e) {
        String errorMessage = "Error: Register request failed. " + e.getMessage();
        logError(errorMessage);
        return RegisterResult.newFailure(errorMessage);
    }

    @Override
    void closeConnection() {
        super.closeConnection();
        this.loginManager.setConnection(null);
    }
}
