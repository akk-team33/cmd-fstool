package de.team33.test.tools.io;

import de.team33.tools.io.StrictHashing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BeforeEachTest {

    private static final AtomicInteger NEXT_INDEX = new AtomicInteger(0);

    private int index;

    @BeforeEach
    final void beforeEach() {
        this.index = NEXT_INDEX.getAndIncrement();
    }

    @ParameterizedTest
    @EnumSource
    final void test(final StrictHashing value) {
        assertEquals(index, value.ordinal());
    }
}
