package ui.cl;

import exception.ResponseException;
import server.ServerFacade;
import java.util.Arrays;
import ui.*;
import static ui.EscapeSequences.*;

public class PreLogin implements ClientUI {
    private final ServerFacade server;
    private final String serverUrl;

    public PreLogin(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
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
                case "quit" -> new ClientResult("quit", State.SIGNEDOUT, request.authToken());
                default -> help();
            };
        } catch (ResponseException ex) {
            return new ClientResult(ex.getMessage(), State.SIGNEDOUT, request.authToken());
        }
    }

    @Override
    public ClientResult help() {
        String result = """
                commands:
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to login to your account
                    quit - to exit application
                    help - to view commands
                """;
        return new ClientResult(result, State.SIGNEDOUT, null);
    }

    @Override
    public void printPrompt() {
        System.out.print("\n" + RESET_BG_COLOR + SET_TEXT_COLOR_LIGHT_GREY);
        System.out.print("Login >>> " + SET_TEXT_COLOR_MAGENTA);
    }

    private ClientResult register(String... params) throws ResponseException {
        if (params.length == 3) {

        }
        throw new ResponseException(400, "Expected format: register <USERNAME> <PASSWORD> <EMAIL>");
    }

    private ClientResult login(String... params) throws ResponseException {
        return new ClientResult("", State.SIGNEDIN, null);
    }
}
