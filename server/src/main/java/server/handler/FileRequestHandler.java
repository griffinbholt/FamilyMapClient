package server.handler;

import com.sun.net.httpserver.HttpExchange;
import server.exception.HttpBadMethodException;
import shared.http.FamilyMapUrl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * The default handler for the server
 * @author griffinbholt
 */
public final class FileRequestHandler extends Handler {
    private static final String WEB_DIR = "web";
    private static final String WEB_INDEX = "/index.html";
    private static final String FILE_NOT_FOUND = WEB_DIR + "/HTML/404.html";


    /**
     * The method called by the server when it receives an {@link com.sun.net.httpserver.HttpExchange HttpExchange} that
     * is not handled by any other handlers
     * @param exchange The incoming {@link com.sun.net.httpserver.HttpExchange HttpExchange}
     * @throws IOException An error occurred while interacting with the
     *                     {@link com.sun.net.httpserver.HttpExchange HttpExchange}
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String urlPath = null;

        try {
            throwBadMethodIfNotGetRequest(exchange);

            urlPath = getURLPath(exchange);

            sendResponse(urlPath, exchange);
        } catch (HttpBadMethodException e) {
            handleBadMethod(exchange, e.getMessage());
        } catch (FileNotFoundException e) {
            handleNotFound(exchange, urlPath);
        } catch (IOException e) {
            handleInternalError(exchange, e.getMessage());
        } finally {
            closeResponseBody(exchange);
        }
    }

    private void throwBadMethodIfNotGetRequest(HttpExchange exchange) throws HttpBadMethodException {
        if (!isGetRequest(exchange)) {
            throw new HttpBadMethodException();
        }
    }

    private String getURLPath(HttpExchange exchange) {
        String uriPath = getRequestURIPath(exchange);

        return (null == uriPath || FamilyMapUrl.DEFAULT_PATH.equals(uriPath)) ? WEB_DIR + WEB_INDEX : WEB_DIR + uriPath;
    }

    @SuppressWarnings("DuplicateThrows")
    private void sendResponse(String urlPath, HttpExchange exchange) throws FileNotFoundException, IOException {
        File urlFile = new File(urlPath);

        if (urlFile.exists()) {
            sendHttpOkResponse(exchange);
            sendFile(urlFile, exchange);
            logSuccess(HTTP_OK + ": Page found: " + urlPath);
            return;
        }

        throw new FileNotFoundException();
    }

    private void handleNotFound(HttpExchange exchange, String urlPath) throws IOException {
        sendHttpNotFoundResponse(exchange);
        sendFile(new File(FILE_NOT_FOUND), exchange);
        logError(HTTP_NOT_FOUND + ": Page not found: " + urlPath);
    }

    private void sendFile(File file, HttpExchange exchange) throws IOException {
        OutputStream respBody = exchange.getResponseBody();
        Files.copy(file.toPath(), respBody);
    }
}
