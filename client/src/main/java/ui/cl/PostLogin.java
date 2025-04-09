package ui.cl;

import server.ServerFacade;
import static ui.EscapeSequences.*;

public class PostLogin implements ClientUI {
    private final ServerFacade server;
    private final String serverUrl;

    public PostLogin(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    @Override
    public String eval(String input) {
        return "";
    }

    @Override
    public String help() {
        return """
                commands:
                    create <NAME> - to create a chess game
                    list - to list all chess games
                    join <ID> [WHITE|BLACK] - to join a chess game
                    observe <ID> - to observe a chess game
                    logout - to exit to login menu
                    help - to view commands
                """;
    }

    @Override
    public void printPrompt() {
        System.out.print("\n" + RESET_BG_COLOR + SET_TEXT_COLOR_LIGHT_GREY);
        System.out.print("Chess >>> " + SET_TEXT_COLOR_MAGENTA);
    }
}
