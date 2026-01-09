package com.virtuallabs.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.virtuallabs.data.CatalogRepository
import com.virtuallabs.premium.PremiumManager
import com.virtuallabs.ui.components.AppTopBar
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    catalog: CatalogRepository,
    premiumManager: PremiumManager,
    onOpenSubject: (String) -> Unit,
    onOpenTutor: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val subjects = remember { catalog.subjects() }
    val allTopics = remember { catalog.loadCatalog().topics }
    val isPremium by premiumManager.isPremiumFlow.collectAsState(initial = false)

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Виртуальные лаборатории",
                canNavigateBack = false,
                onBack = {},
                onOpenTutor = onOpenTutor,
                onOpenSettings = onOpenSettings
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = if (isPremium) "Premium активен ✅" else "Free режим (часть тем закрыта) 🔒",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Выберите предмет. В каждой теме — визуализация/симуляция и подсказки.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            items(subjects) { subject ->
                val topics = allTopics.filter { it.subjectId == subject.id }
                val premiumCount = topics.count { it.premium }
                val freeCount = topics.size - premiumCount

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenSubject(subject.id) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = subject.emoji,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = subject.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Тем: $freeCount бесплатно • $premiumCount premium",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            val total = topics.size.coerceAtLeast(1)
                            val freeRatio = (freeCount.toFloat() / total.toFloat()).coerceIn(0f, 1f)
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = freeRatio,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = "Бесплатно доступно: ${(freeRatio * 100).roundToInt()}%",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                        Text(
                            text = "→",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }
        }
    }
}
