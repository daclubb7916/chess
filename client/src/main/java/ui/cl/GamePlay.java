package ui.cl;

import server.ServerFacade;
import ui.*;
import static ui.EscapeSequences.*;

public class GamePlay implements ClientUI {
    private final ServerFacade server;
    private final String serverUrl;

    public GamePlay(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    @Override
    public ClientResult eval(ClientRequest request) {
        return new ClientResult(null, null, null);
    }

    @Override
    public ClientResult help() {
        return new ClientResult(null, null, null);
    }

    @Override
    public void printPrompt() {
        System.out.print("\n" + RESET_BG_COLOR + SET_TEXT_COLOR_LIGHT_GREY);
        System.out.print("Chess Game >>> " + SET_TEXT_COLOR_MAGENTA);
    }
}
