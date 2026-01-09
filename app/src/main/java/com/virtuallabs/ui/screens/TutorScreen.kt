package com.virtuallabs.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.virtuallabs.tutor.LocalTutor
import com.virtuallabs.ui.components.AppTopBar
import kotlinx.coroutines.launch

private data class ChatMessage(
    val fromUser: Boolean,
    val text: String
)

@Composable
fun TutorScreen(
    tutor: LocalTutor,
    prefill: String?,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val messages = remember { mutableStateListOf<ChatMessage>() }

    var input by remember { mutableStateOf(prefill ?: "") }
    var busy by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (messages.isEmpty()) {
            messages.add(
                ChatMessage(
                    fromUser = false,
                    text = "Привет! Я офлайн‑AI‑тьютор. Задайте вопрос по теме — я найду ответ в встроенной базе знаний."
                )
            )
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "AI‑тьютор (офлайн)",
                canNavigateBack = true,
                onBack = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages) { msg ->
                    MessageBubble(msg)
                }
                if (busy) {
                    item {
                        MessageBubble(
                            ChatMessage(
                                fromUser = false,
                                text = "Думаю… (локально)"
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    modifier = Modifier.weight(1f),
                    value = input,
                    onValueChange = { input = it },
                    placeholder = { Text("Напишите вопрос…") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        val q = input.trim()
                        if (q.isEmpty() || busy) return@IconButton
                        input = ""
                        messages.add(ChatMessage(fromUser = true, text = q))
                        busy = true
                        scope.launch {
                            val ans = tutor.answer(q)
                            messages.add(ChatMessage(fromUser = false, text = ans.answerText))
                            busy = false
                        }
                    }
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Отправить")
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(msg: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (msg.fromUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.88f)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = if (msg.fromUser) "Вы" else "AI",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = msg.text,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
