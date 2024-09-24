package chess;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface PieceMovesCalculator {
    Collection<ChessMove> pieceMoves();
    boolean inBounds(int row, int column);
    boolean teamPiece(ChessPosition finalPosition);
}

class KingMovesCalc implements PieceMovesCalculator {
    private final ChessBoard board;
    private final ChessPosition current_position;
    public KingMovesCalc(ChessBoard board, ChessPosition current_position) {
        this.board = board;
        this.current_position = current_position;
    }

    @Override
    public Collection<ChessMove> pieceMoves() {
        return List.of();
    }

    @Override
    public boolean inBounds(int row, int column) {
        return false;
    }

    @Override
    public boolean teamPiece(ChessPosition finalPosition) {
        return false;
    }
}

class QueenMovesCalc implements PieceMovesCalculator {
    private final ChessBoard board;
    private final ChessPosition current_position;
    public QueenMovesCalc(ChessBoard board, ChessPosition current_position) {
        this.board = board;
        this.current_position = current_position;
    }

    @Override
    public Collection<ChessMove> pieceMoves() {
        return List.of();
    }

    @Override
    public boolean inBounds(int row, int column) {
        return false;
    }

    @Override
    public boolean teamPiece(ChessPosition finalPosition) {
        return false;
    }
}

class KnightMovesCalc implements PieceMovesCalculator {
    private final ChessBoard board;
    private final ChessPosition current_position;
    public KnightMovesCalc(ChessBoard board, ChessPosition current_position) {
        this.board = board;
        this.current_position = current_position;
    }

    @Override
    public Collection<ChessMove> pieceMoves() {
        ArrayList<ChessMove> validMoves = new ArrayList<>();

        if (inBounds(current_position.getRow()+2, current_position.getColumn()+1)) {
            ChessPosition finalPosition = new ChessPosition(current_position.getRow()+2,
                    current_position.getColumn()+1);
            // optional if !teamPiece(finalPosition) statement possible
            ChessMove validMove = new ChessMove(current_position, finalPosition, null);
            validMoves.add(validMove);
        }

        if (inBounds(current_position.getRow()+2, current_position.getColumn()-1)) {
            ChessPosition finalPosition = new ChessPosition(current_position.getRow()+2,
                    current_position.getColumn()-1);
            ChessMove validMove = new ChessMove(current_position, finalPosition, null);
            validMoves.add(validMove);
        }

        if (inBounds(current_position.getRow()-2, current_position.getColumn()-1)) {
            ChessPosition finalPosition = new ChessPosition(current_position.getRow()-2,
                    current_position.getColumn()-1);
            ChessMove validMove = new ChessMove(current_position, finalPosition, null);
            validMoves.add(validMove);
        }

        if (inBounds(current_position.getRow()-2, current_position.getColumn()+1)) {
            ChessPosition finalPosition = new ChessPosition(current_position.getRow()-2,
                    current_position.getColumn()+1);
            ChessMove validMove = new ChessMove(current_position, finalPosition, null);
            validMoves.add(validMove);
        }

        if (inBounds(current_position.getRow()-1, current_position.getColumn()+2)) {
            ChessPosition finalPosition = new ChessPosition(current_position.getRow()-1,
                    current_position.getColumn()+2);
            ChessMove validMove = new ChessMove(current_position, finalPosition, null);
            validMoves.add(validMove);
        }

        if (inBounds(current_position.getRow()-1, current_position.getColumn()-2)) {
            ChessPosition finalPosition = new ChessPosition(current_position.getRow()-1,
                    current_position.getColumn()-2);
            ChessMove validMove = new ChessMove(current_position, finalPosition, null);
            validMoves.add(validMove);
        }

        if (inBounds(current_position.getRow()+1, current_position.getColumn()-2)) {
            ChessPosition finalPosition = new ChessPosition(current_position.getRow()+1,
                    current_position.getColumn()-2);
            ChessMove validMove = new ChessMove(current_position, finalPosition, null);
            validMoves.add(validMove);
        }

        if (inBounds(current_position.getRow()+1, current_position.getColumn()+2)) {
            ChessPosition finalPosition = new ChessPosition(current_position.getRow()+1,
                    current_position.getColumn()+2);
            ChessMove validMove = new ChessMove(current_position, finalPosition, null);
            validMoves.add(validMove);
        }
        return validMoves;
    }

    @Override
    public boolean inBounds(int row, int column) {
        return row > 0 && row < 9 && column > 0 && column < 9;
    }

    @Override
    public boolean teamPiece(ChessPosition finalPosition) {
        return false;
    }
}

