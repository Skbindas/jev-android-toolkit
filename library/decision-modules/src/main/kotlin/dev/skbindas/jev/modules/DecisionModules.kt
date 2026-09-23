package dev.skbindas.jev.modules

import dev.skbindas.jev.client.JevClient
import dev.skbindas.jev.core.Decision
import dev.skbindas.jev.core.DecisionPolicy
import dev.skbindas.jev.core.JevQuestions

enum class PaywallAction { SHOW, DEFER, SUPPRESS }

data class PaywallContext(
    val sessions: Int,
    val daysSinceInstall: Int,
    val hasSeenPaywall: Boolean,
    val isEntitled: Boolean
)

class PaywallDecider(
    private val jev: JevClient,
    private val policy: DecisionPolicy = DecisionPolicy()
) {
    suspend fun decide(context: PaywallContext): Decision<PaywallAction> {
        if (context.isEntitled) {
            return Decision.Accepted(PaywallAction.SUPPRESS, 1.0)
        }

        val raw = jev.choice(
            state = "sessions=" + context.sessions +
                " daysSinceInstall=" + context.daysSinceInstall +
                " hasSeenPaywall=" + context.hasSeenPaywall +
                " isEntitled=" + context.isEntitled,
            questionId = "paywall_action",
            instructions = "Choose the least intrusive next monetization UI action for this user.",
            criteria = mapOf(
                "show" to "Show the paywall now because timing and context justify an upsell.",
                "defer" to "Delay the paywall because the user should continue their current task.",
                "suppress" to "Do not show a paywall in this context."
            )
        ) ?: return Decision.Abstained("missing_jev_answer")

        return policy.accept(raw).mapValue {
            when (it) {
                "show" -> PaywallAction.SHOW
                "defer" -> PaywallAction.DEFER
                else -> PaywallAction.SUPPRESS
            }
        }
    }
}

enum class ModerationAction { ALLOW, REVIEW, REJECT }

data class ModerationContext(
    val text: String,
    val trustedAuthor: Boolean = false
)

class ModerationDecider(
    private val jev: JevClient,
    private val policy: DecisionPolicy = DecisionPolicy(
        minimumConfidence = 0.75,
        minimumMargin = 0.08
    )
) {
    suspend fun decide(context: ModerationContext): Decision<ModerationAction> {
        val raw = jev.choice(
            state = "text=" + context.text + "
trustedAuthor=" + context.trustedAuthor,
            questionId = "moderation_action",
            instructions = "Classify the content for a user-generated-content moderation workflow.",
            criteria = mapOf(
                "allow" to "No meaningful policy concern is present.",
                "review" to "Ambiguous or potentially harmful; route to human review.",
                "reject" to "Clear policy concern requiring refusal or blocking."
            )
        ) ?: return Decision.Abstained("missing_jev_answer")

        return policy.accept(raw).mapValue {
            when (it) {
                "allow" -> ModerationAction.ALLOW
                "review" -> ModerationAction.REVIEW
                else -> ModerationAction.REJECT
            }
        }
    }
}

enum class NotificationAction { DELIVER, DIGEST, SUPPRESS }

data class NotificationContext(
    val title: String,
    val body: String,
    val userActiveNow: Boolean,
    val priority: Int
)

class NotificationRouter(
    private val jev: JevClient,
    private val policy: DecisionPolicy = DecisionPolicy(0.70, 0.10)
) {
    suspend fun decide(context: NotificationContext): Decision<NotificationAction> {
        if (context.priority >= 5) {
            return Decision.Accepted(NotificationAction.DELIVER, 1.0)
        }

        val raw = jev.choice(
            state = "title=" + context.title +
                "
body=" + context.body +
                "
active=" + context.userActiveNow +
                "
priority=" + context.priority,
            questionId = "notification_action",
            instructions = "Choose the least disruptive notification route that still preserves useful information.",
            criteria = mapOf(
                "deliver" to "Deliver immediately.",
                "digest" to "Hold for the next digest.",
                "suppress" to "Do not notify."
            )
        ) ?: return Decision.Abstained("missing_jev_answer")

        return policy.accept(raw).mapValue {
            when (it) {
                "deliver" -> NotificationAction.DELIVER
                "digest" -> NotificationAction.DIGEST
                else -> NotificationAction.SUPPRESS
            }
        }
    }
}

data class SearchCandidate(
    val id: String,
    val title: String,
    val text: String
)

data class RankedCandidate(
    val candidate: SearchCandidate,
    val score: Int,
    val confidence: Double
)

class SemanticReranker(
    private val jev: JevClient,
    private val policy: DecisionPolicy = DecisionPolicy(0.60, 0.0)
) {
    suspend fun rerank(query: String, candidates: List<SearchCandidate>): List<RankedCandidate> {
        require(candidates.isNotEmpty()) { "At least one candidate is required." }
        require(candidates.size <= 50) { "Reranking is bounded to 50 candidates per call." }

        val state = buildString {
            appendLine("query=" + query)
            candidates.forEachIndexed { index, candidate ->
                appendLine(
                    "candidate_" + index +
                        " id=" + candidate.id +
                        " title=" + candidate.title +
                        " text=" + candidate.text
                )
            }
        }

        val questions = candidates.mapIndexed { index, _ ->
            "fit_" + index to JevQuestions.score(
                instructions = "Rate how well this candidate satisfies the search query on a 2-10 scale.",
                criteria = mapOf(
                    "2" to "Not relevant.",
                    "4" to "Weakly relevant.",
                    "6" to "Moderately relevant.",
                    "8" to "Strongly relevant.",
                    "10" to "Best fit for the query."
                )
            )
        }.toMap()

        val response = jev.evaluate(state, questions)
        return candidates.mapIndexedNotNull { index, candidate ->
            val raw = response.score("fit_" + index) ?: return@mapIndexedNotNull null
            val score = when (val decision = policy.accept(raw)) {
                is Decision.Accepted -> decision.value
                is Decision.Abstained -> raw.score
            }
            RankedCandidate(candidate, score, raw.confidence)
        }.sortedByDescending { it.score }
    }
}

class VerificationDecider(
    private val jev: JevClient,
    private val policy: DecisionPolicy = DecisionPolicy(0.80, 0.0)
) {
    suspend fun verify(expected: String, observed: String): Decision<Boolean> {
        val raw = jev.noul(
            state = "expected=" + expected + "
observed=" + observed,
            questionId = "verified",
            instructions = "Determine whether the observed outcome satisfies the expected outcome."
        ) ?: return Decision.Abstained("missing_jev_answer")

        return policy.accept(raw)
    }
}

private inline fun <T, R> Decision<T>.mapValue(transform: (T) -> R): Decision<R> = when (this) {
    is Decision.Accepted -> Decision.Accepted(transform(value), confidence)
    is Decision.Abstained -> this
}
