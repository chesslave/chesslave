package io.chesslave.model;

import io.chesslave.sugar.Ensure;

public class Board {

    public static Board standard = new Board(8);
    public final int size;

    public Board(int size) {
        Ensure.isTrue(size > 0, "cannot create a board with size %d", size);
        this.size = size;
    }

    public Square square(int col, int row) {
        return new Square(col, row);
    }

    public class Square {

        private final int col;
        private final int row;

        private Square(int col, int row) {
            Ensure.isTrue(0 <= col && col < size, "illegal column %d", col);
            Ensure.isTrue(0 <= row && row < size, "illegal row %d", row);
            this.col = col;
            this.row = row;
        }

        @Override
        public String toString() {
            return new StringBuilder()
                    .append((char) ('a' + col))
                    .append((char) ('1' + row))
                    .toString();
        }

        @Override
        public int hashCode() {
            return size * row + col;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs instanceof Square == false) {
                return false;
            }
            final Square other = (Square) rhs;
            return this.col == other.col && this.row == other.row;
        }
    }
}