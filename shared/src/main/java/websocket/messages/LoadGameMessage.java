package websocket.messages;

public class LoadGameMessage extends ServerMessage {
    private final String chessBoard;

    public LoadGameMessage(String chessBoard) {
        super(ServerMessageType.LOAD_GAME);
        this.chessBoard = chessBoard;
    }

    public String getBoard() {
        return chessBoard;
    }
}
