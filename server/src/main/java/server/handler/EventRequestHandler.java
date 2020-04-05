package server.handler;

import com.sun.net.httpserver.HttpExchange;
import server.exception.HttpBadRequestException;

import server.service.AllEventsService;
import server.service.SingleEventService;
import shared.model.AuthToken;
import shared.model.ServerEvent;
import shared.result.AllEventsResult;
import shared.result.EventResult;
import shared.result.Result;

import java.io.IOException;
import java.util.Arrays;

/**
 * A handler to process {@link ServerEvent ServerEvent} requests
 * @author griffinbholt
 */
public final class EventRequestHandler extends GetHandler {
    private static final String MODEL_NAME = "ServerEvent";

    private final SingleEventService singleEventService = new SingleEventService();
    private final AllEventsService allEventsService = new AllEventsService();

    /**
     * The method called by the server when an incoming {@link com.sun.net.httpserver.HttpExchange HttpExchange} for an
     * event request is sent to the server
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
                String eventID = getID(components);
                result = processSingleEventRequest(eventID, authToken);
            } else {
                result = processAllEventsRequest(authToken);
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

    private EventResult processSingleEventRequest(String eventID, AuthToken authToken) {
        logSingleEventRequest(eventID);
        return singleEventService.getEvent(eventID, authToken);
    }

    private void logSingleEventRequest(String eventID) {
        logSingleObjectRequest(MODEL_NAME, eventID);
    }

    private AllEventsResult processAllEventsRequest(AuthToken authToken) {
        logAllEventsRequest();
        return allEventsService.getAllEvents(authToken);
    }

    private void logAllEventsRequest() {
        logAllObjectsRequest(MODEL_NAME);
    }
}
