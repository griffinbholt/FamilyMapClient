package com.griffinbholt.familymapclient;

import java.util.ArrayList;
import java.util.List;

import shared.json.JsonInterpreter;
import shared.model.ServerEvent;
import shared.model.ServerPerson;
import shared.model.User;
import shared.request.LoadRequest;

abstract class SingletonTest {
    private final static String sheilaUsername = "sheila";
    private final static String patrickUsername = "patrick";

    private final static String familyDataJsonString =
            "{\n" +
                    "   \"users\":[\n" +
                    "      {\n" +
                    "         \"userName\":\"sheila\",\n" +
                    "         \"password\":\"parker\",\n" +
                    "         \"email\":\"sheila@parker.com\",\n" +
                    "         \"firstName\":\"Sheila\",\n" +
                    "         \"lastName\":\"Parker\",\n" +
                    "         \"gender\":\"f\",\n" +
                    "         \"personID\":\"Sheila_Parker\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"userName\":\"patrick\",\n" +
                    "         \"password\":\"spencer\",\n" +
                    "         \"email\":\"patrick@spencer.com\",\n" +
                    "         \"firstName\":\"Patrick\",\n" +
                    "         \"lastName\":\"Spencer\",\n" +
                    "         \"gender\":\"m\",\n" +
                    "         \"personID\":\"Patrick_Spencer\"\n" +
                    "      }\n" +
                    "   ],\n" +
                    "   \"persons\":[\n" +
                    "      {\n" +
                    "         \"firstName\":\"Sheila\",\n" +
                    "         \"lastName\":\"Parker\",\n" +
                    "         \"gender\":\"f\",\n" +
                    "         \"personID\":\"Sheila_Parker\",\n" +
                    "         \"spouseID\":\"Davis_Hyer\",\n" +
                    "         \"fatherID\":\"Blaine_McGary\",\n" +
                    "         \"motherID\":\"Betty_White\",\n" +
                    "         \"associatedUsername\":\"sheila\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"firstName\":\"Davis\",\n" +
                    "         \"lastName\":\"Hyer\",\n" +
                    "         \"gender\":\"m\",\n" +
                    "         \"personID\":\"Davis_Hyer\",\n" +
                    "         \"spouseID\":\"Sheila_Parker\",\n" +
                    "         \"associatedUsername\":\"sheila\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"firstName\":\"Blaine\",\n" +
                    "         \"lastName\":\"McGary\",\n" +
                    "         \"gender\":\"m\",\n" +
                    "         \"personID\":\"Blaine_McGary\",\n" +
                    "         \"fatherID\":\"Ken_Rodham\",\n" +
                    "         \"motherID\":\"Mrs_Rodham\",\n" +
                    "         \"spouseID\":\"Betty_White\",\n" +
                    "         \"associatedUsername\":\"sheila\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"firstName\":\"Betty\",\n" +
                    "         \"lastName\":\"White\",\n" +
                    "         \"gender\":\"f\",\n" +
                    "         \"personID\":\"Betty_White\",\n" +
                    "         \"fatherID\":\"Frank_Jones\",\n" +
                    "         \"motherID\":\"Mrs_Jones\",\n" +
                    "         \"spouseID\":\"Blaine_McGary\",\n" +
                    "         \"associatedUsername\":\"sheila\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"firstName\":\"Ken\",\n" +
                    "         \"lastName\":\"Rodham\",\n" +
                    "         \"gender\":\"m\",\n" +
                    "         \"personID\":\"Ken_Rodham\",\n" +
                    "         \"spouseID\":\"Mrs_Rodham\",\n" +
                    "         \"associatedUsername\":\"sheila\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"firstName\":\"Mrs\",\n" +
                    "         \"lastName\":\"Rodham\",\n" +
                    "         \"gender\":\"f\",\n" +
                    "         \"personID\":\"Mrs_Rodham\",\n" +
                    "         \"spouseID\":\"Ken_Rodham\",\n" +
                    "         \"associatedUsername\":\"sheila\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"firstName\":\"Frank\",\n" +
                    "         \"lastName\":\"Jones\",\n" +
                    "         \"gender\":\"m\",\n" +
                    "         \"personID\":\"Frank_Jones\",\n" +
                    "         \"spouseID\":\"Mrs_Jones\",\n" +
                    "         \"associatedUsername\":\"sheila\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"firstName\":\"Mrs\",\n" +
                    "         \"lastName\":\"Jones\",\n" +
                    "         \"gender\":\"f\",\n" +
                    "         \"personID\":\"Mrs_Jones\",\n" +
                    "         \"spouseID\":\"Frank_Jones\",\n" +
                    "         \"associatedUsername\":\"sheila\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"firstName\":\"Patrick\",\n" +
                    "         \"lastName\":\"Spencer\",\n" +
                    "         \"gender\":\"m\",\n" +
                    "         \"personID\":\"Patrick_Spencer\",\n" +
                    "         \"associatedUsername\":\"patrick\",\n" +
                    "         \"fatherID\":\"Happy_Birthday\",\n" +
                    "         \"motherID\":\"Golden_Boy\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"firstName\":\"Patrick\",\n" +
                    "         \"lastName\":\"Wilson\",\n" +
                    "         \"gender\":\"m\",\n" +
                    "         \"personID\":\"Happy_Birthday\",\n" +
                    "         \"associatedUsername\":\"patrick\",\n" +
                    "         \"spouseID\":\"Golden_Boy\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"firstName\":\"Spencer\",\n" +
                    "         \"lastName\":\"Seeger\",\n" +
                    "         \"gender\":\"f\",\n" +
                    "         \"personID\":\"Golden_Boy\",\n" +
                    "         \"associatedUsername\":\"patrick\",\n" +
                    "         \"spouseID\":\"Happy_Birthday\"\n" +
                    "      }\n" +
                    "   ],\n" +
                    "   \"events\":[\n" +
                    "      {\n" +
                    "         \"eventType\":\"birth\",\n" +
                    "         \"personID\":\"Sheila_Parker\",\n" +
                    "         \"city\":\"Melbourne\",\n" +
                    "         \"country\":\"Australia\",\n" +
                    "         \"latitude\":-36.1833,\n" +
                    "         \"longitude\":144.9667,\n" +
                    "         \"year\":1970,\n" +
                    "         \"eventID\":\"Sheila_Birth\",\n" +
                    "         \"associatedUsername\":\"sheila\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"eventType\":\"marriage\",\n" +
                    "         \"personID\":\"Sheila_Parker\",\n" +
                    "         \"city\":\"Los Angeles\",\n" +
                    "         \"country\":\"United States\",\n" +
                    "         \"latitude\":34.0500,\n" +
                    "         \"longitude\":-117.7500,\n" +
                    "         \"year\":2012,\n" +
                    "         \"eventID\":\"Sheila_Marriage\",\n" +
                    "         \"associatedUsername\":\"sheila\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"eventType\":\"completed asteroids\",\n" +
                    "         \"personID\":\"Sheila_Parker\",\n" +
                    "         \"city\":\"Qaanaaq\",\n" +
                    "         \"country\":\"Denmark\",\n" +
                    "         \"latitude\":77.4667,\n" +
                    "         \"longitude\":-68.7667,\n" +
                    "         \"year\":2014,\n" +
                    "         \"eventID\":\"Sheila_Asteroids\",\n" +
                    "         \"associatedUsername\":\"sheila\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"eventType\":\"COMPLETED ASTEROIDS\",\n" +
                    "         \"personID\":\"Sheila_Parker\",\n" +
                    "         \"city\":\"Qaanaaq\",\n" +
                    "         \"country\":\"Denmark\",\n" +
                    "         \"latitude\":74.4667,\n" +
                    "         \"longitude\":-60.7667,\n" +
                    "         \"year\":2014,\n" +
                    "         \"eventID\":\"Other_Asteroids\",\n" +
                    "         \"associatedUsername\":\"sheila\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"eventType\":\"death\",\n" +
                    "         \"personID\":\"Sheila_Parker\",\n" +
                    "         \"city\":\"Hohhot\",\n" +
                    "         \"country\":\"China\",\n" +
                    "         \"latitude\":40.2444,\n" +
                    "         \"longitude\":111.6608,\n" +
                    "         \"year\":2015,\n" +
                    "         \"eventID\":\"Sheila_Death\",\n" +
                    "         \"associatedUsername\":\"sheila\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"eventType\":\"birth\",\n" +
                    "         \"personID\":\"Davis_Hyer\",\n" +
                    "         \"city\":\"Hakodate\",\n" +
                    "         \"country\":\"Japan\",\n" +
                    "         \"latitude\":41.7667,\n" +
                    "         \"longitude\":140.7333,\n" +
                    "         \"year\":1970,\n" +
                    "         \"eventID\":\"Davis_Birth\",\n" +
                    "         \"associatedUsername\":\"sheila\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"eventType\":\"birth\",\n" +
                    "         \"personID\":\"Blaine_McGary\",\n" +
                    "         \"city\":\"Bratsk\",\n" +
                    "         \"country\":\"Russia\",\n" +
                    "         \"latitude\":56.1167,\n" +
                    "         \"longitude\":101.6000,\n" +
                    "         \"year\":1948,\n" +
                    "         \"eventID\":\"Blaine_Birth\",\n" +
                    "         \"associatedUsername\":\"sheila\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"eventType\":\"death\",\n" +
                    "         \"personID\":\"Betty_White\",\n" +
                    "         \"city\":\"Birmingham\",\n" +
                    "         \"country\":\"United Kingdom\",\n" +
                    "         \"latitude\":52.4833,\n" +
                    "         \"longitude\":-0.1000,\n" +
                    "         \"year\":2017,\n" +
                    "         \"eventID\":\"Betty_Death\",\n" +
                    "         \"associatedUsername\":\"sheila\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"eventType\":\"Graduated from BYU\",\n" +
                    "         \"personID\":\"Ken_Rodham\",\n" +
                    "         \"country\": \"United States\",\n" +
                    "         \"city\": \"Provo\",\n" +
                    "         \"latitude\": 40.2338,\n" +
                    "         \"longitude\": -111.6585,\n" +
                    "         \"year\":1879,\n" +
                    "         \"eventID\":\"BYU_graduation\",\n" +
                    "         \"associatedUsername\":\"sheila\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"eventType\":\"marriage\",\n" +
                    "         \"personID\":\"Ken_Rodham\",\n" +
                    "         \"country\": \"North Korea\",\n" +
                    "         \"city\": \"Wonsan\",\n" +
                    "         \"latitude\": 39.15,\n" +
                    "         \"longitude\": 127.45,\n" +
                    "         \"year\":1895,\n" +
                    "         \"eventID\":\"Rodham_Marriage\",\n" +
                    "         \"associatedUsername\":\"sheila\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"eventType\":\"Did a backflip\",\n" +
                    "         \"personID\":\"Mrs_Rodham\",\n" +
                    "         \"country\": \"Mexico\",\n" +
                    "         \"city\": \"Mexicali\",\n" +
                    "         \"latitude\": 32.6667,\n" +
                    "         \"longitude\": -114.5333,\n" +
                    "         \"year\":1890,\n" +
                    "         \"eventID\":\"Mrs_Rodham_Backflip\",\n" +
                    "         \"associatedUsername\":\"sheila\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"eventType\":\"learned Java\",\n" +
                    "         \"personID\":\"Mrs_Rodham\",\n" +
                    "         \"country\": \"Algeria\",\n" +
                    "         \"city\": \"Algiers\",\n" +
                    "         \"latitude\": 36.7667,\n" +
                    "         \"longitude\": 3.2167,\n" +
                    "         \"year\":1890,\n" +
                    "         \"eventID\":\"Mrs_Rodham_Java\",\n" +
                    "         \"associatedUsername\":\"sheila\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"eventType\":\"Caught a frog\",\n" +
                    "         \"personID\":\"Frank_Jones\",\n" +
                    "         \"country\": \"Bahamas\",\n" +
                    "         \"city\": \"Nassau\",\n" +
                    "         \"latitude\": 25.0667,\n" +
                    "         \"longitude\": -76.6667,\n" +
                    "         \"year\":1993,\n" +
                    "         \"eventID\":\"Jones_Frog\",\n" +
                    "         \"associatedUsername\":\"sheila\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"eventType\":\"marriage\",\n" +
                    "         \"personID\":\"Frank_Jones\",\n" +
                    "         \"country\": \"Ghana\",\n" +
                    "         \"city\": \"Tamale\",\n" +
                    "         \"latitude\": 9.4,\n" +
                    "         \"longitude\": 0.85,\n" +
                    "         \"year\":1997,\n" +
                    "         \"eventID\":\"Jones_Marriage\",\n" +
                    "         \"associatedUsername\":\"sheila\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"eventType\":\"Ate Brazilian Barbecue\",\n" +
                    "         \"personID\":\"Mrs_Jones\",\n" +
                    "         \"country\": \"Brazil\",\n" +
                    "         \"city\": \"Curitiba\",\n" +
                    "         \"latitude\": -24.5833,\n" +
                    "         \"longitude\": -48.75,\n" +
                    "         \"year\":2012,\n" +
                    "         \"eventID\":\"Mrs_Jones_Barbecue\",\n" +
                    "         \"associatedUsername\":\"sheila\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"eventType\":\"Learned to Surf\",\n" +
                    "         \"personID\":\"Mrs_Jones\",\n" +
                    "         \"country\": \"Australia\",\n" +
                    "         \"city\": \"Gold Coast\",\n" +
                    "         \"latitude\": -27.9833,\n" +
                    "         \"longitude\": 153.4,\n" +
                    "         \"year\": 2000,\n" +
                    "         \"eventID\":\"Mrs_Jones_Surf\",\n" +
                    "         \"associatedUsername\":\"sheila\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"eventType\":\"birth\",\n" +
                    "         \"personID\":\"Patrick_Spencer\",\n" +
                    "         \"city\":\"Grise Fiord\",\n" +
                    "         \"country\":\"Canada\",\n" +
                    "         \"latitude\":76.4167,\n" +
                    "         \"longitude\":-81.1,\n" +
                    "         \"year\":2016,\n" +
                    "         \"eventID\":\"Thanks_Woodfield\",\n" +
                    "         \"associatedUsername\":\"patrick\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"eventType\":\"marriage\",\n" +
                    "         \"personID\":\"Happy_Birthday\",\n" +
                    "         \"city\":\"Boise\",\n" +
                    "         \"country\":\"United States\",\n" +
                    "         \"latitude\":43.6167,\n" +
                    "         \"longitude\":-115.8,\n" +
                    "         \"year\":2016,\n" +
                    "         \"eventID\":\"True_Love\",\n" +
                    "         \"associatedUsername\":\"patrick\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"eventType\":\"marriage\",\n" +
                    "         \"personID\":\"Golden_Boy\",\n" +
                    "         \"city\":\"Boise\",\n" +
                    "         \"country\":\"United States\",\n" +
                    "         \"latitude\":43.6167,\n" +
                    "         \"longitude\":-115.8,\n" +
                    "         \"year\":2016,\n" +
                    "         \"eventID\":\"Together_Forever\",\n" +
                    "         \"associatedUsername\":\"patrick\"\n" +
                    "      }\n" +
                    "   ]\n" +
                    "}";

