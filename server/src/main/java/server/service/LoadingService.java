package server.service;

import server.dao.AuthTokenDao;
import server.dao.EventDao;
import server.dao.PersonDao;
import server.exception.DataAccessException;
import shared.model.ServerEvent;
import shared.model.ServerPerson;
import server.service.helper.FamilyDataLoader;

abstract class LoadingService extends UsersService {
    private final FamilyDataLoader familyDataLoader;

    LoadingService() {
        EventDao eventDao = new EventDao(this.dbConn, this.userDao, this.userDao.getPersonDao().getAuthTokenDao());
        this.familyDataLoader = new FamilyDataLoader(this.userDao.getPersonDao(), eventDao);
    }

    void loadUserPerson(String userID, ServerPerson person) throws DataAccessException {
        familyDataLoader.loadUserPerson(userID, person);
    }

    int loadPersons(Iterable<ServerPerson> persons) throws DataAccessException {
        return familyDataLoader.loadPersons(persons);
    }

    int loadEvents(Iterable<ServerEvent> events) throws DataAccessException {
        return familyDataLoader.loadEvents(events);
    }

    @Override
    void openConnection() throws DataAccessException {
        super.openConnection();
        this.familyDataLoader.setConnection(dbConn);
    }

    @Override
    void closeConnection() {
        super.closeConnection();
        this.familyDataLoader.setConnection(null);
    }

    // Getter
    EventDao getEventDao() {
        return this.familyDataLoader.getEventDao();
    }

    PersonDao getPersonDao() {
        return this.familyDataLoader.getPersonDao();
    }

    AuthTokenDao getAuthTokenDao() {
        return this.familyDataLoader.getAuthTokenDao();
    }
}
