package server.service;

import java.util.Collection;

import server.exception.DataAccessException;
import server.service.helper.FamilyDataGenerator;
import shared.model.ServerEvent;
import shared.model.ServerPerson;
import shared.model.User;

abstract class FillingService extends LoadingService {
    private final FamilyDataGenerator familyDataGenerator;

    FillingService() { this.familyDataGenerator = new FamilyDataGenerator(); }

    int generateAncestors(String userID, User user, int numGenerations) throws DataAccessException {
        Collection<ServerPerson> ancestors = familyDataGenerator.generateAncestors(user, numGenerations);
        super.loadUserPerson(userID, user.getPersonObj());
        super.loadPersons(ancestors);

        return ancestors.size();
    }

    int generateEvents(User user) throws DataAccessException {
        Collection<ServerEvent> familyEvents = familyDataGenerator.generateEvents(user);
        super.loadEvents(familyEvents);

        return familyEvents.size();
    }
}
