package io.chesslave.hands;

import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;

import io.chesslave.model.Square;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.awt.Desktop;
import java.awt.Point;

public class FailingClickBotMoverTest {

    @Mock
    private Pointer pointer;

    @InjectMocks
    private ClickBotMover botMover;

    @Before
    public void setUp() {
        assumeTrue(Desktop.isDesktopSupported());
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = RuntimeException.class)
    public void moveTest() {
        doThrow(RuntimeException.class).when(pointer).click(any(Point.class));
        botMover.move(Square.of("a1"), Square.of("b3"));
        fail("Move should fail");
    }
}