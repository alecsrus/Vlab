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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.virtuallabs.data.CatalogRepository
import com.virtuallabs.data.Topic
import com.virtuallabs.data.LabType
import com.virtuallabs.premium.PremiumManager
import com.virtuallabs.ui.components.AppTopBar

@Composable
fun SubjectScreen(
    subjectId: String,
    catalog: CatalogRepository,
    premiumManager: PremiumManager,
    onBack: () -> Unit,
    onOpenLab: (String) -> Unit,
    onOpenPaywall: (String) -> Unit,
    onOpenTutor: (String) -> Unit,
    onOpenSettings: () -> Unit
) {
    val subject = remember(subjectId) { catalog.subjectById(subjectId) }
    val topics = remember(subjectId) { catalog.topicsForSubject(subjectId) }
    val isPremium by premiumManager.isPremiumFlow.collectAsState(initial = false)

    Scaffold(
        topBar = {
            AppTopBar(
                title = subject?.title ?: "Предмет",
                canNavigateBack = true,
                onBack = onBack,
                onOpenTutor = { onOpenTutor(subject?.title ?: "школа") },
                onOpenSettings = onOpenSettings
            )
        }
    ) { padding ->
        if (subject == null) {
            Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                Text("Не найден предмет: $subjectId")
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "${subject.emoji}  ${subject.title}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isPremium) "Premium активен — все темы доступны." else "Часть тем может быть заблокирована (Premium).",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            items(topics) { topic ->
                TopicCard(
                    topic = topic,
                    isUnlocked = isPremium || !topic.premium,
                    onClick = {
                        if (isPremium || !topic.premium) onOpenLab(topic.id) else onOpenPaywall(topic.id)
                    }
                )
            }
        }
    }
}

@Composable
private fun TopicCard(
    topic: Topic,
    isUnlocked: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        val (labIcon, labLabel) = labMeta(topic.labType)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = labIcon,
                contentDescription = labLabel
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = topic.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = buildString {
                        append(labLabel)
                        append(" • ")
                        append(if (topic.premium) "Premium" else "Free")
                    },
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Классы: ${topic.gradeFrom}–${topic.gradeTo}",
                    style = MaterialTheme.typography.bodySmall
                )
                if (topic.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = topic.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = if (isUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                contentDescription = if (isUnlocked) "Доступно" else "Заблокировано"
            )
        }
    }
}


private fun labMeta(type: LabType): Pair<androidx.compose.ui.graphics.vector.ImageVector, String> {
    return when (type) {
        LabType.PENDULUM -> Icons.Filled.Science to "Симуляция"
        LabType.QUADRATIC -> Icons.Filled.ShowChart to "График"
        LabType.TIMELINE -> Icons.Filled.Timeline to "Таймлайн"
        LabType.PLACEHOLDER -> Icons.Filled.Construction to "Лаборатория"
    }
}
