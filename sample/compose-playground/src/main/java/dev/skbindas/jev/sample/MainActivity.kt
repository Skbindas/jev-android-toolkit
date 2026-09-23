package dev.skbindas.jev.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.skbindas.jev.client.JevClient
import dev.skbindas.jev.client.JevTransport
import dev.skbindas.jev.core.Decision
import dev.skbindas.jev.core.DecisionPolicy
import dev.skbindas.jev.core.JevResponse
import dev.skbindas.jev.modules.ModerationContext
import dev.skbindas.jev.modules.ModerationDecider
import dev.skbindas.jev.modules.NotificationContext
import dev.skbindas.jev.modules.NotificationRouter
import dev.skbindas.jev.modules.PaywallContext
import dev.skbindas.jev.modules.PaywallDecider
import dev.skbindas.jev.modules.RankingStatus
import dev.skbindas.jev.modules.SearchCandidate
import dev.skbindas.jev.modules.SemanticReranker
import dev.skbindas.jev.modules.VerificationDecider
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                Playground()
            }
        }
    }
}

@Composable
private fun Playground() {
    val scope = rememberCoroutineScope()
    var status by remember { mutableStateOf("Ready") }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Jev Android Toolkit", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Offline Compose playground for bounded, typed semantic decisions.",
                style = MaterialTheme.typography.bodyLarge
            )

            Text("Paywall", style = MaterialTheme.typography.titleMedium)
            Button(onClick = {
                scope.launch {
                    val client = fakeClient(
                        "paywall_action" to buildJsonObject {
                            put("choice", "defer")
                            put("confidence", 0.91)
                            put("probabilities", buildJsonObject {
                                put("show", 0.06)
                                put("defer", 0.91)
                                put("suppress", 0.03)
                            })
                        }
                    )
                    val result = PaywallDecider(client, DecisionPolicy(0.70, 0.10))
                        .decide(PaywallContext(3, 2, false, false))
                    status = formatDecision("Paywall", result)
                }
            }) { Text("Run paywall demo") }

            Text("Moderation", style = MaterialTheme.typography.titleMedium)
            Button(onClick = {
                scope.launch {
                    val client = fakeClient(
                        "moderation_action" to buildJsonObject {
                            put("choice", "review")
                            put("confidence", 0.93)
                            put("probabilities", buildJsonObject {
                                put("allow", 0.04)
                                put("review", 0.93)
                                put("reject", 0.03)
                            })
                        }
                    )
                    val result = ModerationDecider(client)
                        .decide(ModerationContext("borderline content"))
                    status = formatDecision("Moderation", result)
                }
            }) { Text("Run moderation demo") }

            Text("Notifications", style = MaterialTheme.typography.titleMedium)
            Button(onClick = {
                scope.launch {
                    val client = fakeClient(
                        "notification_action" to buildJsonObject {
                            put("choice", "digest")
                            put("confidence", 0.88)
                            put("probabilities", buildJsonObject {
                                put("deliver", 0.07)
                                put("digest", 0.88)
                                put("suppress", 0.05)
                            })
                        }
                    )
                    val result = NotificationRouter(client)
                        .decide(NotificationContext("Product update", "New features", true, 1))
                    status = formatDecision("Notifications", result)
                }
            }) { Text("Run notification demo") }

            Text("Semantic reranking", style = MaterialTheme.typography.titleMedium)
            Button(onClick = {
                scope.launch {
                    val candidates = listOf(
                        SearchCandidate("a", "Payments", "billing"),
                        SearchCandidate("b", "Notifications", "push notification routing"),
                        SearchCandidate("c", "Weather", "forecast")
                    )
                    val client = fakeClientFactory { request ->
                        JevResponse(
                            answers = request.questions.keys.associateWith { id ->
                                buildJsonObject {
                                    put("score", if (id == "fit_1") 10 else 4)
                                    put("confidence", 0.90)
                                }
                            }
                        )
                    }
                    val result = SemanticReranker(client)
                        .rerank("android notifications", candidates)
                    val top = result.firstOrNull { it.status == RankingStatus.RANKED }
                    status = if (top == null) {
                        "Reranking: ABSTAINED"
                    } else {
                        "Reranking: ${top.candidate.title} score=${top.score} confidence=${top.confidence}"
                    }
                }
            }) { Text("Run reranking demo") }

            Text("Verification", style = MaterialTheme.typography.titleMedium)
            Button(onClick = {
                scope.launch {
                    val client = fakeClient(
                        "verified" to buildJsonObject {
                            put("noul", 0.92)
                        }
                    )
                    val result = VerificationDecider(client)
                        .verify("payment confirmed", "payment confirmed")
                    status = formatDecision("Verification", result)
                }
            }) { Text("Run verification demo") }

            Text("Result", style = MaterialTheme.typography.titleMedium)
            Text(status)
            Text(
                "All demos use deterministic fake responses. Live mode should keep long-lived API keys behind a trusted backend or proxy.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

private fun fakeClient(
    answer: Pair<String, kotlinx.serialization.json.JsonObject>
) = fakeClientFactory { JevResponse(answers = mapOf(answer.first to answer.second)) }

private fun fakeClientFactory(
    responder: (dev.skbindas.jev.core.JevRequest) -> JevResponse
) = JevClient(JevTransport { request -> responder(request) })

private fun <T> formatDecision(label: String, decision: Decision<T>): String {
    return if (decision is Decision.Accepted<*>) {
        "$label: ${decision.value} confidence=${decision.confidence}"
    } else {
        val abstained = decision as Decision.Abstained
        "$label: ABSTAINED reason=${abstained.reason}"
    }
}
