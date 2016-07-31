package io.chesslave.model;

import javaslang.control.Option;
import java.util.Objects;

/**
 * Factory of standard chess moves.
 */
public final class Movements {

    private Movements() {
    }

    /**
     * Moves a piece from a square to another square.
     */
    public static Regular regular(Square from, Square to) {
        return new Regular(from, to, false, Option.none());
    }

    /**
     * Moves a pawn capturing the opponent pawn en passant.
     */
    public static Regular enPassant(Square from, Square to) {
        return new Regular(from, to, true, Option.none());
    }

    /**
     * Moves a pawn from a square to another square and promotes it to the given piece type.
     */
    public static Regular promotion(Square from, Square to, Piece.Type type) {
        return new Regular(from, to, false, Option.some(type));
    }

    /**
     * Performs a castling on the king side of the specified color.
     */
    public static ShortCastling shortCastling(Color color) {
        return new ShortCastling(color);
    }

    /**
     * Performs a castling on the queen side of the specified color.
     */
    public static LongCastling longCastling(Color color) {
        return new LongCastling(color);
    }

    public static class Regular implements Move {

        public final Square from;
        public final Square to;
        public final boolean enPassant;
        public final Option<Piece.Type> promotion;

        public Regular(Square from, Square to, boolean enPassant, Option<Piece.Type> promotion) {
            this.from = from;
            this.to = to;
            this.enPassant = enPassant;
            this.promotion = promotion;
        }

        @Override
        public Position apply(Position position) {
            final Piece piece = position.at(from).get();
            final int direction = Pawns.direction(piece.color);
            final Position moved = position.move(from, to);
            final Position promoted = promotion.toSet().foldLeft(moved, (pos, type) -> pos.put(to, Piece.of(type, piece.color)));
            final Position captured = enPassant ? promoted.remove(to.translate(0, -direction).get()) : promoted;
            return captured;
        }

        @Override
        public String toString() {
            return new StringBuilder()
                    .append("Regular{from=").append(from)
                    .append(", to=").append(to)
                    .append(", enPassant=").append(enPassant)
                    .append(", promotion=").append(promotion).append("}")
                    .toString();
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other instanceof Regular == false) {
                return false;
            }
            final Regular that = (Regular) other;
            return this.enPassant == that.enPassant &&
                    Objects.equals(this.from, that.from) &&
                    Objects.equals(this.to, that.to) &&
                    Objects.equals(this.promotion, that.promotion);
        }

        @Override
        public int hashCode() {
            return Objects.hash(from, to, enPassant, promotion);
        }
    }

    public static class ShortCastling implements Move {

        public final Color color;

        private ShortCastling(Color color) {
            this.color = color;
        }

        @Override
        public Position apply(Position position) {
            final int row = color == Color.WHITE ? 0 : 7;
            return position
                    .move(new Square(4, row), new Square(6, row))
                    .move(new Square(7, row), new Square(5, row));
        }

        @Override
        public String toString() {
            return new StringBuilder()
                    .append("ShortCastling{color=").append(color).append("}")
                    .toString();
        }
    }

    public static class LongCastling implements Move {

        public final Color color;

        private LongCastling(Color color) {
            this.color = color;
        }

        @Override
        public Position apply(Position position) {
            final int row = color == Color.WHITE ? 0 : 7;
            return position
                    .move(new Square(4, row), new Square(2, row))
                    .move(new Square(0, row), new Square(3, row));
        }

        @Override
        public String toString() {
            return new StringBuilder()
                    .append("LongCastling{color=").append(color).append("}")
                    .toString();
        }
    }
}
