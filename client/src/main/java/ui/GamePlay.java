package ui;

import server.ServerFacade;

public class GamePlay {
    private final ServerFacade server;
    private final String serverUrl;

    public GamePlay(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }
}
