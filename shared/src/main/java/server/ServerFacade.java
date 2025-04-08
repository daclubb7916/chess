package server;

import com.google.gson.Gson;
import exception.ResponseException;
import request.*;
import result.*;

import java.io.*;
import java.net.*;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public void clear() throws ResponseException {
        this.makeRequest("DELETE", "/db", null, null);
    }

    public CreateGameResult createGame(CreateGameRequest req) throws ResponseException {
        return this.makeRequest("POST", "/game", req, CreateGameResult.class);
    }

    public void joinGame(JoinGameRequest req) throws ResponseException {
        this.makeRequest("PUT", "/game", req, null);
    }

    public LoginResult login(LoginRequest req) throws ResponseException {
        return this.makeRequest("POST", "/session", req, LoginResult.class);
    }

    public void logout(LogoutRequest req) throws ResponseException {
        this.makeRequest("DELETE", "/session", req, null);
    }

    public RegisterResult register(RegisterRequest req) throws ResponseException {
        return this.makeRequest("POST", "/user", req, RegisterResult.class);
    }

    public ListGamesResult listGames(ListGamesRequest req) throws ResponseException {
        return this.makeRequest("GET", "/game", req, ListGamesResult.class);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
