package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private final ChessPiece[][] squares = new ChessPiece[8][8];

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow() - 1][position.getColumn() - 1];
    }

    private void createAndAddPiece(ChessGame.TeamColor color, ChessPiece.PieceType type, int row, int col) {
        ChessPiece piece = new ChessPiece(color, type);
        ChessPosition position = new ChessPosition(row, col);
        addPiece(position, piece);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        createAndAddPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK, 1, 1);
        createAndAddPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK, 1, 8);
        createAndAddPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT, 1, 2);
        createAndAddPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT, 1, 7);
        createAndAddPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP, 1, 3);
        createAndAddPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP, 1, 6);
        createAndAddPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN, 1, 4);
        createAndAddPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING, 1, 5);

        for (int i = 1; i < 9; i++) {
            createAndAddPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN, 2, i);
        }

        createAndAddPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK, 8, 1);
        createAndAddPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK, 8, 8);
        createAndAddPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT, 8, 2);
        createAndAddPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT, 8, 7);
        createAndAddPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP, 8, 3);
        createAndAddPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP, 8, 6);
        createAndAddPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN, 8, 4);
        createAndAddPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING, 8, 5);

        for (int i = 1; i < 9; i++) {
            createAndAddPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN, 7, i);
        }
    }
}
