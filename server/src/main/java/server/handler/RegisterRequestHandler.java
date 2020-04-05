package server.handler;

import com.sun.net.httpserver.HttpExchange;
import server.exception.HttpBadRequestException;
import server.service.RegisterService;
import shared.http.FamilyMapUrl;
import shared.request.RegisterRequest;
import shared.result.RegisterResult;

import java.io.IOException;

/**
 * A handler to process {@link RegisterRequest register requests}
 * @author griffinbholt
 */
public final class RegisterRequestHandler extends PostHandler {
    private final RegisterService registerService = new RegisterService();

    /**
     * The method called by the server when an incoming {@link com.sun.net.httpserver.HttpExchange HttpExchange} for a
     * register request is sent to the server
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
            checkUriPath(FamilyMapUrl.REGISTER, uriPath);

            RegisterRequest request = getRegisterRequest(exchange);

            RegisterResult result = processRegisterRequest(request);

            sendResponse(exchange, result);
        } catch (IOException e) {
            handleInternalError(exchange, e.getMessage());
        } catch (HttpBadRequestException e) {
            handleBadRequest(exchange, e.getMessage());
        } finally {
            closeResponseBody(exchange);
        }
    }

    private RegisterRequest getRegisterRequest(HttpExchange exchange) throws IOException {
        return (RegisterRequest) getRequest(exchange, RegisterRequest.class);
    }

    private RegisterResult processRegisterRequest(RegisterRequest request) {
        logRegisterRequest(request.getUserName());
        return registerService.register(request);
    }

    private void logRegisterRequest(String username) {
        logSuccess("Register request received for new user: " + username);
    }
}
