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

        int rowIndex = myPosition.getRow();
        int colIndex = myPosition.getColumn();

        multiMove(rowIndex, colIndex, 1, 0);
        multiMove(rowIndex, colIndex, -1, 0);
        multiMove(rowIndex, colIndex, 0, 1);
        multiMove(rowIndex, colIndex, 0, -1);

        return validMoves;
    }
}
