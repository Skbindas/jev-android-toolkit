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
    fun choice_uses_selected_probability_when_probabilities_are_present() {
        val policy = DecisionPolicy(0.7, 0.05)

        val abstained = policy.accept(
            ChoiceDecision(
                key = "show",
                confidence = 0.99,
                probabilities = mapOf("show" to 0.52, "defer" to 0.45, "suppress" to 0.03)
            )
        )

        assertIs<Decision.Abstained>(abstained)
    }

    @Test
    fun noul_abstains_when_probability_is_too_close_to_half() {
        val policy = DecisionPolicy(0.7, 0.1)

        val abstained = policy.accept(NoulDecision(0.51))
        assertIs<Decision.Abstained>(abstained)

        val accepted = policy.accept(NoulDecision(0.85))
        assertEquals(true, (accepted as Decision.Accepted).value)
    }
}
