package ui;

import server.ServerFacade;

public class PostLogin {
    private final ServerFacade server;
    private final String serverUrl;

    public PostLogin(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }
}