class PawnMovesCalc implements PieceMovesCalculator {
    private final ChessBoard board;
    private final ChessPosition current_position;
    public PawnMovesCalc(ChessBoard board, ChessPosition current_position) {
        this.board = board;
        this.current_position = current_position;
    }

    @Override
    public Collection<ChessMove> pieceMoves() {
        return List.of();
    }

    @Override
    public boolean inBounds(int row, int column) {
        return false;
    }

    @Override
    public boolean teamPiece(ChessPosition finalPosition) {
        return false;
    }
}

class BishopMovesCalc implements PieceMovesCalculator {
    private final ChessBoard board;
    private final ChessPosition current_position;
    public BishopMovesCalc(ChessBoard board, ChessPosition current_position) {
        this.board = board;
        this.current_position = current_position;
    }

    @Override
    public Collection<ChessMove> pieceMoves() {
        ArrayList<ChessMove> validMoves = new ArrayList<>();

        int rowIndex = current_position.getRow();
        int colIndex = current_position.getColumn();
        while (inBounds(rowIndex+1, colIndex+1)) {
            rowIndex += 1;
            colIndex += 1;
            ChessPosition finalPosition = new ChessPosition(rowIndex, colIndex);
            // encounters a piece
            if (board.getPiece(finalPosition) != null) {
                // not a teammate
                if (!teamPiece(finalPosition)) {
                    ChessMove validMove = new ChessMove(current_position, finalPosition, null);
                    validMoves.add(validMove);
                }
                break;
            }
            ChessMove validMove = new ChessMove(current_position, finalPosition, null);
            validMoves.add(validMove);
        }

        rowIndex = current_position.getRow();
        colIndex = current_position.getColumn();
        while (inBounds(rowIndex-1, colIndex+1)) {
            rowIndex -= 1;
            colIndex += 1;
            ChessPosition finalPosition = new ChessPosition(rowIndex, colIndex);
            // encounters a piece
            if (board.getPiece(finalPosition) != null) {
                // not a teammate
                if (!teamPiece(finalPosition)) {
                    ChessMove validMove = new ChessMove(current_position, finalPosition, null);
                    validMoves.add(validMove);
                }
                break;
            }
            ChessMove validMove = new ChessMove(current_position, finalPosition, null);
            validMoves.add(validMove);
        }

        rowIndex = current_position.getRow();
        colIndex = current_position.getColumn();
        while (inBounds(rowIndex-1, colIndex-1)) {
            rowIndex -= 1;
            colIndex -= 1;
            ChessPosition finalPosition = new ChessPosition(rowIndex, colIndex);
            // encounters a piece
            if (board.getPiece(finalPosition) != null) {
                // not a teammate
                if (!teamPiece(finalPosition)) {
                    ChessMove validMove = new ChessMove(current_position, finalPosition, null);
                    validMoves.add(validMove);
                }
                break;
            }
            ChessMove validMove = new ChessMove(current_position, finalPosition, null);
            validMoves.add(validMove);
        }

        rowIndex = current_position.getRow();
        colIndex = current_position.getColumn();
        while (inBounds(rowIndex+1, colIndex-1)) {
            rowIndex += 1;
            colIndex -= 1;
            ChessPosition finalPosition = new ChessPosition(rowIndex, colIndex);
            // encounters a piece
            if (board.getPiece(finalPosition) != null) {
                // not a teammate
                if (!teamPiece(finalPosition)) {
                    ChessMove validMove = new ChessMove(current_position, finalPosition, null);
                    validMoves.add(validMove);
                }
                break;
            }
            ChessMove validMove = new ChessMove(current_position, finalPosition, null);
            validMoves.add(validMove);
        }
        return validMoves;
    }

    @Override
    public boolean inBounds(int row, int column) {
        return row > 0 && row < 9 && column > 0 && column < 9;
    }

    @Override
    public boolean teamPiece(ChessPosition finalPosition) {
        ChessPiece newPiece = board.getPiece(finalPosition);
        ChessPiece thisPiece = board.getPiece(current_position);
        return newPiece.getTeamColor() == thisPiece.getTeamColor();
    }
}

class RookMovesCalc implements PieceMovesCalculator {
    private final ChessBoard board;
    private final ChessPosition current_position;
    public RookMovesCalc(ChessBoard board, ChessPosition current_position) {
        this.board = board;
        this.current_position = current_position;
    }

    @Override
    public Collection<ChessMove> pieceMoves() {
        return List.of();
    }

    @Override
    public boolean inBounds(int row, int column) {
        return false;
    }

    @Override
    public boolean teamPiece(ChessPosition finalPosition) {
        return false;
    }
}