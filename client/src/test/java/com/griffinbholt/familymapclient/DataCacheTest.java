package com.griffinbholt.familymapclient;

import com.griffinbholt.familymapclient.model.Settings;
import com.griffinbholt.familymapclient.model.data.DataCache;
import com.griffinbholt.familymapclient.model.data.item.ClientEvent;
import com.griffinbholt.familymapclient.model.data.item.ClientPerson;
import com.griffinbholt.familymapclient.model.data.item.SideOfFamily;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import shared.model.Gender;
import shared.model.ServerEvent;
import shared.model.ServerPerson;
import shared.model.User;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertSame;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

/*
7 Public Methods: 14 Tests
    1. loadFamilyMembers() - DONE
    2. loadFamilyEvents() - DONE
    3. searchEnabledPeople() - DONE
    4. searchEnabledEvents() - DONE
    5. clear() - DONE
    6. enabledPersons() - DONE
    7. enabledEvents() - DONE
 */
class DataCacheTest extends SingletonTest {
    private final DataCache DATA_CACHE = DataCache.INSTANCE;
    private final Settings SETTINGS = Settings.INSTANCE;

    private final User user = getUserSheila();
    private final String personID = user.getPersonID();
    private final List<ServerPerson> serverPersons = getSheilaFamilyMembers();
    private final List<ServerEvent> serverEvents = getSheilaFamilyEvents();

    @BeforeEach
    void setUp() {
        assertTrue(dataCacheIsEmpty());
        DATA_CACHE.setPersonID(personID);
        setAllFilters(true);
    }

    private void setAllFilters(boolean setting) {
        SETTINGS.setMotherSideFilter(setting);
        SETTINGS.setFatherSideFilter(setting);
        SETTINGS.setFemaleEventFilter(setting);
        SETTINGS.setMaleEventFilter(setting);
    }

    private boolean dataCacheIsEmpty() {
        return ((DATA_CACHE.getUser() == null) &&
                (DATA_CACHE.getPersonID() == null) &&
                (DATA_CACHE.firstName() == null) &&
                (DATA_CACHE.lastName() == null) &&
                DATA_CACHE.enabledPersons().isEmpty() &&
                DATA_CACHE.enabledEvents().isEmpty());
    }

    @AfterEach
    void tearDown() {
        DATA_CACHE.clear();
    }

    @Test
    void loadManyFamilyMembersTest() {
        DATA_CACHE.loadFamilyMembers(serverPersons);

        ClientPerson userPerson = DATA_CACHE.getUser();

        assertPersonEqualToUser(user, userPerson);

        assertDataCacheNamesCorrect(user);

        assertDataCacheContainsEqualNumberOfPersons(serverPersons);

        recursivelyCheckFamilyRelationships(DATA_CACHE.getPersonID(), userPerson);
    }

    private void assertPersonEqualToUser(User user, ClientPerson userPerson) {
        assertNotNull(userPerson);
        assertEquals(user.getFirstName(), user.getFirstName());
        assertEquals(user.getLastName(), user.getLastName());
        assertEquals(user.getGender(), user.getGender());
    }

    private void assertDataCacheNamesCorrect(User user) {
        assertEquals(user.getFirstName(), DATA_CACHE.firstName());
        assertEquals(user.getLastName(), DATA_CACHE.lastName());
    }

    private void assertDataCacheContainsEqualNumberOfPersons(List<ServerPerson> serverPersons) {
        assertEquals(serverPersons.size(), DATA_CACHE.enabledPersons().size());
    }

    private void recursivelyCheckFamilyRelationships(String personID, ClientPerson clientPerson) {
        // Base Case #1 - The personID doesn't exist & the client person doesn't exist
        if (personID == null && clientPerson == null) {
            return;
        }

        // Base Case #2 - One of them exists & the other doesn't
        assertFalse(personID == null || clientPerson == null);

        // Recursive Case - Both exists -> Test if equal
        ServerPerson serverPerson = getServerPerson(personID);

        assertNotNull(serverPerson);
        assertPeopleAreEqual(serverPerson, clientPerson);

        assertSameMother(serverPerson, clientPerson);
        assertSameFather(serverPerson, clientPerson);
        assertSameSpouse(serverPerson, clientPerson);
    }

