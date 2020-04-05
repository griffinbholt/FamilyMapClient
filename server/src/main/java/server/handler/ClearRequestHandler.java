package server.handler;

import com.sun.net.httpserver.HttpExchange;
import server.exception.HttpBadRequestException;
import server.service.ClearService;
import shared.http.FamilyMapUrl;
import shared.result.ClearResult;

import java.io.IOException;

/**
 * A handler to process requests to clear the database
 * @author griffinbholt
 */
public final class ClearRequestHandler extends PostHandler {
    private final ClearService clearService = new ClearService();

    /**
     * The method called by the server when an incoming {@link com.sun.net.httpserver.HttpExchange HttpExchange} for a
     * clear request is sent to the server
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

            checkUriPath(FamilyMapUrl.CLEAR, uriPath);

            ClearResult result = processClearRequest();

            sendResponse(exchange, result);
        } catch (IOException e) {
            handleInternalError(exchange, e.getMessage());
        } catch (HttpBadRequestException e) {
            handleBadRequest(exchange, e.getMessage());
        } finally {
            closeResponseBody(exchange);
        }
    }

    private ClearResult processClearRequest() {
        logClearRequest();
        return clearService.clearTables();
    }

    private void logClearRequest() {
        logSuccess("Clear request received.");
    }
}