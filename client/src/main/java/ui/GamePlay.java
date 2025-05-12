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
                    redraw - redraws the chess board
                    leave - leave current chess game
                    move <start> <end> - make a chess move (Example: move A2 A4)
                    resign - forfeit current chess game
                    legal moves <piece> - highlights legal moves for a chess piece
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
