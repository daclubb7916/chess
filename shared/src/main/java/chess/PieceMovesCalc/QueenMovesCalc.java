package chess.PieceMovesCalc;

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
        return validMoves;
    }
}
