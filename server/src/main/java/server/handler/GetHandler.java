package server.handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import server.exception.HttpBadRequestException;
import server.exception.InvalidUriPathException;
import shared.model.AuthToken;

abstract class GetHandler extends JsonHandler {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String NOT_GET_REQUEST = "Not GET request.";

    void checkRequestMethod(HttpExchange exchange) throws HttpBadRequestException {
        if (!isGetRequest(exchange)) {
            throw new HttpBadRequestException(NOT_GET_REQUEST);
        }
    }

    AuthToken getAuthToken(HttpExchange exchange) {
        Headers headers = exchange.getRequestHeaders();
        String authToken = headers.getFirst(AUTHORIZATION_HEADER);
        return new AuthToken(authToken);
    }

    String getID(String[] components) {
        return components[2];
    }

    boolean isSingleObjectRequest(int numComponents) {
        return 3 == numComponents;
    }

    void checkUriPath(int numComponents, String uriPath) throws InvalidUriPathException {
        if (!(isSingleObjectRequest(numComponents) || isAllObjectsRequest(numComponents))) {
            throw new InvalidUriPathException(uriPath);
        }
    }

    private boolean isAllObjectsRequest(int numComponents) {
        return 2 == numComponents;
    }

    void logSingleObjectRequest(String object, String id) {
        logSuccess(object + " request received: " + object.toLowerCase() + "Id{" + id + "}.");
    }

    void logAllObjectsRequest(String object) {
        logSuccess(object + " request received for all family " + object.toLowerCase() + "s.");
    }
}
