package chess.PieceMovesCalc;

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

        int rowIndex = myPosition.getRow();
        int colIndex = myPosition.getColumn();

        multiMove(rowIndex, colIndex, 1, 1);
        multiMove(rowIndex, colIndex, 1, -1);
        multiMove(rowIndex, colIndex, -1, 1);
        multiMove(rowIndex, colIndex, -1, -1);

        return validMoves;
    }
}