    private final static LoadRequest loadRequest =
            (LoadRequest) JsonInterpreter.parseJson(familyDataJsonString, LoadRequest.class);

    private final static List<User> users = loadRequest.getUsers();

    private final static List<ServerPerson> persons = loadRequest.getPersons();

    private final static List<ServerEvent> events = loadRequest.getEvents();

    static User getUserSheila() {
        return getUser(sheilaUsername);
    }

    private static User getUser(String username) {
        for (User user : users) {
            if (username.equals(user.getUserName())) {
                return user;
            }
        }

        return null;
    }

    static User getUserPatrick() {
        return getUser(patrickUsername);
    }

    static List<ServerPerson> getSheilaFamilyMembers() {
        return getAssociatedPersons(sheilaUsername);
    }

    static List<ServerPerson> getPatrickFamilyMembers() {
        return getAssociatedPersons(patrickUsername);
    }

    private static List<ServerPerson> getAssociatedPersons(String username) {
        List<ServerPerson> associatedPersons = new ArrayList<>();

        for (ServerPerson person : persons) {
            if (username.equals(person.getAssociatedUsername())) {
                associatedPersons.add(person);
            }
        }

        return associatedPersons;
    }

    static List<ServerEvent> getSheilaFamilyEvents() {
        return getAssociatedEvents();
    }

    private static List<ServerEvent> getAssociatedEvents() {
        List<ServerEvent> associatedEvents = new ArrayList<>();

        for (ServerEvent event : events) {
            if (sheilaUsername.equals(event.getAssociatedUsername())) {
                associatedEvents.add(event);
            }
        }

        return associatedEvents;
    }
}
