package chess.PieceMovesCalc;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public class KingMovesCalc extends PieceMovesCalc {

    public KingMovesCalc(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
    }

    @Override
    public Collection<ChessMove> pieceMoves() {

        int rowIndex = myPosition.getRow();
        int colIndex = myPosition.getColumn();

        singleMove(rowIndex+1, colIndex);
        singleMove(rowIndex-1, colIndex);
        singleMove(rowIndex, colIndex+1);
        singleMove(rowIndex, colIndex-1);
        singleMove(rowIndex+1, colIndex+1);
        singleMove(rowIndex+1, colIndex-1);
        singleMove(rowIndex-1, colIndex+1);
        singleMove(rowIndex-1, colIndex-1);

        return validMoves;
    }
}
