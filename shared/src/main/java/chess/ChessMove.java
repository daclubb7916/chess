package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(startPosition, chessMove.startPosition) && Objects.equals(endPosition, chessMove.endPosition) && promotionPiece == chessMove.promotionPiece;
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(startPosition);
        result = 31 * result + Objects.hashCode(endPosition);
        result = 31 * result + Objects.hashCode(promotionPiece);
        return result;
    }

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

    @Override
    public String toString() {
        String promo;
        switch (promotionPiece) {
            case KING:
                promo = "King";
                break;
            case PAWN:
                promo = "Pawn";
                break;
            case ROOK:
                promo = "Rook";
                break;
            case QUEEN:
                promo = "Queen";
                break;
            case BISHOP:
                promo = "Bishop";
                break;
            case KNIGHT:
                promo = "Knight";
                break;
            case null:
                promo = "null";
                break;
        }
        return startPosition.toString() + "->" + endPosition.toString() + promo;
    }
}
