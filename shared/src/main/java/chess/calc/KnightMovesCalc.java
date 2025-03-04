package chess.calc;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public class KnightMovesCalc extends PieceMovesCalc {

    public KnightMovesCalc(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
    }

    @Override
    public Collection<ChessMove> pieceMoves() {

        int rowIndex = myPosition.getRow();
        int colIndex = myPosition.getColumn();

        singleMove(rowIndex+2, colIndex+1);
        singleMove(rowIndex+2, colIndex-1);
        singleMove(rowIndex-2, colIndex+1);
        singleMove(rowIndex-2, colIndex-1);
        singleMove(rowIndex+1, colIndex+2);
        singleMove(rowIndex-1, colIndex+2);
        singleMove(rowIndex+1, colIndex-2);
        singleMove(rowIndex-1, colIndex-2);

        return validMoves;
    }
}
