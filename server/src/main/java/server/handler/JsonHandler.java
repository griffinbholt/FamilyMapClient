package server.handler;

import com.sun.net.httpserver.HttpExchange;
import server.exception.HttpBadRequestException;
import server.exception.InvalidUriPathException;
import shared.json.JsonInterpreter;
import shared.result.Result;

import java.io.*;

abstract class JsonHandler extends Handler {
    private static final int READ_BUFFER_LENGTH = 1024;

    private JsonInterpreter jsonInterpreter = new JsonInterpreter();

    String[] getRequestURIPathComponents(HttpExchange exchange) {
        String uriPath = getRequestURIPath(exchange);
        return uriPath.split("/");
    }

    void sendResponse(HttpExchange exchange, Result result) throws IOException {
        sendAppropriateResponseHeaders(exchange, result);
        writeResultToResponseBody(result, exchange.getResponseBody());
    }

    private void sendAppropriateResponseHeaders(HttpExchange exchange, Result result) throws IOException {
        if (result.isSuccess()) {
            sendHttpOkResponse(exchange);
        } else {
            sendHttpBadRequestResponse(exchange);
        }
    }

    private void writeResultToResponseBody(Result result, OutputStream respBody) throws IOException {
        String resultJsonString = jsonInterpreter.generateJsonString(result);
        writeStringToOutputStream(resultJsonString, respBody);
    }

    Object getRequest(HttpExchange exchange, Class<?> jsonClass) throws IOException {
        InputStream requestBody = exchange.getRequestBody();
        String jsonString = readStringFromInputStream(requestBody);
        return jsonInterpreter.parseJson(jsonString, jsonClass);
    }

    @SuppressWarnings("NestedAssignment")
    private String readStringFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        InputStreamReader streamReader = new InputStreamReader(inputStream);
        char[] buf = new char[READ_BUFFER_LENGTH];
        int len;
        while (0 < (len = streamReader.read(buf))) {
            stringBuilder.append(buf, 0, len);
        }
        return stringBuilder.toString();
    }

    private void writeStringToOutputStream(String str, OutputStream outputStream) throws IOException {
        OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream);
        BufferedWriter bufferedWriter = new BufferedWriter(streamWriter);
        bufferedWriter.write(str);
        bufferedWriter.flush();
    }

    void checkUriPath(String expectedPath, String uriPath) throws InvalidUriPathException {
        if (!(uriPath.equals(expectedPath)) || (uriPath.equals(expectedPath + "/"))) {
            throw new InvalidUriPathException(uriPath);
        }
    }

    abstract void checkRequestMethod(HttpExchange exchange) throws HttpBadRequestException;
}
