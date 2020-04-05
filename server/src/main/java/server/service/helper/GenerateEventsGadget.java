package server.service.helper;

import server.service.helper.jsonobjects.Location;
import shared.model.*;

import java.time.Year;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * A gadget that manages the logic for generating events for a user and his/her family
 * @author griffinbholt
 */
public final class GenerateEventsGadget extends GenerateDataGadget {
    /**
     * A reasonable minimum age for a client user
     */
    public static final int MIN_USER_AGE = 13;
    /**
     * A reasonable minimum age for a person to be married
     */
    public static final int MIN_AGE_TO_MARRY = 18;
    /**
     * A reasonable minimum age for birthing a child
     */
    public static final int MIN_AGE_TO_BIRTH_CHILD = 19;
    private static final int MIN_YRS_TO_MARRY_BEFORE_CHILD = MIN_AGE_TO_BIRTH_CHILD - MIN_AGE_TO_MARRY;
    /**
     * A reasonable maximum age for birthing a child
     */
    public static final int MAX_AGE_TO_BIRTH_CHILD = 49;
    /**
     * A (not-so) reasonable maximum age for a person to die
     */
    public static final int MAX_AGE_TO_DIE = 119;

    private final List<Location> locations;

    private final int numLocations;

    private Map<String, ServerPerson> generatedAncestors;
    private Collection<ServerEvent> generatedEvents;

    GenerateEventsGadget(List<Location> locations) {
        this.locations = locations;
        this.numLocations = locations.size();
    }

    Collection<ServerEvent> generateEvents(User user, Map<String, ServerPerson> generatedAncestors) {
        ServerPerson userPerson = user.getPersonObj();
        String username = userPerson.getAssociatedUsername();
        String userPersonID = userPerson.getPersonID();

        this.generatedEvents = new ArrayList<>();
        this.generatedAncestors = generatedAncestors;

        ServerEvent userBirth = generateUserBirthEvent(username, userPersonID);
        Year userBirthYear = userBirth.getYear();

        this.generatedEvents.add(userBirth);

        recursivelyGenerateEvents(username, userPerson, userBirthYear);

        return generatedEvents;
    }

    private void recursivelyGenerateEvents(String username, ServerPerson person, Year childBirthYear) {
        String motherID = person.getMotherID();
        String fatherID = person.getFatherID();

        // Base Case - No mother/father
        if (null != motherID && null != fatherID) {
            // Recursive Case - Mother & father exist

            // Mother
            ServerPerson mother = getPerson(motherID);
            ServerEvent motherBirth = addParentBirth(username, motherID, childBirthYear);
            Year motherBirthYear = motherBirth.getYear();

            // Father
            ServerPerson father = getPerson(fatherID);
            ServerEvent fatherBirth = addParentBirth(username, fatherID, childBirthYear);
            Year fatherBirthYear = fatherBirth.getYear();

            // Mother
            ServerEvent marriage = addMarriageForMother(username, motherID, childBirthYear, motherBirthYear, fatherBirthYear);
            addParentDeath(username, motherID, childBirthYear, motherBirthYear);

            // Father
            addMarriageForFather(father, marriage);
            addParentDeath(username, fatherID, childBirthYear, fatherBirthYear);

            // Recurse Mother & Father's Parents
            recursivelyGenerateEvents(username, mother, motherBirthYear);
            recursivelyGenerateEvents(username, father, fatherBirthYear);
        }
    }


    private ServerEvent addParentBirth(String username, String parentID, Year childBirthYear) {
        ServerEvent parentBirth = generateAncestorBirthEvent(username, parentID, childBirthYear);
        generatedEvents.add(parentBirth);
        return parentBirth;
    }

    private ServerEvent addMarriageForMother(String username, String motherID, Year childBirthYear, Year motherBirthYear,
                                             Year fatherBirthYear) {
        Year mostRecentParentBirth = motherBirthYear;

        if (fatherBirthYear.getValue() > motherBirthYear.getValue()) {
            mostRecentParentBirth = fatherBirthYear;
        }

        ServerEvent marriage = generateMarriageForMother(username, motherID, childBirthYear, mostRecentParentBirth);
        generatedEvents.add(marriage);
        return marriage;
    }

