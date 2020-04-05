package server.handler;

import com.sun.net.httpserver.HttpExchange;
import server.exception.HttpBadRequestException;
import server.service.LoginService;
import shared.http.FamilyMapUrl;
import shared.request.LoginRequest;
import shared.result.LoginResult;

import java.io.IOException;

/**
 * A handler to process {@link LoginRequest login requests}
 * @author griffinbholt
 */
public final class LoginRequestHandler extends PostHandler {
    private final LoginService loginService = new LoginService();

    /**
     * The method called by the server when an incoming {@link com.sun.net.httpserver.HttpExchange HttpExchange} for a
     * login request is sent to the server
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

            checkUriPath(FamilyMapUrl.LOGIN, uriPath);

            LoginRequest request = getLoginRequest(exchange);

            LoginResult result = processLoginRequest(request);

            sendResponse(exchange, result);
        } catch (IOException e) {
            handleInternalError(exchange, e.getMessage());
        } catch (HttpBadRequestException e) {
            handleBadRequest(exchange, e.getMessage());
        } finally {
            closeResponseBody(exchange);
        }
    }

    private LoginRequest getLoginRequest(HttpExchange exchange) throws IOException {
        return (LoginRequest) getRequest(exchange, LoginRequest.class);
    }

    private LoginResult processLoginRequest(LoginRequest request) {
        logLoginRequest(request.getUserName());
        return loginService.login(request);
    }

    private void logLoginRequest(String username) {
        logSuccess("Login request received for user: " + username);
    }
}
