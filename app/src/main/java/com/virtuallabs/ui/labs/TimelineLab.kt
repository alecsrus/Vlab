package com.virtuallabs.ui.labs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private data class TimelineEvent(
    val year: String,
    val title: String,
    val note: String
)

@Composable
fun TimelineLab() {
    val events = listOf(
        TimelineEvent("1789", "Созыв Генеральных штатов", "Кризис финансов и политическая напряжённость."),
        TimelineEvent("14 июля 1789", "Взятие Бастилии", "Символическое начало революции."),
        TimelineEvent("1789", "Декларация прав человека и гражданина", "Новые принципы прав и свобод."),
        TimelineEvent("1792", "Провозглашение республики", "Монархия упразднена, начинается новый этап."),
        TimelineEvent("1793–1794", "Якобинский террор", "Радикализация и массовые репрессии."),
        TimelineEvent("1795–1799", "Директория", "Политическая нестабильность и войны."),
        TimelineEvent("1799", "18 брюмера", "Переворот и приход к власти Наполеона.")
    )

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Таймлайн: Французская революция",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Нажмите на события (в расширенной версии) и связывайте причины/последствия.",
                style = MaterialTheme.typography.bodyMedium
            )

            Divider()

            events.forEachIndexed { idx, e ->
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "${e.year} — ${e.title}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = e.note,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                if (idx != events.lastIndex) {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}
