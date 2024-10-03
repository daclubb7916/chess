package chess;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Cloneable {
    private ChessPiece[][] squares = new ChessPiece[8][8];

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

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
        squares[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // white side
        ChessPiece whiteRook = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        ChessPosition position = new ChessPosition(1, 1);
        addPiece(position, whiteRook);
        whiteRook = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        position = new ChessPosition(1, 8);
        addPiece(position, whiteRook);

        ChessPiece whiteKnight = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        position = new ChessPosition(1, 2);
        addPiece(position, whiteKnight);
        whiteKnight = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        position = new ChessPosition(1, 7);
        addPiece(position, whiteKnight);

        ChessPiece whiteBishop = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        position = new ChessPosition(1, 3);
        addPiece(position, whiteBishop);
        whiteBishop = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        position = new ChessPosition(1, 6);
        addPiece(position, whiteBishop);

        ChessPiece whiteQueen = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        position = new ChessPosition(1, 4);
        addPiece(position, whiteQueen);
        ChessPiece whiteKing = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        position = new ChessPosition(1, 5);
        addPiece(position, whiteKing);

        for (int i = 1; i < 9; i++) {
            ChessPiece whitePawn = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            position = new ChessPosition(2, i);
            addPiece(position, whitePawn);
        }

        //black side
        ChessPiece blackRook = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        position = new ChessPosition(8, 1);
        addPiece(position, blackRook);
        blackRook = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        position = new ChessPosition(8, 8);
        addPiece(position, blackRook);

        ChessPiece blackKnight = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        position = new ChessPosition(8, 2);
        addPiece(position, blackKnight);
        blackKnight = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        position = new ChessPosition(8, 7);
        addPiece(position, blackKnight);

        ChessPiece blackBishop = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        position = new ChessPosition(8, 3);
        addPiece(position, blackBishop);
        blackBishop = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        position = new ChessPosition(8, 6);
        addPiece(position, blackBishop);

        ChessPiece blackQueen = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        position = new ChessPosition(8, 4);
        addPiece(position, blackQueen);
        ChessPiece blackKing = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        position = new ChessPosition(8, 5);
        addPiece(position, blackKing);

        for (int i = 1; i < 9; i++) {
            ChessPiece blackPawn = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            position = new ChessPosition(7, i);
            addPiece(position, blackPawn);
        }
    }

    private boolean kingInMoves(ChessPosition newPosition) {
        ChessPiece newChessPiece = getPiece(newPosition);
        Collection<ChessMove> newPieceMoves = newChessPiece.pieceMoves(this, newPosition);
        for (ChessMove aMove : newPieceMoves) {
            ChessPiece endPiece = getPiece(aMove.getEndPosition());
            if (endPiece == null) {
                continue;
            }
            if (getPiece(aMove.getEndPosition()).getPieceType() == ChessPiece.PieceType.KING) { // getPiece returns a null?
                return true;
            }
        }
        return false;
    }

    public boolean isInCheck(ChessGame.TeamColor teamColor) {

        for (int rowIndex = 1; rowIndex < 9; rowIndex++) {
            for (int colIndex = 1; colIndex < 9; colIndex++) {

                ChessPosition newPosition = new ChessPosition(rowIndex, colIndex);
                if (getPiece(newPosition) == null) {
                    continue;
                }
                if (getPiece(newPosition).getTeamColor() == teamColor) {
                    continue;
                }
                if (kingInMoves(newPosition)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public ChessBoard clone() {
        try {
            ChessBoard clone = (ChessBoard) super.clone();

            ChessPiece[][] clonedSquares = new ChessPiece[8][8];
            for (int i = 0; i < 8; i++) {
                //this might work lol
                System.arraycopy(squares[i], 0, clonedSquares[i], 0, 8);
            }

            clone.squares = clonedSquares;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}


