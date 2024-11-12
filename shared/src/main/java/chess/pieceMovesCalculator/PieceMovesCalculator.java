package chess.pieceMovesCalculator;

import chess.*;
import java.util.Collection;

public interface PieceMovesCalculator {
    Collection<ChessMove> pieceMoves();
    boolean inBounds(int row, int col);
    boolean isTeamMate(ChessPosition otherPosition);
    void addToMoves(ChessPosition otherPosition);
}
