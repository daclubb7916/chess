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

    private ChessBoard board;
    private TeamColor teamTurn;

    public ChessGame() {
        this.board = new ChessBoard();
        this.teamTurn = TeamColor.WHITE;
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
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
        if (board.getPiece(startPosition) == null) {
            return null;
        }
        ChessPiece pieceToMove = board.getPiece(startPosition);
        teamTurn = pieceToMove.getTeamColor();
        Collection<ChessMove> totalMoves = pieceToMove.pieceMoves(board, startPosition);
        Collection<ChessMove> finalMoves = new ArrayList<>();
        for (ChessMove move : totalMoves) {
            if (!wouldResultInCheck(move)) {
                finalMoves.add(move);
            }
        }
        return finalMoves;
    }

    private void addToBoard(ChessMove move, ChessPiece piece) {
        if (move.getPromotionPiece() != null) {
            ChessPiece promotedPiece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
            board.addPiece(move.getEndPosition(), promotedPiece);
        } else {
            board.addPiece(move.getEndPosition(), piece);
        }
        board.addPiece(move.getStartPosition(), null);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> pieceMoves = validMoves(move.getStartPosition());
        if (pieceMoves == null) {
            throw new InvalidMoveException("No piece here");
        }
        ChessPiece pieceToMove = board.getPiece(move.getStartPosition());
        if (pieceToMove.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Not this team's turn");
        }

        boolean inPieceMoves = false;
        for (ChessMove validMove : pieceMoves) {
            if (validMove.equals(move)) {
                inPieceMoves = true;
                break;
            }
        }
        if (!inPieceMoves) {
            throw new InvalidMoveException("Not a valid move");
        }
        addToBoard(move, pieceToMove);

        if (teamTurn == TeamColor.WHITE) {
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
        }
    }

    private boolean kingInMoves(ChessPosition position) {
        ChessPiece chessPiece = board.getPiece(position);
        Collection<ChessMove> pieceMoves = chessPiece.pieceMoves(board, position);
        for (ChessMove move : pieceMoves) {
            ChessPiece endPiece = board.getPiece(move.getEndPosition());
            if (endPiece == null) {
                continue;
            }
            if (endPiece.getPieceType() == ChessPiece.PieceType.KING) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        for (int rowIndex = 1; rowIndex < 9; rowIndex++) {
            for (int colIndex = 1; colIndex < 9; colIndex++) {

                ChessPosition newPosition = new ChessPosition(rowIndex, colIndex);
                ChessPiece newPiece = board.getPiece(newPosition);
                if (newPiece == null) {
                    continue;
                }
                if (newPiece.getTeamColor() == teamColor) {
                    continue;
                }
                if (kingInMoves(newPosition)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean wouldResultInCheck(ChessMove testMove) {
        ChessBoard originalBoard = board;
        setBoard(originalBoard.clone());
        ChessPiece pieceToMove = board.getPiece(testMove.getStartPosition());
        addToBoard(testMove, pieceToMove);

        boolean inCheck = isInCheck(teamTurn);
        setBoard(originalBoard);
        return inCheck;
    }

    // private boolean allMovesResultInCheck(TeamColor teamColor) {

    // }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
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
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
