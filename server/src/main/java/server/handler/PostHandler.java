package server.handler;

import com.sun.net.httpserver.HttpExchange;
import server.exception.HttpBadRequestException;

abstract class PostHandler extends JsonHandler {
    private static final String POST = "POST";
    private static final String NOT_POST_REQUEST = "Not POST request.";

    void checkRequestMethod(HttpExchange exchange) throws HttpBadRequestException {
        if (!isPostRequest(exchange)) {
            throw new HttpBadRequestException(NOT_POST_REQUEST);
        }
    }

    private boolean isPostRequest(HttpExchange exchange) {
        return POST.equals(exchange.getRequestMethod().toUpperCase());
    }
}
