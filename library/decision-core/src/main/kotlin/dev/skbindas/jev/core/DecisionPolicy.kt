package dev.skbindas.jev.core

data class DecisionPolicy(
    val minimumConfidence: Double = 0.70,
    val minimumMargin: Double = 0.10
) {
    init {
        require(minimumConfidence in 0.0..1.0)
        require(minimumMargin in 0.0..1.0)
    }

    fun accept(choice: ChoiceDecision): Decision<String> {
        val selectedProbability = choice.probabilities[choice.key] ?: choice.confidence
        val runnerUp = choice.probabilities
            .asSequence()
            .filter { it.key != choice.key }
            .map { it.value }
            .maxOrNull()
            ?: 0.0

        if (selectedProbability !in 0.0..1.0 || choice.confidence !in 0.0..1.0 ||
            choice.probabilities.values.any { it !in 0.0..1.0 }) {
            return Decision.Abstained(
                reason = "decision_invalid_probability",
                confidence = null
            )
        }

        val confidenceOk = selectedProbability >= minimumConfidence
        val marginOk = choice.probabilities.isEmpty() ||
            (selectedProbability - runnerUp) >= minimumMargin

        return if (confidenceOk && marginOk) {
            Decision.Accepted(choice.key, selectedProbability)
        } else {
            Decision.Abstained(
                reason = if (!confidenceOk) {
                    "decision_confidence_below_threshold"
                } else {
                    "decision_margin_below_threshold"
                },
                confidence = selectedProbability
            )
        }
    }

    fun accept(score: ScoreDecision): Decision<Int> {
        if (score.score !in 2..10) {
            return Decision.Abstained(
                reason = "score_out_of_range",
                confidence = score.confidence
            )
        }
        if (score.confidence !in 0.0..1.0) {
            return Decision.Abstained(
                reason = "score_invalid_confidence",
                confidence = null
            )
        }

        return if (score.confidence >= minimumConfidence) {
            Decision.Accepted(score.score, score.confidence)
        } else {
            Decision.Abstained("score_confidence_below_threshold", score.confidence)
        }
    }

    fun accept(noul: NoulDecision): Decision<Boolean> {
        if (noul.probability !in 0.0..1.0) {
            return Decision.Abstained("noul_invalid_probability", null)
        }

        val probability = noul.probability
        val confidence = maxOf(probability, 1.0 - probability)

        return if (confidence >= minimumConfidence) {
            Decision.Accepted(probability >= 0.5, confidence)
        } else {
            Decision.Abstained("noul_confidence_below_threshold", confidence)
        }
    }
}
