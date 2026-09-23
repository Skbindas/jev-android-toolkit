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

        val confidenceOk = maxOf(choice.confidence, selectedProbability) >= minimumConfidence
        val marginOk = (selectedProbability - runnerUp) >= minimumMargin

        return if (confidenceOk && marginOk) {
            Decision.Accepted(choice.key, maxOf(choice.confidence, selectedProbability))
        } else {
            Decision.Abstained(
                reason = "decision_quality_below_threshold",
                confidence = maxOf(choice.confidence, selectedProbability)
            )
        }
    }

    fun accept(score: ScoreDecision): Decision<Int> =
        if (score.confidence >= minimumConfidence) {
            Decision.Accepted(score.score, score.confidence)
        } else {
            Decision.Abstained("score_confidence_below_threshold", score.confidence)
        }

    fun accept(noul: NoulDecision): Decision<Boolean> {
        val probability = noul.probability.coerceIn(0.0, 1.0)
        return Decision.Accepted(probability >= 0.5, maxOf(probability, 1.0 - probability))
    }
}
