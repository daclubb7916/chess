package chess.PieceMovesCalc;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public class RookMovesCalc extends PieceMovesCalc {

    public RookMovesCalc(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
    }

    @Override
    public Collection<ChessMove> pieceMoves() {

        multiMove(1, 0);
        multiMove(-1, 0);
        multiMove(0, 1);
        multiMove(0, -1);

        return validMoves;
    }
}
