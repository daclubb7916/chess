package chess.calc;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public class QueenMovesCalc extends PieceMovesCalc {

    public QueenMovesCalc(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
    }

    @Override
    public Collection<ChessMove> pieceMoves() {

        multiMove(1, 0);
        multiMove(-1, 0);
        multiMove(0, 1);
        multiMove(0, -1);
        multiMove(1, 1);
        multiMove(1, -1);
        multiMove(-1, 1);
        multiMove(-1, -1);

        return validMoves;
    }
}
