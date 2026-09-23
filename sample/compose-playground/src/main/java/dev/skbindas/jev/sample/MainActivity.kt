package dev.skbindas.jev.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import dev.skbindas.jev.modules.PaywallContext
import dev.skbindas.jev.modules.PaywallDecider
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
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Jev Android Toolkit", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Typed semantic decisions with deterministic application logic.",
                style = MaterialTheme.typography.bodyLarge
            )
            Text("Paywall decision demo", style = MaterialTheme.typography.titleMedium)

            Button(
                onClick = {
                    scope.launch {
                        val fakeJev = JevClient(
                            JevTransport {
                                JevResponse(
                                    answers = mapOf(
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
                                )
                            }
                        )

                        val result = PaywallDecider(
                            jev = fakeJev,
                            policy = DecisionPolicy(0.70, 0.10)
                        ).decide(PaywallContext(3, 2, false, false))

                        status = when (result) {
                            is Decision.Accepted ->
                                "Decision: " + result.value + " (" + result.confidence + ")"
                            is Decision.Abstained ->
                                "Abstained: " + result.reason
                        }
                    }
                }
            ) {
                Text("Run offline demo")
            }

            Text(status)
            Text(
                "Live mode uses HttpJevTransport against TypeSafe. Keep API keys out of the APK and prefer a trusted backend or proxy for production.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
