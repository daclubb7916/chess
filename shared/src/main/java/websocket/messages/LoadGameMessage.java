package websocket.messages;

import model.GameData;

public class LoadGameMessage extends ServerMessage {
    private final GameData game;

    public LoadGameMessage(GameData gameData) {
        super(ServerMessageType.LOAD_GAME);
        this.game = gameData;
    }

    public GameData getGameData() {
        return game;
    }
}
