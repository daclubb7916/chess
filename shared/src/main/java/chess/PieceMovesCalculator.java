package chess;

import java.util.ArrayList;
import java.util.Collection;

public interface PieceMovesCalculator {
    Collection<ChessMove> pieceMoves();
    boolean inBounds(int row, int col);
    boolean isTeamMate(ChessPosition otherPosition);
    void addToMoves(ChessPosition otherPosition);
}

class KingMovesCalc implements PieceMovesCalculator {
    private final ChessBoard board;
    private final ChessPosition myPosition;
    private ArrayList<ChessMove> validMoves = new ArrayList<>();

    public KingMovesCalc(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
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

    public void singleMove(int row, int col) {
        ChessPosition otherPosition = new ChessPosition(row, col);
        if (isValid(otherPosition)) {
            addToMoves(otherPosition);
        }
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

class QueenMovesCalc implements PieceMovesCalculator {
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

class BishopMovesCalc implements PieceMovesCalculator {
    private final ChessBoard board;
    private final ChessPosition myPosition;
    private ArrayList<ChessMove> validMoves = new ArrayList<>();

    public BishopMovesCalc(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
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

class KnightMovesCalc implements PieceMovesCalculator {
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

class RookMovesCalc implements PieceMovesCalculator {
    private final ChessBoard board;
    private final ChessPosition myPosition;
    private ArrayList<ChessMove> validMoves = new ArrayList<>();

    public RookMovesCalc(ChessBoard board, ChessPosition myPosition) {
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

class PawnMovesCalc implements PieceMovesCalculator {
    private final ChessBoard board;
    private final ChessPosition myPosition;
    private final ChessGame.TeamColor pawnColor;
    private ArrayList<ChessMove> validMoves = new ArrayList<>();

    public PawnMovesCalc(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
        this.pawnColor = board.getPiece(myPosition).getTeamColor();
    }

    @Override
    public Collection<ChessMove> pieceMoves() {

        int rowIndex = myPosition.getRow();
        int colIndex = myPosition.getColumn();

        if (pawnColor == ChessGame.TeamColor.WHITE) {
            pawnMoveWhite(rowIndex, colIndex);
        }
        if (pawnColor == ChessGame.TeamColor.BLACK) {
            pawnMoveBlack(rowIndex, colIndex);
        }
        return validMoves;
    }

    public void pawnMoveWhite(int row, int col) {
        ChessPosition otherPosition = new ChessPosition(row+1, col);
        if (isValidForward(otherPosition)) {
            if (row == 7) {
                addPromotedMoves(otherPosition);
            } else {
                addToMoves(otherPosition);
                if (row == 2) {
                    otherPosition = new ChessPosition(row+2, col);
                    if (isValidForward(otherPosition)) {
                        addToMoves(otherPosition);
                    }
                }
            }
        }

        otherPosition = new ChessPosition(row+1, col+1);
        if (isValidAttack(otherPosition)) {
            if (row == 7) {
                addPromotedMoves(otherPosition);
            } else {
                addToMoves(otherPosition);
            }
        }

        otherPosition = new ChessPosition(row+1, col-1);
        if (isValidAttack(otherPosition)) {
            if (row == 7) {
                addPromotedMoves(otherPosition);
            } else {
                addToMoves(otherPosition);
            }
        }
    }

    public void pawnMoveBlack(int row, int col) {
        ChessPosition otherPosition = new ChessPosition(row-1, col);
        if (isValidForward(otherPosition)) {
            if (row == 2) {
                addPromotedMoves(otherPosition);
            } else {
                addToMoves(otherPosition);
                if (row == 7) {
                    otherPosition = new ChessPosition(row-2, col);
                    if (isValidForward(otherPosition)) {
                        addToMoves(otherPosition);
                    }
                }
            }
        }

        otherPosition = new ChessPosition(row-1, col+1);
        if (isValidAttack(otherPosition)) {
            if (row == 2) {
                addPromotedMoves(otherPosition);
            } else {
                addToMoves(otherPosition);
            }
        }

        otherPosition = new ChessPosition(row-1, col-1);
        if (isValidAttack(otherPosition)) {
            if (row == 2) {
                addPromotedMoves(otherPosition);
            } else {
                addToMoves(otherPosition);
            }
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

    public boolean isValidForward(ChessPosition otherPosition) {
        if (!inBounds(otherPosition.getRow(), otherPosition.getColumn())) {
            return false;
        }
        return board.getPiece(otherPosition) == null;
    }

    public boolean isValidAttack(ChessPosition otherPosition) {
        if (!inBounds(otherPosition.getRow(), otherPosition.getColumn())) {
            return false;
        }
        if (board.getPiece(otherPosition) == null) {
            return false;
        }
        return !isTeamMate(otherPosition);
    }

    public void addPromotedMoves(ChessPosition otherPosition) {
        ChessMove queenAdd = new ChessMove(myPosition, otherPosition, ChessPiece.PieceType.QUEEN);
        ChessMove knightAdd = new ChessMove(myPosition, otherPosition, ChessPiece.PieceType.KNIGHT);
        ChessMove bishopAdd = new ChessMove(myPosition, otherPosition, ChessPiece.PieceType.BISHOP);
        ChessMove rookAdd = new ChessMove(myPosition, otherPosition, ChessPiece.PieceType.ROOK);
        validMoves.add(queenAdd);
        validMoves.add(knightAdd);
        validMoves.add(bishopAdd);
        validMoves.add(rookAdd);
    }

    @Override
    public void addToMoves(ChessPosition otherPosition) {
        ChessMove validMove = new ChessMove(myPosition, otherPosition, null);
        validMoves.add(validMove);
    }
}