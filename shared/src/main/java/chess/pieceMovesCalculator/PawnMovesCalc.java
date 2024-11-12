package chess.pieceMovesCalculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalc implements PieceMovesCalculator {
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
