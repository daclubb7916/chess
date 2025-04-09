package ui.cl;

import server.ServerFacade;

public class GamePlay implements ClientUI {
    private final ServerFacade server;
    private final String serverUrl;

    public GamePlay(String serverUrl) {
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
}