    private void assertPeopleAreEqual(ServerPerson serverPerson, ClientPerson clientPerson) {
        assertEquals(serverPerson.getPersonID(), clientPerson.getPersonID());
        assertEquals(serverPerson.getFirstName(), clientPerson.getFirstName());
        assertEquals(serverPerson.getLastName(), clientPerson.getLastName());
        assertEquals(serverPerson.getGender(), clientPerson.getGender());
    }

    private void assertSameMother(ServerPerson serverPerson, ClientPerson clientPerson) {
        String motherID = serverPerson.getMotherID();
        ClientPerson clientMother = clientPerson.getMother();
        recursivelyCheckFamilyRelationships(motherID, clientMother);

        if (clientMother != null) {
            assertPersonIsChild(clientPerson, clientMother);
        }
    }

    private void assertPersonIsChild(ClientPerson person, ClientPerson parent) {
        assertTrue(parent.getChildren().contains(person));
    }

    private void assertSameFather(ServerPerson serverPerson, ClientPerson clientPerson) {
        String fatherID = serverPerson.getFatherID();
        ClientPerson clientFather = clientPerson.getFather();
        recursivelyCheckFamilyRelationships(fatherID, clientFather);

        if (clientFather != null) {
            assertPersonIsChild(clientPerson, clientFather);
        }
    }

    private void assertSameSpouse(ServerPerson serverPerson, ClientPerson clientPerson) {
        String spouseID = serverPerson.getSpouseID();

        ServerPerson serverSpouse = getServerPerson(spouseID);
        ClientPerson clientSpouse = clientPerson.getSpouse();

        if (serverSpouse == null && clientSpouse == null) {
            return;
        }

        assertFalse(serverSpouse == null || clientSpouse == null);

        assertPeopleAreEqual(serverSpouse, clientSpouse);
    }

    private ServerPerson getServerPerson(String personID) {
        return getServerPersonFromList(personID, serverPersons);
    }

    private ServerPerson getServerPersonFromList(String personID, List<ServerPerson> serverPersonList) {
        for (ServerPerson person : serverPersonList) {
            if (personID.equals(person.getPersonID())) {
                return person;
            }
        }

        return null;
    }

    @Test
    void loadOnlyUserPersonTest() {
        User patrickUser = getUserPatrick();
        String patrickID = patrickUser.getPersonID();

        DATA_CACHE.setPersonID(patrickID);

        ServerPerson serverPatrick = getServerPersonFromList(patrickID, getPatrickFamilyMembers());
        assertNotNull(serverPatrick);

        List<ServerPerson> justPatrick = new ArrayList<>();
        justPatrick.add(serverPatrick);

        DATA_CACHE.loadFamilyMembers(justPatrick);

        ClientPerson clientPatrick = DATA_CACHE.getUser();
        assertNotNull(clientPatrick);

        assertPersonEqualToUser(patrickUser, clientPatrick);

        assertDataCacheNamesCorrect(patrickUser);

        assertDataCacheContainsEqualNumberOfPersons(justPatrick);

        assertPeopleAreEqual(serverPatrick, clientPatrick);

        assertNotNull(serverPatrick.getMotherID());
        assertNull(clientPatrick.getMother());

        assertNotNull(serverPatrick.getFatherID());
        assertNull(clientPatrick.getFather());
    }

    @Test
    void loadManyFamilyEventsTest() {
        DATA_CACHE.loadFamilyMembers(serverPersons);
        DATA_CACHE.loadFamilyEvents(serverEvents);

        List<ClientEvent> clientEvents = DATA_CACHE.enabledEvents();

        assertClientEventsEqualServerEvents(clientEvents, serverEvents);

        assertEventsAttachedCorrectlyToClientPersons();
    }

    private void assertClientEventsEqualServerEvents(List<ClientEvent> clientEvents, List<ServerEvent> serverEvents) {
        assertEquals(clientEvents.size(), serverEvents.size());

        for (ServerEvent serverEvent : serverEvents) {
            assertTrue(foundClientEvent(serverEvent, clientEvents));
        }
    }

    private boolean foundClientEvent(ServerEvent serverEvent, List<ClientEvent> clientEvents) {
        boolean found;

        for (ClientEvent clientEvent : clientEvents) {
            found = areEqual(serverEvent, clientEvent);

            if (found) {
                return true;
            }
        }

        return false;
    }

