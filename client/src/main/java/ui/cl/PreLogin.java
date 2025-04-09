package ui.cl;

import exception.ResponseException;
import server.ServerFacade;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class PreLogin implements ClientUI {
    private final ServerFacade server;
    private final String serverUrl;

    public PreLogin(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    @Override
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    @Override
    public String help() {
        return """
                commands:
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to login to your account
                    quit - to exit application
                    help - to view commands
                """;
    }

    @Override
    public void printPrompt() {
        System.out.print("\n" + RESET_BG_COLOR + SET_TEXT_COLOR_LIGHT_GREY);
        System.out.print("Login >>> " + SET_TEXT_COLOR_MAGENTA);
    }

    private String register(String... params) throws ResponseException {
        return "";
    }

    private String login(String... params) throws ResponseException {
        return "";
    }
}
