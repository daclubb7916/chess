package ui.websocket;

import websocket.messages.*;

public interface NotificationHandler {
    void notify(NotificationMessage notifyMessage);
    void loadGame(LoadGameMessage loadMessage);
    void error(ErrorMessage errorMessage);
}