    private boolean areEqual(ServerEvent serverEvent, ClientEvent clientEvent) {
        return (serverEvent.getPersonID().equals(Objects.requireNonNull(clientEvent.getPerson()).getPersonID()))
                && (serverEvent.getYear().equals(clientEvent.getYear()))
                && (serverEvent.getEventType().equals(clientEvent.getEventType()))
                && (serverEvent.getCity().equals(clientEvent.getCity()))
                && (serverEvent.getCountry().equals(clientEvent.getCountry()))
                && (0 == Double.compare(serverEvent.getLatitude(), clientEvent.getLatitude()))
                && (0 == Double.compare(serverEvent.getLongitude(), clientEvent.getLongitude()));
    }

    private void assertEventsAttachedCorrectlyToClientPersons() {
        ClientPerson clientUser = DATA_CACHE.getUser();
        assertNotNull(clientUser);
        recursivelyCheckEventsRelatedToClientPersons(clientUser);
    }

    private void recursivelyCheckEventsRelatedToClientPersons(ClientPerson person) {
        // Base Case #1 - Person doesn't exist
        if (person == null) {
            return;
        }

        // Recursive Case - Person does exist
        checkEventsRelatedToPerson(person);

        recursivelyCheckEventsRelatedToClientPersons(person.getFather());
        recursivelyCheckEventsRelatedToClientPersons(person.getMother());

        checkEventsRelatedToSpouse(person);
    }

    private void checkEventsRelatedToPerson(ClientPerson person) {
        String personID = person.getPersonID();

        List<ServerEvent> relatedServerEvents = getRelatedServerEvents(personID);
        assertClientEventsEqualServerEvents(person.getEvents(), relatedServerEvents);
    }

    private void checkEventsRelatedToSpouse(ClientPerson person) {
        ClientPerson clientSpouse = person.getSpouse();

        if (clientSpouse != null) {
            checkEventsRelatedToPerson(clientSpouse);
        }
    }

    private List<ServerEvent> getRelatedServerEvents(String personID) {
        List<ServerEvent> relatedServerEvents = new ArrayList<>();

        for (ServerEvent serverEvent : serverEvents) {
            if (personID.equals(serverEvent.getPersonID())) {
                relatedServerEvents.add(serverEvent);
            }
        }

        return relatedServerEvents;
    }

    @Test
    void loadEmptyFamilyEventsTest() {
        DATA_CACHE.loadFamilyMembers(serverPersons);
        DATA_CACHE.loadFamilyEvents(new ArrayList<>());

        List<ClientEvent> clientEvents = DATA_CACHE.enabledEvents();
        assertTrue(clientEvents.isEmpty());

        recursivelyCheckNoEventsAttachedToClientPersons(DATA_CACHE.getUser());
    }

    private void recursivelyCheckNoEventsAttachedToClientPersons(ClientPerson person) {
        // Base Case #1 - The person does not exist
        if (person == null) {
            return;
        }

        // Recursive Case - Person does exist
        assertTrue(person.getEvents().isEmpty());

        recursivelyCheckNoEventsAttachedToClientPersons(person.getMother());
        recursivelyCheckNoEventsAttachedToClientPersons(person.getFather());

        ClientPerson spouse = person.getSpouse();
        if (spouse != null) {
            assertTrue(spouse.getEvents().isEmpty());
        }
    }

    @Test
    void searchEnabledPeoplePass() {
        loadDataCache();

        String[] searchQueries = new String[]{"sHeIlA", "s", "jo", "RODHAM"};
        int[] expectedNumPersonResults = new int[]{1, 5, 2, 2};

        int numQueries = searchQueries.length;

        for (int i = 0; i < numQueries; i++) {
            String searchQuery = searchQueries[i];

            DATA_CACHE.setTextQuery(searchQuery);
            List<ClientPerson> foundPeople = DATA_CACHE.searchEnabledPeople();
            assertEquals(searchQuery, expectedNumPersonResults[i], foundPeople.size());
        }
    }

    private void loadDataCache() {
        DATA_CACHE.loadFamilyMembers(serverPersons);
        DATA_CACHE.loadFamilyEvents(serverEvents);
        assertFalse(dataCacheIsEmpty());
    }

