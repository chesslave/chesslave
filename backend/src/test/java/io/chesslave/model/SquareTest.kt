package io.chesslave.model

import javaslang.collection.List
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.hasItem
import org.junit.Assert.*
import org.junit.Test

class SquareTest {

    // TODO: split in different tests
    @Test
    fun constructorIllegalArgumentsTest() {
        try {
            Square(-1, 0)
            fail("Should fail when column index is < 0")
        } catch (ex: IllegalArgumentException) {
            assertThat(ex.message, containsString("column -1"))
        }

        try {
            Square(Board.SIZE, 0)
            fail("Should fail when column index >= board size")
        } catch (ex: IllegalArgumentException) {
            assertThat(ex.message, containsString("column " + Board.SIZE))
        }

        try {
            Square(0, -1)
            fail("Should fail when row index is < 0")
        } catch (ex: IllegalArgumentException) {
            assertThat(ex.message, containsString("row -1"))
        }

        try {
            Square(0, Board.SIZE)
            fail("Should fail when row index >= board size")
        } catch (ex: IllegalArgumentException) {
            assertThat(ex.message, containsString("row " + Board.SIZE))
        }

    }

    // TODO: split is different tests
    @Test
    fun factoryMethodIllegalArgumentsTest() {
        try {
            Square.of("")
            fail("Should fail when coordinates are empty")
        } catch (ex: IllegalArgumentException) {
            assertThat(ex.message, containsString("coordinate"))
        }

        try {
            Square.of("a")
            fail("Should fail when coordinates are incomplete")
        } catch (ex: IllegalArgumentException) {
            assertThat(ex.message, containsString("coordinate a"))
        }

        try {
            Square.of("a4c")
            fail("Should fail when coordinates are exceeded")
        } catch (ex: IllegalArgumentException) {
            assertThat(ex.message, containsString("coordinate a4c"))
        }

        try {
            Square.of("i3")
            fail("Should fail when column is out of the board")
        } catch (ex: IllegalArgumentException) {
            assertThat(ex.message, containsString("column"))
        }

        try {
            Square.of("h0")
            fail("Should fail when row is out of the board")
        } catch (ex: IllegalArgumentException) {
            assertThat(ex.message, containsString("row"))
        }
    }

    @Test
    fun nameTest() {
        val a1 = Square.of("a1")
        assertEquals("a1", a1.name())
        val g6 = Square.of("G6")
        assertEquals("g6", g6.name())
        val f3 = Square(5, 2)
        assertEquals("f3", f3.name())
    }

    @Test
    fun columnNameTest() {
        val a1 = Square.of("a1")
        assertEquals("a", a1.columnName())
        val g6 = Square.of("G6")
        assertEquals("g", g6.columnName())
        val f3 = Square(5, 2)
        assertEquals("f", f3.columnName())
    }

    @Test
    fun rowNameTest() {
        val a1 = Square.of("a1")
        assertEquals("1", a1.rowName())
        val g6 = Square.of("G6")
        assertEquals("6", g6.rowName())
        val f3 = Square(5, 2)
        assertEquals("3", f3.rowName())
    }

    @Test
    fun translateTest() {
        val a1MovedToC2 = Square.of("a1").translate(2, 1)
        assertTrue(a1MovedToC2.isDefined)
        assertEquals(Square.of("c2"), a1MovedToC2.get())

        val g6MovedToG1 = Square.of("g6").translate(0, -5)
        assertTrue(g6MovedToG1.isDefined)
        assertEquals(Square.of("g1"), g6MovedToG1.get())

        val h8MovedOutOfBoard = Square.of("h8").translate(1, 0)
        assertTrue(h8MovedOutOfBoard.isEmpty)

        val e4MovedOutOfBoard = Square.of("e4").translate(-2, -5)
        assertTrue(e4MovedOutOfBoard.isEmpty)
    }

    @Test
    fun translateAllTest() {
        val a1 = Square.of("a1")
        val a1Translations = a1.translateAll(
            Pair(2, 1),
            Pair(7, 7),
            Pair(0, 3),
            Pair(-1, 0),
            Pair(0, -1))
        assertEquals(3, a1Translations.size().toLong())
        assertThat(a1Translations, hasItem(Square.of("c2")))
        assertThat(a1Translations, hasItem(Square.of("h8")))
        assertThat(a1Translations, hasItem(Square.of("a4")))
    }

    @Test
    fun walkTest() {
        val a1 = Square.of("a1")
        val a1Walk = a1.walk(1, 1)
        assertEquals(7, a1Walk.size().toLong())
        assertThat(a1Walk, hasItem(Square.of("b2")))
        assertThat(a1Walk, hasItem(Square.of("c3")))
        assertThat(a1Walk, hasItem(Square.of("d4")))
        assertThat(a1Walk, hasItem(Square.of("e5")))
        assertThat(a1Walk, hasItem(Square.of("f6")))
        assertThat(a1Walk, hasItem(Square.of("g7")))
        assertThat(a1Walk, hasItem(Square.of("h8")))

        val e4 = Square.of("e4")
        val e4Walk = e4.walk(-2, 1)
        assertEquals(2, e4Walk.size().toLong())
        assertThat(e4Walk, hasItem(Square.of("c5")))
        assertThat(e4Walk, hasItem(Square.of("a6")))
    }

    @Test
    fun allTest() {
        val allSquares = Square.all()
        assertEquals(64, allSquares.size().toLong())
        List.range(0, Board.SIZE)
            .crossProduct(List.range(0, Board.SIZE))
            .forEach { assertThat(allSquares, hasItem(Square(it._1, it._2))) }
    }

    @Test
    fun equalsTest() {
        val e1Square = Square.of("e1")
        assertTrue(e1Square == e1Square)
        assertFalse(e1Square == Any())
        assertTrue(e1Square == Square.of("e1"))
    }

    @Test
    fun hashCodeTest() {
        val e1Square = Square.of("e1")
        val e1SquareHash = e1Square.hashCode()
        assertEquals(e1SquareHash.toLong(), e1Square.hashCode().toLong())
        assertNotEquals(e1SquareHash.toLong(), Any().hashCode().toLong())
        assertEquals(e1SquareHash.toLong(), Square.of("e1").hashCode().toLong())
    }
}
