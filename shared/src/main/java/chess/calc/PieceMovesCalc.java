package chess.calc;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public abstract class PieceMovesCalc {
    public final ChessBoard board;
    public final ChessPosition myPosition;
    public final ArrayList<ChessMove> validMoves = new ArrayList<>();

    public PieceMovesCalc(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
    }

    public boolean inBounds(int row, int col) {
        return row > 0 && row < 9 && col > 0 && col < 9;
    }

    public boolean isTeamMate(ChessPosition otherPosition) {
        return board.getPiece(myPosition).getTeamColor() == board.getPiece(otherPosition).getTeamColor();
    }

    public void addToMoves(ChessPosition otherPosition) {
        ChessMove validMove = new ChessMove(myPosition, otherPosition, null);
        validMoves.add(validMove);
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

    public void singleMove(int row, int col) {
        ChessPosition otherPosition = new ChessPosition(row, col);
        if (isValid(otherPosition)) {
            addToMoves(otherPosition);
        }
    }

    public void multiMove(int rowInc, int colInc) {
        int row = myPosition.getRow() + rowInc;
        int col = myPosition.getColumn() + colInc;
        ChessPosition otherPosition = new ChessPosition(row, col);
        while (isValid(otherPosition)) {
            addToMoves(otherPosition);
            if (board.getPiece(otherPosition) != null) {
                break;
            }
            row += rowInc;
            col += colInc;
            otherPosition = new ChessPosition(row, col);
        }
    }

    public abstract Collection<ChessMove> pieceMoves();
}
