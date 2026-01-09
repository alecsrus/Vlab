package com.virtuallabs.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.virtuallabs.premium.PremiumManager
import com.virtuallabs.ui.components.AppTopBar
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    premiumManager: PremiumManager,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val isPremium by premiumManager.isPremiumFlow.collectAsState(initial = false)

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Настройки",
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
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Premium (debug)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Переключатель для прототипа. В релизе замените на Google Play Billing.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Switch(
                        checked = isPremium,
                        onCheckedChange = {
                            scope.launch { premiumManager.setPremium(it) }
                        }
                    )
                }
            }

            Card {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Локальный AI",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "В приложении встроен офлайн‑тьютор (TF‑IDF по базе знаний). Он не отправляет данные в интернет.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Text(
                text = "Версия прототипа: 0.1.2",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
