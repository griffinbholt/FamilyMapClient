package server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.logging.Logger;

abstract class Handler implements HttpHandler {
    private static final String GET = "GET";

    private final Logger logger = Logger.getLogger(this.getClass().toString());


    String getRequestURIPath(HttpExchange exchange) {
        return exchange.getRequestURI().getPath();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean isGetRequest(HttpExchange exchange) {
        return GET.equals(exchange.getRequestMethod().toUpperCase());
    }

    void closeResponseBody(HttpExchange exchange) throws IOException {
        exchange.getResponseBody().close();
    }

    void handleInternalError(HttpExchange exchange, String errorMessage) throws IOException {
        sendHttpInternalErrorResponse(exchange);
        logError(errorMessage);
    }

    void handleBadRequest(HttpExchange exchange, String errorMessage) throws IOException {
        sendHttpBadRequestResponse(exchange);
        logError(errorMessage);
    }

    void handleBadMethod(HttpExchange exchange, String errorMessage) throws IOException {
        sendHttpBadMethodResponse(exchange);
        logError(errorMessage);
    }

    void sendHttpOkResponse(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
    }

    private void sendHttpInternalErrorResponse(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
    }

    void sendHttpNotFoundResponse(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
    }

    void sendHttpBadRequestResponse(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
    }

    private void sendHttpBadMethodResponse(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
    }

    void logError(String message) {
        logger.warning("Error: " + message + "\n");
    }

    void logSuccess(String message) {
        logger.info(message + "\n");
    }
}
