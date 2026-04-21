package aslframework.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link GestureGate}.
 *
 * GestureGate has a fixed threshold of 0.95. Tests verify pass/fail
 * boundary behaviour and the threshold accessor.
 */
class GestureGateTest {

    private GestureGate gate;

    @BeforeEach
    void setUp() {
        gate = new GestureGate();
    }

    // ── passes() ─────────────────────────────────────────────────────────────

    @Test
    void passes_exactThreshold_returnsTrue() {
        assertTrue(gate.passes(0.95));
    }

    @Test
    void passes_aboveThreshold_returnsTrue() {
        assertTrue(gate.passes(1.0));
    }

    @Test
    void passes_justBelowThreshold_returnsFalse() {
        assertFalse(gate.passes(0.94));
    }

    @Test
    void passes_zero_returnsFalse() {
        assertFalse(gate.passes(0.0));
    }

    @Test
    void passes_halfConfidence_returnsFalse() {
        assertFalse(gate.passes(0.5));
    }

    @Test
    void passes_justAboveThreshold_returnsTrue() {
        assertTrue(gate.passes(0.951));
    }

    // ── getThreshold() ────────────────────────────────────────────────────────

    @Test
    void getThreshold_returnsExpectedValue() {
        assertEquals(0.95, gate.getThreshold(), 1e-9);
    }

    @Test
    void getThreshold_matchesConstant() {
        assertEquals(GestureGate.CONFIDENCE_GATE, gate.getThreshold(), 1e-9);
    }
}
