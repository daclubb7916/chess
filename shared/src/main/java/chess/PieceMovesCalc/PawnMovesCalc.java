package chess.PieceMovesCalc;

import chess.*;

import java.util.Collection;

public class PawnMovesCalc extends PieceMovesCalc {
    private final ChessGame.TeamColor pawnColor;

    public PawnMovesCalc(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
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

    private void pawnMoveWhite(int row, int col) {
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

    private void pawnMoveBlack(int row, int col) {
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

    private boolean isValidForward(ChessPosition otherPosition) {
        if (!inBounds(otherPosition.getRow(), otherPosition.getColumn())) {
            return false;
        }
        return board.getPiece(otherPosition) == null;
    }

    private boolean isValidAttack(ChessPosition otherPosition) {
        if (!inBounds(otherPosition.getRow(), otherPosition.getColumn())) {
            return false;
        }
        if (board.getPiece(otherPosition) == null) {
            return false;
        }
        return !isTeamMate(otherPosition);
    }

    private void addPromotedMoves(ChessPosition otherPosition) {
        ChessMove queenAdd = new ChessMove(myPosition, otherPosition, ChessPiece.PieceType.QUEEN);
        ChessMove knightAdd = new ChessMove(myPosition, otherPosition, ChessPiece.PieceType.KNIGHT);
        ChessMove bishopAdd = new ChessMove(myPosition, otherPosition, ChessPiece.PieceType.BISHOP);
        ChessMove rookAdd = new ChessMove(myPosition, otherPosition, ChessPiece.PieceType.ROOK);
        validMoves.add(queenAdd);
        validMoves.add(knightAdd);
        validMoves.add(bishopAdd);
        validMoves.add(rookAdd);
    }
}
