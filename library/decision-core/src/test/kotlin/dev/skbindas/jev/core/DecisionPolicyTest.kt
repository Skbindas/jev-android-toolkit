package dev.skbindas.jev.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DecisionPolicyTest {
    @Test
    fun choice_requires_margin_and_confidence() {
        val policy = DecisionPolicy(0.7, 0.1)

        val accepted = policy.accept(
            ChoiceDecision(
                key = "show",
                confidence = 0.92,
                probabilities = mapOf("show" to 0.92, "defer" to 0.05, "suppress" to 0.03)
            )
        )
        assertIs<Decision.Accepted<String>>(accepted)

        val abstained = policy.accept(
            ChoiceDecision(
                key = "show",
                confidence = 0.71,
                probabilities = mapOf("show" to 0.71, "defer" to 0.66, "suppress" to 0.01)
            )
        )
        assertIs<Decision.Abstained>(abstained)
    }

    @Test
    fun noul_has_an_explicit_binary_boundary() {
        val policy = DecisionPolicy()
        assertEquals(true, (policy.accept(NoulDecision(0.8)) as Decision.Accepted).value)
        assertEquals(false, (policy.accept(NoulDecision(0.2)) as Decision.Accepted).value)
    }
}
