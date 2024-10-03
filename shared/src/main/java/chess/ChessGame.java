package chess;

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
        throw new RuntimeException("Not implemented");
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

        //clone whole Board
        ChessBoard clonedBoard = gameBoard.clone();
        if (move.getPromotionPiece() != null) {
            ChessPiece promotedPiece = new ChessPiece(pieceToMove.getTeamColor(), move.getPromotionPiece());
            clonedBoard.addPiece(move.getEndPosition(), promotedPiece);
        } else {
            clonedBoard.addPiece(move.getEndPosition(), pieceToMove);
        }
        clonedBoard.addPiece(move.getStartPosition(), null);
        if (clonedBoard.isInCheck(TeamTurn)) {
            throw new InvalidMoveException("this move would place your King in check");
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

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // if is in check and any move by team won't change that
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
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