    private void addMarriageForFather(ServerPerson father, ServerEvent marriage) {
        ServerEvent duplicateMarriage = generateMarriageForFather(father, marriage);
        generatedEvents.add(duplicateMarriage);
    }

    private void addParentDeath(String username, String parentID, Year childBirthYear, Year parentBirthYear) {
        ServerEvent parentDeath = generateDeathEvent(username, parentID, childBirthYear, parentBirthYear);
        generatedEvents.add(parentDeath);
    }

    private ServerPerson getPerson(String personID) {
        return generatedAncestors.get(personID);
    }

    private ServerEvent generateUserBirthEvent(String username, String personID) {
        Year userBirthYear = calculateDependentMinusYear(MIN_USER_AGE, MAX_AGE_TO_DIE, Year.now());
        return generateBirthEvent(username, personID, userBirthYear);
    }

    private ServerEvent generateAncestorBirthEvent(String username, String personID, Year childBirth) {
        Year birthYear = calculateBirthYear(childBirth);
        return generateBirthEvent(username, personID, birthYear);
    }

    private ServerEvent generateBirthEvent(String username, String personID, Year birthYear) {
        return generateEvent(username, personID, EventType.BIRTH, birthYear);
    }

    private Year calculateBirthYear(Year childBirth) {
        return calculateDependentMinusYear(MIN_AGE_TO_BIRTH_CHILD, MAX_AGE_TO_BIRTH_CHILD, childBirth);
    }

    private ServerEvent generateMarriageForMother(String username, String personID, Year childBirth, Year parentBirth) {
        Year marriageYear = calculateMarriageYear(childBirth, parentBirth);
        return generateEvent(username, personID, EventType.MARRIAGE, marriageYear);
    }

    private Year calculateMarriageYear(Year childBirth, Year parentBirth) {
        int maxAgeToMarry = childBirth.getValue() - parentBirth.getValue() - MIN_YRS_TO_MARRY_BEFORE_CHILD;
        return calculateDependentPlusYear(MIN_AGE_TO_MARRY, maxAgeToMarry, parentBirth);
    }

    private int getRandomYearIndex(int minYearBound, int maxYearBound) {
        int yearRange = maxYearBound - minYearBound;

        int randomYearIndex;
        randomYearIndex = (0 == yearRange) ? 0 : getRandomNumber(yearRange);
        return randomYearIndex;
    }

    private ServerEvent generateMarriageForFather(ServerPerson father, ServerEvent marriage) {
        String username = father.getAssociatedUsername();
        String fatherID = father.getPersonID();
        double marriageLat = marriage.getLatitude();
        double marriageLong = marriage.getLongitude();
        String country = marriage.getCountry();
        String city = marriage.getCity();
        Year year = marriage.getYear();

        return new ServerEvent(username, fatherID, marriageLat, marriageLong, country, city, EventType.MARRIAGE, year);
    }

    private ServerEvent generateDeathEvent(String username, String personID, Year childBirth, Year parentBirth) {
        Year deathYear = calculateDeathYear(childBirth, parentBirth);
        return generateEvent(username, personID, EventType.DEATH, deathYear);
    }

    private Year calculateDeathYear(Year childBirth, Year parentBirth) {
        int minAgeToDie = childBirth.getValue() - parentBirth.getValue();
        return calculateDependentPlusYear(minAgeToDie, MAX_AGE_TO_DIE, parentBirth);
    }

    private Year calculateDependentPlusYear(int minYearBound, int maxYearBound, Year baseYear) {
        int randomYearIndex = getRandomYearIndex(minYearBound, maxYearBound);
        return baseYear.plusYears(minYearBound + randomYearIndex);
    }

    private Year calculateDependentMinusYear(int minYearBound, int maxYearBound, Year baseYear) {
        int randomYearIndex = getRandomYearIndex(minYearBound, maxYearBound);
        return baseYear.minusYears(minYearBound + randomYearIndex);
    }

    private ServerEvent generateEvent(String username, String personID, EventType eventType, Year year) {
        Location location = getRandomLocation();

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        String country = location.getCountry();
        String city = location.getCity();

        return new ServerEvent(username, personID, latitude, longitude, country, city, eventType, year);
    }

    private Location getRandomLocation() {
        return (Location) getRandomElementFromList(numLocations, locations);
    }
}
