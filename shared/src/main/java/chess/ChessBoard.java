package chess;

import java.util.Arrays;

import static chess.EscapeSequences.*;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Cloneable {

    private ChessPiece[][] squares = new ChessPiece[8][8];

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow() - 1][position.getColumn() - 1];
    }

    private void createAndAddPiece(ChessGame.TeamColor color, ChessPiece.PieceType type, int row, int col) {
        ChessPiece piece = new ChessPiece(color, type);
        ChessPosition position = new ChessPosition(row, col);
        addPiece(position, piece);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        createAndAddPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK, 1, 1);
        createAndAddPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK, 1, 8);
        createAndAddPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT, 1, 2);
        createAndAddPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT, 1, 7);
        createAndAddPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP, 1, 3);
        createAndAddPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP, 1, 6);
        createAndAddPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN, 1, 4);
        createAndAddPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING, 1, 5);

        for (int i = 1; i < 9; i++) {
            createAndAddPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN, 2, i);
        }

        createAndAddPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK, 8, 1);
        createAndAddPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK, 8, 8);
        createAndAddPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT, 8, 2);
        createAndAddPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT, 8, 7);
        createAndAddPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP, 8, 3);
        createAndAddPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP, 8, 6);
        createAndAddPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN, 8, 4);
        createAndAddPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING, 8, 5);

        for (int i = 1; i < 9; i++) {
            createAndAddPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN, 7, i);
        }
    }

    @Override
    public ChessBoard clone() {
        try {
            ChessBoard clone = (ChessBoard) super.clone();

            ChessPiece[][] clonedSquares = new ChessPiece[8][8];
            for (int i = 0; i < 8; i++) {
                System.arraycopy(squares[i], 0, clonedSquares[i], 0, 8);
            }

            clone.squares = clonedSquares;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public String stringBoard(ChessGame.TeamColor color) {
        if (color == ChessGame.TeamColor.WHITE) {
            return whiteChessBoard();
        }
        return blackChessBoard();
    }

    private String whiteChessBoard() {
        StringBuilder stringBuilder = new StringBuilder("\n");
        stringBuilder.append(whiteAlpha());
        for (int i = 8; i > 0; i--) {
            stringBuilder.append(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK);
            stringBuilder.append(" ").append(i).append(" ");
            stringBuilder.append(whiteRow(i - 1));
            stringBuilder.append(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK);
            stringBuilder.append(" ").append(i).append(" ").append(RESET_BG_COLOR);
            stringBuilder.append("\n");
        }
        stringBuilder.append(whiteAlpha());
        return stringBuilder.toString();
    }

    private String whiteAlpha() {
        String[] columns = {" ", "a", "b", "c", "d", "e", "f", "g", "h", " "};
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK);
        for (int i = 0; i < 10; i++) {
            stringBuilder.append(" ").append(columns[i]).append(" ");
        }
        stringBuilder.append(RESET_BG_COLOR + RESET_TEXT_COLOR).append('\n');
        return stringBuilder.toString();
    }

    private String whiteRow(int row) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int col = 0; col < 8; col++) {
            if ((row + col) % 2 == 1) {
                stringBuilder.append(SET_BG_COLOR_WHITE);
            } else {
                stringBuilder.append(SET_BG_COLOR_BLACK);
            }

            ChessPiece piece = squares[row][col]; // This might throw an error if null
            stringBuilder.append(printPiece(piece));
        }
        return stringBuilder.toString();
    }

    private String printPiece(ChessPiece piece) {
        StringBuilder stringBuilder = new StringBuilder();

        if (piece == null) {
            stringBuilder.append(EMPTY);

        } else {

            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                stringBuilder.append(SET_TEXT_COLOR_RED);
                switch (piece.getPieceType()) {
                    case ChessPiece.PieceType.PAWN -> stringBuilder.append(WHITE_PAWN);
                    case ChessPiece.PieceType.ROOK -> stringBuilder.append(WHITE_ROOK);
                    case ChessPiece.PieceType.BISHOP -> stringBuilder.append(WHITE_BISHOP);
                    case ChessPiece.PieceType.KNIGHT -> stringBuilder.append(WHITE_KNIGHT);
                    case ChessPiece.PieceType.KING -> stringBuilder.append(WHITE_KING);
                    case ChessPiece.PieceType.QUEEN -> stringBuilder.append(WHITE_QUEEN);
                    default -> stringBuilder.append(EMPTY);
                }
            } else {
                stringBuilder.append(SET_TEXT_COLOR_BLUE);
                switch (piece.getPieceType()) {
                    case ChessPiece.PieceType.PAWN -> stringBuilder.append(BLACK_PAWN);
                    case ChessPiece.PieceType.ROOK -> stringBuilder.append(BLACK_ROOK);
                    case ChessPiece.PieceType.BISHOP -> stringBuilder.append(BLACK_BISHOP);
                    case ChessPiece.PieceType.KNIGHT -> stringBuilder.append(BLACK_KNIGHT);
                    case ChessPiece.PieceType.KING -> stringBuilder.append(BLACK_KING);
                    case ChessPiece.PieceType.QUEEN -> stringBuilder.append(BLACK_QUEEN);
                    default -> stringBuilder.append(EMPTY);
                }
            }

        }

        return stringBuilder.toString();
    }

    private String blackChessBoard() {
        StringBuilder stringBuilder = new StringBuilder("\n");
        stringBuilder.append(blackAlpha());
        for (int i = 1; i < 9; i++) {
            stringBuilder.append(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK);
            stringBuilder.append(" ").append(i).append(" ");
            stringBuilder.append(blackRow(i - 1));
            stringBuilder.append(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK);
            stringBuilder.append(" ").append(i).append(" ").append(RESET_BG_COLOR);
            stringBuilder.append("\n");
        }
        stringBuilder.append(blackAlpha());

        return stringBuilder.toString();
    }

    private String blackAlpha() {
        String[] columns = {" ", "a", "b", "c", "d", "e", "f", "g", "h", " "};
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK);
        for (int i = 9; i > -1; i--) {
            stringBuilder.append(" ").append(columns[i]).append(" ");
        }
        stringBuilder.append(RESET_BG_COLOR + RESET_TEXT_COLOR).append('\n');
        return stringBuilder.toString();
    }

    private String blackRow(int row) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int col = 7; col > -1; col --) {
            if ((row + col) % 2 == 0) {
                stringBuilder.append(SET_BG_COLOR_WHITE);
            } else {
                stringBuilder.append(SET_BG_COLOR_BLACK);
            }

            ChessPiece piece = squares[row][col]; // This might throw an error if null
            stringBuilder.append(printPiece(piece));
        }
        return stringBuilder.toString();
    }
}