    @Test
    void searchEnabledPeopleFail() {
        loadDataCache();

        String[] searchQueries = new String[]{"ast", "COMPLETED", "Hello there!", "0948"};

        for (String searchQuery : searchQueries) {
            DATA_CACHE.setTextQuery(searchQuery);
            List<ClientPerson> foundPeople = DATA_CACHE.searchEnabledPeople();
            assertEquals(searchQuery, 0, foundPeople.size());
        }
    }

    @Test
    void searchEnabledEventsPass() {
        loadDataCache();

        String[] searchQueries = new String[]{"ast", "2014", "aUsTrAlIa", "s"};
        int[] expectedNumEventResults = new int[]{3, 2, 2, 10};

        int numQueries = searchQueries.length;

        for (int i = 0; i < numQueries; i++) {
            String searchQuery = searchQueries[i];

            DATA_CACHE.setTextQuery(searchQuery);
            List<ClientEvent> foundEvents = DATA_CACHE.searchEnabledEvents();
            assertEquals(searchQuery, expectedNumEventResults[i], foundEvents.size());
        }
    }

    @Test
    void searchEnabledEventsFail() {
        loadDataCache();

        String[] searchQueries = new String[]{"sHeIlA", "jo", "RODHAM", "1092"};

        for (String searchQuery : searchQueries) {
            DATA_CACHE.setTextQuery(searchQuery);
            List<ClientEvent> foundEvents = DATA_CACHE.searchEnabledEvents();
            assertEquals(searchQuery, 0, foundEvents.size());
        }
    }

    @Test
    void clearFullDataCache() {
        loadDataCache();

        DATA_CACHE.clear();

        assertTrue(dataCacheIsEmpty());
    }

    @Test
    void clearEmptyDataCache() {
        DATA_CACHE.clear();

        assertTrue(dataCacheIsEmpty());
    }

    @Test
    void getEnabledPeopleForEachFilter() {
        loadDataCache();

        ClientPerson user = DATA_CACHE.getUser();
        assertNotNull(user);

        setAllFilters(false);
        SETTINGS.setMotherSideFilter(true);
        assertSideOfFamilyFilterWorkedForPeople(user, SideOfFamily.MOTHER);

        setAllFilters(false);
        SETTINGS.setFatherSideFilter(true);
        assertSideOfFamilyFilterWorkedForPeople(user, SideOfFamily.FATHER);
    }

    private void assertSideOfFamilyFilterWorkedForPeople(ClientPerson user, SideOfFamily sideOfFamily) {
        List<ClientPerson> enabledPeople = DATA_CACHE.enabledPersons();

        for (ClientPerson clientPerson : enabledPeople) {
            if (clientPerson.getSideOfFamily() != SideOfFamily.IMMEDIATE) {
                assertSame(clientPerson.fullName(), clientPerson.getSideOfFamily(), sideOfFamily);
            }
        }

        ClientPerson parent = getParent(user, sideOfFamily);

        recursivelyCheckSideOfFamily(parent, enabledPeople);

        assertTrue(enabledPeople.contains(user));
        assertTrue(enabledPeople.contains(user.getSpouse()));
    }

    private ClientPerson getParent(ClientPerson user, SideOfFamily sideOfFamily) {
        switch (sideOfFamily) {
            case FATHER:
                return user.getFather();
            case MOTHER:
                return user.getMother();
            default:
                fail("Test case is written wrong: IMMEDIATE should never be passed to this function.");
                return null;
        }
    }

    private void recursivelyCheckSideOfFamily(ClientPerson person, List<ClientPerson> enabledPeople) {
        // Base Case #1 - Person doesn't exist
        if (person == null) {
            return;
        }

        // Recursive Case - Person does exist
        assertTrue(enabledPeople.contains(person));

        recursivelyCheckSideOfFamily(person.getFather(), enabledPeople);
        recursivelyCheckSideOfFamily(person.getMother(), enabledPeople);
    }

