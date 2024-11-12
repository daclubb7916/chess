package chess.pieceMovesCalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalc implements PieceMovesCalculator {
    private final ChessBoard board;
    private final ChessPosition myPosition;
    private ArrayList<ChessMove> validMoves = new ArrayList<>();

    public QueenMovesCalc(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
    }

    @Override
    public Collection<ChessMove> pieceMoves() {

        int rowIndex = myPosition.getRow();
        int colIndex = myPosition.getColumn();

        multiMove(rowIndex, colIndex, 1, 0);
        multiMove(rowIndex, colIndex, -1, 0);
        multiMove(rowIndex, colIndex, 0, 1);
        multiMove(rowIndex, colIndex, 0, -1);
        multiMove(rowIndex, colIndex, 1, 1);
        multiMove(rowIndex, colIndex, 1, -1);
        multiMove(rowIndex, colIndex, -1, 1);
        multiMove(rowIndex, colIndex, -1, -1);

        return validMoves;
    }

    public void multiMove(int row, int col, int rowInc, int colInc) {
        row += rowInc;
        col += colInc;
        while (inBounds(row, col)) {
            ChessPosition otherPosition = new ChessPosition(row, col);
            if (isValid(otherPosition)) {
                addToMoves(otherPosition);
            }
            if (board.getPiece(otherPosition) != null) {
                break;
            }
            row += rowInc;
            col += colInc;
        }
    }

    public boolean isValid(ChessPosition otherPosition) {
        if (board.getPiece(otherPosition) == null) {
            return true;
        }
        return !isTeamMate(otherPosition);
    }

    @Override
    public boolean inBounds(int row, int col) {
        return row > 0 && row < 9 && col > 0 && col < 9;
    }

    @Override
    public boolean isTeamMate(ChessPosition otherPosition) {
        return board.getPiece(myPosition).getTeamColor() == board.getPiece(otherPosition).getTeamColor();
    }

    @Override
    public void addToMoves(ChessPosition otherPosition) {
        ChessMove validMove = new ChessMove(myPosition, otherPosition, null);
        validMoves.add(validMove);
    }
}
