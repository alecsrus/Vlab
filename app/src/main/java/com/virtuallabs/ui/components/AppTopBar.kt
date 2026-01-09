package com.virtuallabs.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    canNavigateBack: Boolean,
    onBack: () -> Unit,
    onOpenTutor: (() -> Unit)? = null,
    onOpenSettings: (() -> Unit)? = null
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Назад")
                }
            }
        },
        actions = {
            if (onOpenTutor != null) {
                IconButton(onClick = onOpenTutor) {
                    Icon(imageVector = Icons.Default.SmartToy, contentDescription = "AI‑тьютор")
                }
            }
            if (onOpenSettings != null) {
                IconButton(onClick = onOpenSettings) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "Настройки")
                }
            }
        }
    )
}
