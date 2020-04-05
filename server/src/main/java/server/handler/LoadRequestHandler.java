package server.handler;

import com.sun.net.httpserver.HttpExchange;
import server.exception.HttpBadRequestException;
import server.service.LoadService;
import shared.http.FamilyMapUrl;
import shared.request.LoadRequest;
import shared.result.LoadResult;

import java.io.IOException;

/**
 * A handler to process {@link LoadRequest load requests}
 * @author griffinbholt
 */
public final class LoadRequestHandler extends PostHandler {
    private final LoadService loadService = new LoadService();

    /**
     * The method called by the server when an incoming {@link com.sun.net.httpserver.HttpExchange HttpExchange} for a
     * load request is sent to the server
     * @param exchange The incoming {@link com.sun.net.httpserver.HttpExchange HttpExchange}
     * @throws IOException An error occurred while interacting with the
     *                     {@link com.sun.net.httpserver.HttpExchange HttpExchange}
     */
    @Override
    @SuppressWarnings("OverlyBroadCatchBlock")
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String uriPath = getRequestURIPath(exchange);

            checkRequestMethod(exchange);
            checkUriPath(FamilyMapUrl.LOAD, uriPath);

            LoadRequest request = getLoadRequest(exchange);

            LoadResult result = processLoadRequest(request);

            sendResponse(exchange, result);
        } catch (IOException e) {
            handleInternalError(exchange, e.getMessage());
        } catch (HttpBadRequestException e) {
            handleBadRequest(exchange, e.getMessage());
        } finally {
            closeResponseBody(exchange);
        }
    }

    private LoadRequest getLoadRequest(HttpExchange exchange) throws IOException {
        return (LoadRequest) getRequest(exchange, LoadRequest.class);
    }

    private LoadResult processLoadRequest(LoadRequest request) {
        logLoadRequest(request);
        return loadService.load(request);
    }

    private void logLoadRequest(LoadRequest request) {
        int numUsers = request.getUsers().size();
        int numPersons = request.getPersons().size();
        int numEvents = request.getEvents().size();

        String logMessage = "Load request received: " + numUsers + " users, " + numPersons + " persons, & "
                + numEvents + " events.";

        logSuccess(logMessage);
    }

}