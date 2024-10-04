package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor TeamTurn;
    private ChessBoard gameBoard;

    public ChessGame() {
        this.TeamTurn = TeamColor.WHITE;
        this.gameBoard = new ChessBoard();
        gameBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return TeamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        TeamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (gameBoard.getPiece(startPosition) == null) {
            return null;
        }
        ChessPiece pieceToMove = gameBoard.getPiece(startPosition);
        TeamTurn = pieceToMove.getTeamColor();
        Collection<ChessMove> totalMoves = pieceToMove.pieceMoves(gameBoard, startPosition);
        Collection<ChessMove> finalMoves = new ArrayList<>();
        for (ChessMove move : totalMoves) {
            if (!wouldResultInCheck(move)) {
                finalMoves.add(move);
            }
        }
        return finalMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

        if (gameBoard.getPiece(move.getStartPosition()) == null) {
            throw new InvalidMoveException("No piece here");
        }

        ChessPiece pieceToMove = gameBoard.getPiece(move.getStartPosition());
        if (pieceToMove.getTeamColor() != TeamTurn) {
            throw new InvalidMoveException("Not this team's turn");
        }

        Collection<ChessMove> pieceMoves = pieceToMove.pieceMoves(gameBoard, move.getStartPosition());
        boolean isInPieceMoves = false;
        for (ChessMove validMove : pieceMoves) {
            if (validMove.equals(move)) {
                isInPieceMoves = true;
                break;
            }
        }

        if (!isInPieceMoves) {
            throw new InvalidMoveException("Not a valid move");
        }

        if (wouldResultInCheck(move)) {
            throw new InvalidMoveException("This move would place your King in check");
        }

        if (move.getPromotionPiece() != null) {
            ChessPiece promotedPiece = new ChessPiece(pieceToMove.getTeamColor(), move.getPromotionPiece());
            gameBoard.addPiece(move.getEndPosition(), promotedPiece);
        } else {
            gameBoard.addPiece(move.getEndPosition(), pieceToMove);
        }
        gameBoard.addPiece(move.getStartPosition(), null);

        if (TeamTurn == TeamColor.WHITE) {
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return gameBoard.isInCheck(teamColor);
    }

    private boolean wouldResultInCheck(ChessMove testMove) {
        ChessBoard clonedBoard = gameBoard.clone();
        ChessPiece pieceToMove = clonedBoard.getPiece(testMove.getStartPosition());
        if (testMove.getPromotionPiece() != null) {
            ChessPiece promotedPiece = new ChessPiece(pieceToMove.getTeamColor(), testMove.getPromotionPiece());
            clonedBoard.addPiece(testMove.getEndPosition(), promotedPiece);
        } else {
            clonedBoard.addPiece(testMove.getEndPosition(), pieceToMove);
        }
        clonedBoard.addPiece(testMove.getStartPosition(), null);

        return clonedBoard.isInCheck(TeamTurn);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!gameBoard.isInCheck(teamColor)) {
            return false;
        }
        // every move you could make will result in check
        for (int rowIndex = 1; rowIndex < 9; rowIndex++) {
            for (int colIndex = 1; colIndex < 9; colIndex++) {

                ChessPosition newPosition = new ChessPosition(rowIndex, colIndex);
                if (gameBoard.getPiece(newPosition) == null) {
                    continue;
                }

                if (gameBoard.getPiece(newPosition).getTeamColor() != teamColor) {
                    continue;
                }

                if (!validMoves(newPosition).isEmpty()) {
                    return false;
                }

            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (gameBoard.isInCheck(teamColor)) {
            return false;
        }
        // every move you could make will result in check
        for (int rowIndex = 1; rowIndex < 9; rowIndex++) {
            for (int colIndex = 1; colIndex < 9; colIndex++) {

                ChessPosition newPosition = new ChessPosition(rowIndex, colIndex);
                if (gameBoard.getPiece(newPosition) == null) {
                    continue;
                }

                if (gameBoard.getPiece(newPosition).getTeamColor() != teamColor) {
                    continue;
                }

                if (!validMoves(newPosition).isEmpty()) {
                    return false;
                }

            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }
}
