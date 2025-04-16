package ui;

import server.ServerFacade;

import static ui.EscapeSequences.*;

public class GamePlay implements ClientUI {
    private final ServerFacade server;

    public GamePlay(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    @Override
    public ClientResult eval(ClientRequest request) {
        return new ClientResult(null, null, null);
    }

    public ClientResult help() {
        String result = """
                commands:
                    create <NAME> - to create a chess game
                    list - to list all chess games
                    join <ID> [WHITE|BLACK] - to join a chess game
                    observe <ID> - to observe a chess game
                    logout - to exit to login menu
                    help - to view commands
                """;
        return new ClientResult(result, State.INGAME, null);
    }

    @Override
    public void printPrompt() {
        System.out.print("\n" + RESET_BG_COLOR + SET_TEXT_COLOR_LIGHT_GREY);
        System.out.print("Chess Game >>> " + SET_TEXT_COLOR_MAGENTA);
    }
}
