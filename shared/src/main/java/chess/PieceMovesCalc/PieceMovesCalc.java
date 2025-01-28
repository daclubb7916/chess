package chess.PieceMovesCalc;

import chess.*;
import java.util.Collection;

public interface PieceMovesCalc {

    Collection<ChessMove> pieceMoves();

    default boolean inBounds(int row, int col) { return row > 0 && row < 9 && col > 0 && col < 9; }


}
