package com.virtuallabs.ui.labs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun PendulumLab() {
    var length by remember { mutableFloatStateOf(1.0f) }     // meters
    var gravity by remember { mutableFloatStateOf(9.8f) }    // m/s^2
    var damping by remember { mutableFloatStateOf(0.02f) }   // arbitrary

    var theta by remember { mutableFloatStateOf(0.8f) }      // rad
    var omega by remember { mutableFloatStateOf(0.0f) }

    // Перезапускаем динамику, если меняем параметры
    LaunchedEffect(length, gravity, damping) {
        theta = 0.8f
        omega = 0.0f

        val dt = 0.016f
        while (isActive) {
            val alpha = -(gravity / length) * sin(theta) - damping * omega
            omega += alpha * dt
            theta += omega * dt
            delay(16)
        }
    }

    val periodApprox = 2.0 * PI * sqrt(length / gravity)

    val colors = MaterialTheme.colorScheme

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Симуляция: математический маятник",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "При малых углах T ≈ 2π√(L/g). Сейчас: T ≈ %.2f с".format(periodApprox),
                style = MaterialTheme.typography.bodyMedium
            )

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                val pivot = Offset(size.width / 2f, 24f)
                val maxLenPx = size.height * 0.75f
                val pxPerMeter = maxLenPx / length

                val bobX = pivot.x + sin(theta) * length * pxPerMeter
                val bobY = pivot.y + cos(theta) * length * pxPerMeter
                val bob = Offset(bobX, bobY)

                // нить
                drawLine(
                    color = colors.onSurface,
                    start = pivot,
                    end = bob,
                    strokeWidth = 6f
                )

                // дуга (для наглядности)
                drawArc(
                    color = colors.primary,
                    startAngle = -90f,
                    sweepAngle = theta * (180f / PI.toFloat()),
                    useCenter = false,
                    topLeft = Offset(pivot.x - length * pxPerMeter, pivot.y - length * pxPerMeter),
                    size = Size(length * pxPerMeter * 2, length * pxPerMeter * 2),
                    style = Stroke(width = 3f)
                )

                // груз
                drawCircle(
                    color = colors.secondary,
                    center = bob,
                    radius = 18f
                )

                // точка подвеса
                drawCircle(
                    color = colors.tertiary,
                    center = pivot,
                    radius = 8f
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text("Длина L = %.2f м".format(length), style = MaterialTheme.typography.bodyMedium)
            Slider(
                value = length,
                onValueChange = { length = it.coerceIn(0.2f, 2.0f) },
                valueRange = 0.2f..2.0f
            )

            Text("g = %.1f м/с²".format(gravity), style = MaterialTheme.typography.bodyMedium)
            Slider(
                value = gravity,
                onValueChange = { gravity = it.coerceIn(1.0f, 20.0f) },
                valueRange = 1.0f..20.0f
            )

            Text("Затухание = %.3f".format(damping), style = MaterialTheme.typography.bodyMedium)
            Slider(
                value = damping,
                onValueChange = { damping = it.coerceIn(0.0f, 0.2f) },
                valueRange = 0.0f..0.2f
            )
        }
    }
}
