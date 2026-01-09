package com.virtuallabs.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.virtuallabs.data.CatalogRepository
import com.virtuallabs.data.LabType
import com.virtuallabs.premium.PremiumManager
import com.virtuallabs.ui.components.AppTopBar
import com.virtuallabs.ui.labs.PendulumLab
import com.virtuallabs.ui.labs.QuadraticLab
import com.virtuallabs.ui.labs.TimelineLab
import com.virtuallabs.ui.labs.PlaceholderLab

@Composable
fun LabHostScreen(
    topicId: String,
    catalog: CatalogRepository,
    premiumManager: PremiumManager,
    onBack: () -> Unit,
    onOpenPaywall: () -> Unit,
    onOpenTutor: (String) -> Unit,
    onOpenSettings: () -> Unit
) {
    val topic = remember(topicId) { catalog.topicById(topicId) }
    val subject = remember(topicId) { topic?.let { catalog.subjectById(it.subjectId) } }
    val isPremium by premiumManager.isPremiumFlow.collectAsState(initial = false)

    val canAccess = (topic != null) && (isPremium || !topic.premium)

    Scaffold(
        topBar = {
            AppTopBar(
                title = topic?.title ?: "Лаборатория",
                canNavigateBack = true,
                onBack = onBack,
                onOpenTutor = { onOpenTutor(topic?.title ?: "школьная тема") },
                onOpenSettings = onOpenSettings
            )
        }
    ) { padding ->
        if (topic == null) {
            Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                Text("Не найдена тема: $topicId")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "${subject?.emoji ?: "📚"}  ${subject?.title ?: ""}",
                style = MaterialTheme.typography.titleMedium
            )

            if (topic.description.isNotBlank()) {
                Text(
                    text = topic.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (!canAccess) {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Тема доступна только в Premium 🔒",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Разблокируйте Premium, чтобы открыть дополнительные лаборатории по программе.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Button(onClick = onOpenPaywall) {
                            Text("Открыть Premium")
                        }
                    }
                }
                return@Scaffold
            }

            // Сама лаборатория
            when (topic.labType) {
                LabType.PENDULUM -> PendulumLab()
                LabType.QUADRATIC -> QuadraticLab()
                LabType.TIMELINE -> TimelineLab()
                LabType.PLACEHOLDER -> PlaceholderLab()
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Подсказка: нажмите на иконку 🤖 сверху, чтобы спросить AI‑тьютора по этой теме (офлайн).",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
