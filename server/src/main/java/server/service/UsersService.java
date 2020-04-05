package server.service;

import server.dao.AuthTokenDao;
import server.dao.PersonDao;
import server.dao.UserDao;
import server.exception.DataAccessException;

abstract class UsersService extends Service {
    final UserDao userDao;

    UsersService() {
        this.userDao = new UserDao();

        AuthTokenDao authTokenDao = new AuthTokenDao();

        PersonDao personDao = new PersonDao(userDao, authTokenDao);
        this.userDao.setPersonDao(personDao);
    }

    @Override
    void openConnection() throws DataAccessException {
        super.openConnection();
        this.userDao.setConnection(dbConn);
    }

    @Override
    void closeConnection() {
        super.closeConnection();
        this.userDao.setConnection(null);
    }
}
