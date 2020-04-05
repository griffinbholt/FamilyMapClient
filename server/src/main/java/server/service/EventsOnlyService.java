package server.service;

import server.dao.AuthTokenDao;
import server.dao.EventDao;
import server.dao.PersonDao;
import server.dao.UserDao;
import server.exception.DataAccessException;
import shared.result.Result;

abstract class EventsOnlyService extends Service {
    @SuppressWarnings("PackageVisibleField")
    final EventDao eventDao;

    EventsOnlyService() {
        UserDao userDao = new UserDao();

        AuthTokenDao authTokenDao = new AuthTokenDao();

        PersonDao personDao = new PersonDao(userDao, authTokenDao);
        userDao.setPersonDao(personDao);

        this.eventDao = new EventDao(userDao, authTokenDao);
    }

    @Override
    void openConnection() throws DataAccessException {
        super.openConnection();
        this.eventDao.setConnection(dbConn);
    }

    @Override
    void closeConnection() {
        super.closeConnection();
        this.eventDao.setConnection(null);
    }

    @Override
    void reportSuccess(Result result) {
        super.reportSuccess(result);
        result.setMessage(null);
    }
}
