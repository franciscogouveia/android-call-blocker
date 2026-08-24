package eu.de_gouveia.callblocker

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ScreeningDecisionTest {
    @Test
    fun incomingCallIsBlocked() {
        assertEquals(ScreeningAction.BLOCK, screeningAction(CallDirection.INCOMING))
    }

    @Test
    fun outgoingCallIsIgnored() {
        assertEquals(ScreeningAction.IGNORE, screeningAction(CallDirection.OUTGOING))
    }

    @Test
    fun incomingDecisionCompletesWithinScreeningDeadline() {
        val startedAt = System.nanoTime()
        screeningAction(CallDirection.INCOMING)
        val elapsedMillis = (System.nanoTime() - startedAt) / 1_000_000

        assertTrue("Decision took ${elapsedMillis}ms", elapsedMillis < 5_000)
    }
}
