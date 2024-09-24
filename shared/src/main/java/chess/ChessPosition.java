package chess;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row;
    private final int col;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChessPosition that = (ChessPosition) o;
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        int result = row;
        result = 31 * result + col;
        return result;
    }

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;

    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }

    @Override
    public String toString() {
        Map<Integer, String> alphaNum = new HashMap<>();
        alphaNum.put(1, "a");
        alphaNum.put(2, "b");
        alphaNum.put(3, "c");
        alphaNum.put(4, "d");
        alphaNum.put(5, "e");
        alphaNum.put(6, "f");
        alphaNum.put(7, "g");
        alphaNum.put(8, "h");
        return "{" + alphaNum.get(row) + ", " + col + "}";
    }

}
