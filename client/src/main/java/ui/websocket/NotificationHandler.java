package ui.websocket;

import websocket.messages.*;

public interface NotificationHandler {
    void notify(NotificationMessage notificationMessage);
    void error(ErrorMessage errorMessage);
    void loadGame(LoadGameMessage loadGameMessage);
}
