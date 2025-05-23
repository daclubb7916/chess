package chess;

import chess.calc.*;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(pieceColor);
        result = 31 * result + Objects.hashCode(type);
        return result;
    }

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return switch (type) {
            case KING -> {
                KingMovesCalc kingMoves = new KingMovesCalc(board, myPosition);
                yield kingMoves.pieceMoves();
            }
            case QUEEN -> {
                QueenMovesCalc queenMoves = new QueenMovesCalc(board, myPosition);
                yield queenMoves.pieceMoves();
            }
            case BISHOP -> {
                BishopMovesCalc bishopMoves = new BishopMovesCalc(board, myPosition);
                yield bishopMoves.pieceMoves();
            }
            case KNIGHT -> {
                KnightMovesCalc knightMoves = new KnightMovesCalc(board, myPosition);
                yield knightMoves.pieceMoves();
            }
            case ROOK -> {
                RookMovesCalc rookMoves = new RookMovesCalc(board, myPosition);
                yield rookMoves.pieceMoves();
            }
            case PAWN -> {
                PawnMovesCalc pawnMoves = new PawnMovesCalc(board, myPosition);
                yield pawnMoves.pieceMoves();
            }
        };
    }

    public String stringPieceType() {
        String pieceName;
        switch (type) {
            case KNIGHT -> pieceName = "Knight";
            case BISHOP -> pieceName = "Bishop";
            case QUEEN -> pieceName = "Queen";
            case ROOK -> pieceName = "Rook";
            case KING -> pieceName = "King";
            case PAWN -> pieceName = "Pawn";
            default -> pieceName = "";
        }
        return pieceName;
    }
}
