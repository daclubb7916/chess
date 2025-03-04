package chess.calc;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public class BishopMovesCalc extends PieceMovesCalc {

    public BishopMovesCalc(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
    }

    @Override
    public Collection<ChessMove> pieceMoves() {

        multiMove(1, 1);
        multiMove(1, -1);
        multiMove(-1, 1);
        multiMove(-1, -1);

        return validMoves;
    }
}
