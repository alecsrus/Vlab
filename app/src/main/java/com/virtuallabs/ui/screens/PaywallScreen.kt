package com.virtuallabs.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.virtuallabs.data.CatalogRepository
import com.virtuallabs.premium.PremiumManager
import com.virtuallabs.ui.components.AppTopBar
import kotlinx.coroutines.launch

@Composable
fun PaywallScreen(
    topicId: String,
    catalog: CatalogRepository,
    premiumManager: PremiumManager,
    onBack: () -> Unit,
    onUnlocked: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val topic = catalog.topicById(topicId)

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Premium",
                canNavigateBack = true,
                onBack = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Открыть Premium 🔓",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = buildString {
                            append("Тема: ")
                            append(topic?.title ?: topicId)
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "Premium может включать: дополнительные лаборатории, офлайн‑пакеты контента, расширенные симуляции, тесты и т.д.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Прототипная разблокировка (debug)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Кнопка ниже просто включает Premium на этом устройстве через DataStore. Для релиза подключите Google Play Billing.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(onClick = {
                        scope.launch {
                            premiumManager.setPremium(true)
                            onUnlocked()
                        }
                    }) {
                        Text("Разблокировать Premium (debug)")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Важно: в реальном продукте доступ к Premium должен проверяться через Google Play Billing / серверную валидацию (по необходимости).",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