    @Test
    void getEnabledPeopleAllFiltersOff() {
        loadDataCache();

        setAllFilters(false);

        List<ClientPerson> enabledPeople = DATA_CACHE.enabledPersons();

        assertEquals(2, enabledPeople.size());
        assertEquals(DATA_CACHE.getUser(), enabledPeople.get(0));
        assertEquals(Objects.requireNonNull(DATA_CACHE.getUser()).getSpouse(), enabledPeople.get(1));
    }

    @Test
    void getEnabledEventsForEachFilter() {
        loadDataCache();

        ClientPerson user = DATA_CACHE.getUser();
        assertNotNull(user);

        checkSideOfFamilyFilterForEvents(user, SideOfFamily.MOTHER);
        checkSideOfFamilyFilterForEvents(user, SideOfFamily.FATHER);
        checkGenderFilter(user, Gender.FEMALE);
        checkGenderFilter(user, Gender.MALE);
    }

    private void checkSideOfFamilyFilterForEvents(ClientPerson user, SideOfFamily sideOfFamily) {
        setAllFilters(false);
        SETTINGS.setMaleEventFilter(true);
        SETTINGS.setFemaleEventFilter(true);

        ClientPerson parent;
        ClientPerson otherParent;

        if (sideOfFamily == SideOfFamily.MOTHER) {
            SETTINGS.setMotherSideFilter(true);
            parent = user.getMother();
            otherParent = user.getFather();
        } else {
            SETTINGS.setFatherSideFilter(true);
            parent = user.getFather();
            otherParent = user.getMother();
        }

        List<ClientEvent> enabledEvents = DATA_CACHE.enabledEvents();

        recursivelyAssertEventsForSideOfFamily(parent, enabledEvents, true);
        recursivelyAssertEventsForSideOfFamily(otherParent, enabledEvents, false);
    }

    private void recursivelyAssertEventsForSideOfFamily(ClientPerson person, List<ClientEvent> enabledEvents,
                                                        boolean contains) {
        // Base Case #1 - Person doesn't exist
        if (person == null) {
            return;
        }

        // Recursive Case - Person does exist
        List<ClientEvent> personEvents = person.getEvents();

        for (ClientEvent event : personEvents) {
            if (contains) {
                assertTrue(event.description(), enabledEvents.contains(event));
            } else {
                assertFalse(enabledEvents.contains(event), event.description());
            }
        }

        recursivelyAssertEventsForSideOfFamily(person.getFather(), enabledEvents, contains);
        recursivelyAssertEventsForSideOfFamily(person.getMother(), enabledEvents, contains);
    }

    private void checkGenderFilter(ClientPerson user, Gender gender) {
        setAllFilters(true);

        if (gender == Gender.FEMALE) {
            SETTINGS.setMaleEventFilter(false);
        } else {
            SETTINGS.setFemaleEventFilter(false);
        }

        List<ClientEvent> enabledEvents = DATA_CACHE.enabledEvents();

        for (ClientEvent event : enabledEvents) {
            assertSame(event.description(), gender, Objects.requireNonNull(event.getPerson()).getGender());
        }

        assertPersonEventsEnabled(user, gender, enabledEvents);
        //noinspection ConstantConditions
        assertPersonEventsEnabled(user.getSpouse(), gender, enabledEvents);

        recursivelyAssertEventsForGender(user, gender, enabledEvents);
    }

    private void assertPersonEventsEnabled(ClientPerson person, Gender gender, List<ClientEvent> enabledEvents) {
        if (person.getGender() == gender) {
            List<ClientEvent> personEvents = person.getEvents();

            for (ClientEvent event : personEvents) {
                assertTrue(enabledEvents.contains(event));
            }
        }
    }

    private void recursivelyAssertEventsForGender(ClientPerson person, Gender gender, List<ClientEvent> enabledEvents) {
        // Base Case #1 - Person doesn't exist
        if (person == null) {
            return;
        }

        assertPersonEventsEnabled(person, gender, enabledEvents);

        recursivelyAssertEventsForGender(person.getMother(), gender, enabledEvents);
        recursivelyAssertEventsForGender(person.getFather(), gender, enabledEvents);
    }

    @Test
    void getEnabledEventsAllFiltersOff() {
        loadDataCache();

        setAllFilters(false);

        List<ClientEvent> enabledEvents = DATA_CACHE.enabledEvents();

        assertTrue(enabledEvents.isEmpty());
    }
}
