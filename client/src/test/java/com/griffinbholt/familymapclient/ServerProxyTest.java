package com.griffinbholt.familymapclient;

import com.griffinbholt.familymapclient.model.connection.ServerProxy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import shared.model.AuthToken;
import shared.model.ServerEvent;
import shared.model.ServerPerson;
import shared.model.User;
import shared.request.LoginRequest;
import shared.request.RegisterRequest;
import shared.result.AllEventsResult;
import shared.result.FamilyMembersResult;
import shared.result.LoginResult;
import shared.result.RegisterResult;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

/*
4 Public Methods: 8 Tests
   1. login()
   2. register()
   3. requestFamilyMembers()
   4. requestFamilyEvents()
 */
class ServerProxyTest extends SingletonTest {
    private final ServerProxy SERVER_PROXY = ServerProxy.INSTANCE;

    private final User user = getUserSheila();

    @SuppressWarnings("FieldCanBeLocal")
    private final String TEST_SERVER_HOST = "localhost";

    @SuppressWarnings("FieldCanBeLocal")
    private final int TEST_SERVER_PORT = 8080;

    @BeforeEach
    void setUp() {
        SERVER_PROXY.setServerHost(TEST_SERVER_HOST);
        SERVER_PROXY.setServerPort(TEST_SERVER_PORT);
    }

    @Test
    void loginExistingUser() {
        loginExistingUser(user.getUserName(), user.getPassword(), user.getPersonID());
    }

    private AuthToken loginExistingUser(String username, String password, String expectedPersonID) {
        LoginRequest loginRequest = new LoginRequest(username, password);

        LoginResult loginResult = SERVER_PROXY.login(loginRequest);
        AuthToken authToken = loginResult.getAuthToken();

        assertTrue(loginResult.isSuccess());
        assertNotNull(authToken);
        assertEquals(expectedPersonID, loginResult.getPersonID());
        assertEquals(username, loginResult.getUserName());

        return authToken;
    }

    @Test
    void loginNonExistentUser() {
        LoginRequest request = new LoginRequest("nonexistentUser", "notAPassword");

        LoginResult result = SERVER_PROXY.login(request);

        assertFalse(result.isSuccess());
        assertNull(result.getAuthToken().toString());
        assertNull(result.getPersonID());
        assertNull(result.getUserName());
    }

    @Test
    void registerNewUser() {
        String randomUsername = UUID.randomUUID().toString();
        String randomPassword = UUID.randomUUID().toString();
        String firstName = "Griffin";
        String lastName = "Holt";
        String email = "myemail@gmail.com";
        String gender = "m";

        RegisterRequest registerRequest = new RegisterRequest(randomUsername, randomPassword, email,
                firstName, lastName, gender);

        RegisterResult registerResult = SERVER_PROXY.register(registerRequest);

        String personID = registerResult.getPersonID();

        assertTrue(registerResult.isSuccess());
        assertNotNull(registerResult.getAuthToken());
        assertNotNull(personID);
        assertEquals(randomUsername, registerResult.getUserName());

        loginExistingUser(randomUsername, randomPassword, personID);
    }

    @Test
    void registerPreviouslyExistentUser() {
        RegisterRequest registerRequest = new RegisterRequest(user.getUserName(), user.getPassword(), user.getEmail(),
                user.getFirstName(), user.getLastName(),
                user.getGenderAbbr());

        RegisterResult registerResult = SERVER_PROXY.register(registerRequest);

        assertFalse(registerResult.isSuccess());
        assertNull(registerResult.getAuthToken().toString());
        assertNull(registerResult.getPersonID());
        assertNull(registerResult.getUserName());
    }

    @Test
    void requestFamilyMembersWithValidAuthToken() {
        AuthToken authToken = loginExistingUser(user.getUserName(), user.getPassword(), user.getPersonID());

        FamilyMembersResult result = SERVER_PROXY.requestFamilyMembers(authToken);

        assertTrue(result.isSuccess());

        List<ServerPerson> expectedFamilyMembers = getSheilaFamilyMembers();
        List<ServerPerson> actualFamilyMembers = result.getData();

        assertNotNull(actualFamilyMembers);
        assertEquals(expectedFamilyMembers.size(), actualFamilyMembers.size());

        assertContainsSameMembers(expectedFamilyMembers, actualFamilyMembers);
        assertContainsSameMembers(actualFamilyMembers, expectedFamilyMembers);
    }

    void assertContainsSameMembers(List<ServerPerson> firstList, List<ServerPerson> secondList) {
        for (ServerPerson person : firstList) {
            assertTrue(secondList.contains(person));
        }
    }

    @Test
    void requestFamilyMembersWithInvalidAuthToken() {
        AuthToken invalidAuthToken = new AuthToken(UUID.randomUUID().toString());

        FamilyMembersResult result = SERVER_PROXY.requestFamilyMembers(invalidAuthToken);

        assertFalse(result.isSuccess());
        assertNull(result.getData());
    }

    @Test
    void requestFamilyEventsWithValidAuthToken() {
        AuthToken authToken = loginExistingUser(user.getUserName(), user.getPassword(), user.getPersonID());

        AllEventsResult result = SERVER_PROXY.requestFamilyEvents(authToken);

        assertTrue(result.isSuccess());

        List<ServerEvent> expectedFamilyEvents = getSheilaFamilyEvents();
        List<ServerEvent> actualFamilyEvents = result.getData();

        assertNotNull(actualFamilyEvents);
        assertEquals(expectedFamilyEvents.size(), actualFamilyEvents.size());

        assertContainsSameEvents(expectedFamilyEvents, actualFamilyEvents);
        assertContainsSameEvents(actualFamilyEvents, expectedFamilyEvents);
    }

    private void assertContainsSameEvents(List<ServerEvent> firstList, List<ServerEvent> secondList) {
        for (ServerEvent event : firstList) {
            assertTrue(secondList.contains(event));
        }
    }

    @Test
    void requestFamilyEventsWithInvalidAuthToken() {
        AuthToken invalidAuthToken = new AuthToken(UUID.randomUUID().toString());

        AllEventsResult result = SERVER_PROXY.requestFamilyEvents(invalidAuthToken);

        assertFalse(result.isSuccess());
        assertNull(result.getData());
    }
}
