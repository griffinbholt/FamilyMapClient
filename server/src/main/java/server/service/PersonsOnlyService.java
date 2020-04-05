package server.service;

import server.dao.AuthTokenDao;
import server.dao.PersonDao;
import server.dao.UserDao;
import server.exception.DataAccessException;
import shared.result.Result;

/**
 * An abstract superclass for Service objects using a {@link server.dao.PersonDao PersonDao} object
 * to access persons in the database
 * @author griffinbholt
 */
abstract class PersonsOnlyService extends Service {
    final PersonDao personDao;

    PersonsOnlyService() {
        UserDao userDao = new UserDao();

        AuthTokenDao authTokenDao = new AuthTokenDao();

        this.personDao = new PersonDao(userDao, authTokenDao);
        userDao.setPersonDao(this.personDao);
    }

    @Override
    void openConnection() throws DataAccessException {
        super.openConnection();
        this.personDao.setConnection(dbConn);
    }

    @Override
    void closeConnection() {
        super.closeConnection();
        this.personDao.setConnection(null);
    }

    @Override
    void reportSuccess(Result result) {
        super.reportSuccess(result);
        result.setMessage(null);
    }
}
