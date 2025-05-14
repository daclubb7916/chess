package ui;

import exception.ResponseException;
import result.*;
import request.*;
import server.ServerFacade;
import java.util.Arrays;

import static ui.EscapeSequences.*;

public class PreLogin implements ClientUI {
    private final ServerFacade server;

    public PreLogin(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    @Override
    public ClientResult eval(ClientRequest request) {
        try {
            String input = request.input();
            var tokens = input.split(" ");
            var cmd = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "clear" -> clear();
                case "quit" -> new ClientResult("quit", State.SIGNEDOUT, request.authToken(), null,
                        null, null);
                default -> help();
            };
        } catch (ResponseException ex) {
            return new ClientResult(ex.getMessage(), State.SIGNEDOUT, request.authToken(), null,
                    null, null);
        }
    }

    private ClientResult clear() throws ResponseException {
        server.clear();
        return new ClientResult("Database cleared", State.SIGNEDOUT, null, null,
                null, null);
    }

    public ClientResult help() {
        String result = """
                commands:
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to login to your account
                    quit - to exit application
                    help - to view commands
                """;
        return new ClientResult(result, State.SIGNEDOUT, null, null, null, null);
    }

    @Override
    public void printPrompt() {
        System.out.print("\n" + RESET_BG_COLOR + SET_TEXT_COLOR_LIGHT_GREY);
        System.out.print("Login >>> " + SET_TEXT_COLOR_MAGENTA);
    }

    private ClientResult register(String... params) throws ResponseException {
        if (params.length == 3) {
            RegisterResult result = server.register(new RegisterRequest(params[0], params[1], params[2]));
            String message = String.format("Registered user %s", result.username());
            return new ClientResult(message, State.SIGNEDIN, result.authToken(), null,
                    result.username(), null);
        }
        throw new ResponseException(400, "Expected format: register <USERNAME> <PASSWORD> <EMAIL>");
    }

    private ClientResult login(String... params) throws ResponseException {
        if (params.length == 2) {
            LoginResult result = server.login(new LoginRequest(params[0], params[1]));
            String message = String.format("Signed in as %s", result.username());
            return new ClientResult(message, State.SIGNEDIN, result.authToken(), null,
                    result.username(), null);
        }
        throw new ResponseException(400, "Expected format: login <USERNAME> <PASSWORD>");
    }
}
