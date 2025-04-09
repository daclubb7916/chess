package ui.cl;

import server.ServerFacade;
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
        return "";
    }

    @Override
    public String help() {
        return "";
    }

    @Override
    public void printPrompt() {
        System.out.print("\n" + RESET_BG_COLOR + SET_TEXT_COLOR_LIGHT_GREY);
        System.out.print("Login >>> " + SET_TEXT_COLOR_MAGENTA);
    }
}
