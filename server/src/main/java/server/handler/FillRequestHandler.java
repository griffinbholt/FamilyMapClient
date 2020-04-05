package server.handler;

import com.sun.net.httpserver.HttpExchange;
import server.exception.HttpBadRequestException;
import server.exception.InvalidUriPathException;
import server.service.FillService;
import server.service.helper.FamilyDataGenerator;
import shared.request.FillRequest;
import shared.result.FillResult;

import java.io.IOException;
import java.util.Arrays;

/**
 * A handler to process {@link shared.request.FillRequest fill requests}
 * @author griffinbholt
 */
public final class FillRequestHandler extends PostHandler {
    private final FillService fillService = new FillService();

    /**
     * The method called by the server when an incoming {@link com.sun.net.httpserver.HttpExchange HttpExchange} for a
     * fill request is sent to the server
     * @param exchange The incoming {@link com.sun.net.httpserver.HttpExchange HttpExchange}
     * @throws IOException An error occurred while interacting with the
     *                     {@link com.sun.net.httpserver.HttpExchange HttpExchange}
     */
    @Override
    @SuppressWarnings("OverlyBroadCatchBlock")
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String[] components = getRequestURIPathComponents(exchange);

            checkRequestMethod(exchange);

            FillRequest request = parse(components);

            FillResult result = processFillRequest(request);

            sendResponse(exchange, result);
        } catch (IOException e) {
            handleInternalError(exchange, e.getMessage());
        } catch (HttpBadRequestException e) {
            handleBadRequest(exchange, e.getMessage());
        } finally {
            closeResponseBody(exchange);
        }
    }

    private FillResult processFillRequest(FillRequest request) {
        logFillRequest(request);
        return fillService.fill(request);
    }

    private void logFillRequest(FillRequest request) {
        String username = request.getUserName();
        int numGenerations = request.getNumGenerations();
        logSuccess("Fill request received: username{" + username + "}, numGenerations{" + numGenerations +"}.");
    }

    private FillRequest parse(String[] components) throws InvalidUriPathException {
        checkComponents(components.length, Arrays.toString(components));

        String username = getUsername(components);
        int numGenerations = getNumGenerations(components);

        return new FillRequest(username, numGenerations);
    }

    private void checkComponents(int numComponents, String uriPath) throws InvalidUriPathException {
        if (!(defersToDefaultNumGenerations(numComponents) || specifiesNumGenerations(numComponents))) {
            throw new InvalidUriPathException(uriPath);
        }
    }

    private boolean defersToDefaultNumGenerations(int numComponents) {
        return 3 == numComponents;
    }


    private String getUsername(String[] components) {
        return components[2];
    }

    private int getNumGenerations(String[] components) {
        return specifiesNumGenerations(components.length) ?
                getSpecifiedNumGenerations(components) : FamilyDataGenerator.DEFAULT_NUM_GENERATIONS;
    }

    private int getSpecifiedNumGenerations(String[] components) {
        return Integer.parseInt(components[3]);
    }

    private boolean specifiesNumGenerations(int numComponents) {
        return 4 == numComponents;
    }
}
