package chess.PieceMovesCalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalc implements PieceMovesCalculator {
    private final ChessBoard board;
    private final ChessPosition myPosition;
    private ArrayList<ChessMove> validMoves = new ArrayList<>();

    public KnightMovesCalc(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
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

    public void singleMove(int row, int col) {
        ChessPosition otherPosition = new ChessPosition(row, col);
        if (isValid(otherPosition)) {
            addToMoves(otherPosition);
        }
    }

    @Override
    public boolean inBounds(int row, int col) {
        return row > 0 && row < 9 && col > 0 && col < 9;
    }

    @Override
    public boolean isTeamMate(ChessPosition otherPosition) {
        return board.getPiece(myPosition).getTeamColor() == board.getPiece(otherPosition).getTeamColor();
    }

    public boolean isValid(ChessPosition otherPosition) {
        if (!inBounds(otherPosition.getRow(), otherPosition.getColumn())) {
            return false;
        }
        if (board.getPiece(otherPosition) == null) {
            return true;
        }
        return !isTeamMate(otherPosition);
    }

    @Override
    public void addToMoves(ChessPosition otherPosition) {
        ChessMove validMove = new ChessMove(myPosition, otherPosition, null);
        validMoves.add(validMove);
    }
}
