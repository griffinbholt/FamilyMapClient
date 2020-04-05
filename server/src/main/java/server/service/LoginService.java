package server.service;

import server.exception.DataAccessException;
import shared.request.LoginRequest;
import shared.result.LoginResult;
import shared.result.Result;
import server.service.helper.LoginManager;

/**
 * A <code>LoginServiceTest</code> object manages the functionality to login users
 * @author griffinbholt
 */
public final class LoginService extends UsersService {
    private final LoginManager loginManager;

    /**
     * Default constructor
     */
    public LoginService() {
        this.loginManager = new LoginManager(this.userDao);
    }

    /**
     * Logs in the user
     * @param request request A {@link shared.request.LoginRequest LoginRequest} object that represents
     *                the request to login the user
     * @return The result of the login, represented as a {@link shared.result.LoginResult LoginResult} object
     */
    public LoginResult login(LoginRequest request) {
        try {
            this.openConnection();
            LoginResult success = this.attemptToLoginUser(request.getUserName(), request.getPassword());
            this.commitChanges();
            super.reportSuccess(success);
            return success;
        } catch (DataAccessException e) {
            LoginResult failure = this.reportError(e);
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

    private LoginResult attemptToLoginUser(String username, String password) throws DataAccessException {
        return loginManager.loginUserForLoginResult(username, password);
    }

    @Override
    void reportSuccess(Result result) {
        super.reportSuccess(result);
        result.setMessage(null);
    }

    @Override
    protected LoginResult reportError(Exception e) {
        String errorMessage = "Error: Login request failed. " + e.getMessage();
        logError(errorMessage);
        return LoginResult.newFailure(errorMessage);
    }

    @Override
    void closeConnection() {
        super.closeConnection();
        this.loginManager.setConnection(null);
    }
}
