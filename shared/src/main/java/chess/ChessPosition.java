package chess;

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
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

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

    public String toString() {
        String column;
        switch (col) {
            case 1 -> column = "A";
            case 2 -> column = "B";
            case 3 -> column = "C";
            case 4 -> column = "D";
            case 5 -> column = "E";
            case 6 -> column = "F";
            case 7 -> column = "G";
            case 8 -> column = "H";
            default -> column = "error";
        }

        String rowString;
        switch (row) {
            case 1 -> rowString = "1";
            case 2 -> rowString = "2";
            case 3 -> rowString = "3";
            case 4 -> rowString = "4";
            case 5 -> rowString = "5";
            case 6 -> rowString = "6";
            case 7 -> rowString = "7";
            case 8 -> rowString = "8";
            default -> rowString = "error";
        }
        return column + rowString;
    }
}
