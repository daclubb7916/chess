package ui;

import server.ServerFacade;

public class PreLogin {
    private final ServerFacade server;
    private final String serverUrl;

    public PreLogin(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }
}
