package facade;

import com.google.gson.Gson;
import exception.ResponseException;
import request.*;
import result.*;
import java.io.*;
import java.net.*;
import java.util.HashMap;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public void clear() throws ResponseException {
        try {
            HttpURLConnection http = setupHttp("DELETE", "/db");
            http.connect();
            throwIfNotSuccessful(http);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public CreateGameResult createGame(CreateGameRequest req) throws ResponseException {
        try {
            HttpURLConnection http = setupHttp("POST", "/game");
            http.addRequestProperty("authorization", req.authToken());
            writeBody(new CreateGameRequest(req.gameName(), null), http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, CreateGameResult.class);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void joinGame(JoinGameRequest req) throws ResponseException {
        try {
            HttpURLConnection http = setupHttp("PUT", "/game");
            http.addRequestProperty("authorization", req.authToken());
            writeBody(new JoinGameRequest(req.playerColor(), req.gameID(), null), http);
            http.connect();
            throwIfNotSuccessful(http);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public LoginResult login(LoginRequest req) throws ResponseException {
        try {
            HttpURLConnection http = setupHttp("POST", "/session");
            writeBody(req, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, LoginResult.class);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void logout(LogoutRequest req) throws ResponseException {
        try {
            HttpURLConnection http = setupHttp("DELETE", "/session");
            http.addRequestProperty("authorization", req.authorization());
            http.connect();
            throwIfNotSuccessful(http);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public RegisterResult register(RegisterRequest req) throws ResponseException {
        try {
            HttpURLConnection http = setupHttp("POST", "/user");
            writeBody(req, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, RegisterResult.class);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public ListGamesResult listGames(ListGamesRequest req) throws ResponseException {
        try {
            HttpURLConnection http = setupHttp("GET", "/game");
            http.addRequestProperty("authorization", req.authToken());
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, ListGamesResult.class);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private HttpURLConnection setupHttp(String method, String path) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            return http;
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
                    var map = new Gson().fromJson(new InputStreamReader(respErr), HashMap.class);
                    String message = map.get("message").toString();
                    throw new ResponseException(status, message);
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
