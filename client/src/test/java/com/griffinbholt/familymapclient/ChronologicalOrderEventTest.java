package com.griffinbholt.familymapclient;

import com.griffinbholt.familymapclient.model.data.item.ClientEvent;
import com.griffinbholt.familymapclient.model.data.item.ClientPerson;
import com.griffinbholt.familymapclient.model.data.item.SideOfFamily;

import org.junit.jupiter.api.Test;

import java.util.List;

import shared.model.ServerEvent;
import shared.model.ServerPerson;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
    Two positive tests:
    1. Standard 3-Event Person (Birth, Marriage, Death) - DONE
    2. Person with unusual events - DONE
 */
class ChronologicalOrderEventTest {
    @Test
    void testChronologicalOrderStandardEvents() {
        ClientPerson standardEventPerson = setupStandardEventPerson();

        addStandardEventsToStandardPerson(standardEventPerson);

        testChronologicalOrderOfEvents(standardEventPerson);
    }

    private ClientPerson setupStandardEventPerson() {
        ServerPerson standardEventServerPerson = new ServerPerson("Standard_Person", "standardPerson",
                "John", "Doe", "m", null, null, null);

        return new ClientPerson(SideOfFamily.IMMEDIATE, null, null, standardEventServerPerson);
    }

    private void addStandardEventsToStandardPerson(ClientPerson standardEventPerson) {
        ServerEvent[] standardServerEvents = new ServerEvent[]{
                new ServerEvent("Standard_Birth", "standardPerson", "Standard_Person",
                        100.0, 100.0, "StandardCity", "StandardCountry", "BIRTH", 1984),
                new ServerEvent("Standard_Marriage", "standardPerson", "Standard_Person",
                        100.0, 100.0, "StandardCity", "StandardCountry", "MARRIAGE", 2000),
                new ServerEvent("Standard_Death", "standardPerson", "Standard_Person",
                        100.0, 100.0, "StandardCity", "StandardCountry", "DEATH", 2020)
        };

        // NOTE: The events are purposefully out of order, in order to test the chronological ordering functionality
        ClientEvent[] standardEvents = new ClientEvent[]{
                new ClientEvent(standardEventPerson, standardServerEvents[1]),
                new ClientEvent(standardEventPerson, standardServerEvents[0]),
                new ClientEvent(standardEventPerson, standardServerEvents[2])
        };

        for (ClientEvent standardEvent : standardEvents) {
            standardEventPerson.addEvent(standardEvent);
        }
    }

    private void testChronologicalOrderOfEvents(ClientPerson person) {
        boolean isSorted = isChronologicallySorted(person.getEvents());

        assertFalse(isSorted);

        // The "ClientEvent.compareTo" function is the functionality behind sorting a person's events
        person.getEvents().sort(ClientEvent::compareTo);

        isSorted = isChronologicallySorted(person.getEvents());

        assertTrue(isSorted);
    }

    private boolean isChronologicallySorted(List<ClientEvent> clientEvents) {
        int currentYear = 0;

        for (ClientEvent clientEvent : clientEvents) {
            int clientEventYear = clientEvent.getYearAsInt();

            if (currentYear < clientEventYear) {
                currentYear = clientEventYear;
            } else {
                return false;
            }
        }

        return true;
    }

    @Test
    void testChronologicalOrderUnusualEvents() {
        ClientPerson unusualEventPerson = setupUnusualEventPerson();

        testChronologicalOrderOfEvents(unusualEventPerson);
    }

    private ClientPerson setupUnusualEventPerson() {
        ServerPerson unusualEventServerPerson = new ServerPerson("Unusual_Person", "unusualPerson",
                "Jimmy", "Doodle", "m", null, null, null);

        ClientPerson unusualEventPerson = new ClientPerson(SideOfFamily.IMMEDIATE, null, null,
                unusualEventServerPerson);

        ServerEvent[] unusualServerEvents = new ServerEvent[]{
                new ServerEvent("Standard_Birth", "unusualPerson", "Unusual_Person",
                        100.0, 100.0, "StandardCountry", "StandardCity", "BIRTH", 1984),
                new ServerEvent("Standard_Marriage", "unusualPerson", "Unusual_Person",
                        100.0, 100.0, "StandardCountry", "StandardCity", "MARRIAGE", 2000),
                new ServerEvent("Standard_Death", "unusualPerson", "Unusual_Person",
                        100.0, 100.0, "StandardCountry", "StandardCity", "DEATH", 2020),
                new ServerEvent("Unusual_Day", "unusualPerson", "Unusual_Person",
                        923.0, 126.94, "Oompa Loompa Land", "Timbuktu", "went flying", 890),
                new ServerEvent("Unusual_Night", "unusualPerson", "Unusual_Person",
                        10.0, 23.8, "England", "Hogwarts", "DEFEATED VOLDEMORT", 2001),
                new ServerEvent("Unusual_Happening", "unusualPerson", "Unusual_Person",
                        7.62638, 0.9282, "USA", "Beaver, UT", "bIrThEd A cAlF", 1786)
        };

        // NOTE: The events are purposefully out of order, in order to test the chronological ordering functionality
        ClientEvent[] unusualEvents = new ClientEvent[]{
                new ClientEvent(unusualEventPerson, unusualServerEvents[5]),
                new ClientEvent(unusualEventPerson, unusualServerEvents[4]),
                new ClientEvent(unusualEventPerson, unusualServerEvents[3]),
                new ClientEvent(unusualEventPerson, unusualServerEvents[0]),
                new ClientEvent(unusualEventPerson, unusualServerEvents[2]),
                new ClientEvent(unusualEventPerson, unusualServerEvents[1])
        };

        for (ClientEvent standardEvent : unusualEvents) {
            unusualEventPerson.addEvent(standardEvent);
        }

        return unusualEventPerson;
    }
}
