package dev.skbindas.jev.benchmark

import dev.skbindas.jev.core.ChoiceDecision
import dev.skbindas.jev.core.DecisionPolicy
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

@Serializable
private data class ChoiceFixture(
    val name: String,
    val key: String,
    val confidence: Double,
    val probabilities: Map<String, Double>,
    val expected: String
)

private const val WARMUP_ITERATIONS = 1_000
private const val MEASURED_ITERATIONS = 10_000

fun main() {
    val fixtures = Json.decodeFromString<List<ChoiceFixture>>(
        requireNotNull(object {}.javaClass.getResource("/decision-policy-fixtures.json"))
            .readText()
    )
    val policy = DecisionPolicy(0.70, 0.10)

    println("name,iterations,accepted_count,total_ms,average_ns,result")
    fixtures.forEach { fixture ->
        val decision = ChoiceDecision(
            key = fixture.key,
            confidence = fixture.confidence,
            probabilities = fixture.probabilities
        )

        repeat(WARMUP_ITERATIONS) { policy.accept(decision) }
        var accepted = 0
        val start = System.nanoTime()
        repeat(MEASURED_ITERATIONS) {
            if (policy.accept(decision) is dev.skbindas.jev.core.Decision.Accepted<*>) {
                accepted++
            }
        }
        val elapsed = System.nanoTime() - start
        val result = if (accepted == MEASURED_ITERATIONS) "accepted" else "abstained"
        val totalMs = TimeUnit.NANOSECONDS.toMicros(elapsed) / 1000.0
        val averageNs = elapsed / MEASURED_ITERATIONS
        check(result == fixture.expected) {
            "Fixture ${fixture.name} expected ${fixture.expected}, got $result"
        }
        println("${fixture.name},$MEASURED_ITERATIONS,$accepted,$totalMs,$averageNs,$result")
    }
}
