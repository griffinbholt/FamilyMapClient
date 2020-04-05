package server.handler;

import com.sun.net.httpserver.HttpExchange;
import server.exception.HttpBadRequestException;
import server.service.FamilyMembersService;
import server.service.SinglePersonService;
import shared.model.AuthToken;
import shared.model.ServerPerson;
import shared.result.FamilyMembersResult;
import shared.result.PersonResult;
import shared.result.Result;

import java.io.IOException;
import java.util.Arrays;

/**
 * A handler to process {@link ServerPerson Person} requests
 * @author griffinbholt
 */
public final class PersonRequestHandler extends GetHandler {
    private static final String MODEL_NAME = "Person";

    private final SinglePersonService personService = new SinglePersonService();
    private final FamilyMembersService familyMembersService = new FamilyMembersService();

    /**
     * The method called by the server when an incoming {@link com.sun.net.httpserver.HttpExchange HttpExchange} for a
     * person request is sent to the server
     * @param exchange The incoming {@link com.sun.net.httpserver.HttpExchange HttpExchange}
     * @throws IOException An error occurred while interacting with the
     *                     {@link com.sun.net.httpserver.HttpExchange HttpExchange}
     */
    @Override
    @SuppressWarnings("OverlyBroadCatchBlock")
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String[] components = getRequestURIPathComponents(exchange);

            int numComponents = components.length;

            checkUriPath(numComponents, Arrays.toString(components));
            checkRequestMethod(exchange);

            AuthToken authToken = getAuthToken(exchange);

            Result result;

            if (isSingleObjectRequest(numComponents)) {
                String personID = getID(components);
                result = processPersonRequest(personID, authToken);
            } else {
                result = processFamilyMembersRequest(authToken);
            }

            sendResponse(exchange, result);
        } catch (IOException e) {
            handleInternalError(exchange, e.getMessage());
        } catch (HttpBadRequestException e) {
            handleBadRequest(exchange, e.getMessage());
        } finally {
            closeResponseBody(exchange);
        }
    }

    private PersonResult processPersonRequest(String personID, AuthToken authToken) {
        logPersonRequest(personID);
        return personService.getPerson(personID, authToken);
    }

    private void logPersonRequest(String personID) {
        logSingleObjectRequest(MODEL_NAME, personID);
    }

    private FamilyMembersResult processFamilyMembersRequest(AuthToken authToken) {
        logAllEventsRequest();
        return familyMembersService.getAllFamilyMembers(authToken);
    }

    private void logAllEventsRequest() {
        logAllObjectsRequest(MODEL_NAME);
    }
}
