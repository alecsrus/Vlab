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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

@Composable
fun QuadraticLab() {
    var a by remember { mutableFloatStateOf(1.0f) }
    var b by remember { mutableFloatStateOf(0.0f) }
    var c by remember { mutableFloatStateOf(0.0f) }

    val colors = MaterialTheme.colorScheme

    val disc = b * b - 4f * a * c
    val rootsText = if (a == 0f) {
        "a = 0 (это уже не парабола)"
    } else if (disc < 0f) {
        "D = %.2f < 0 — действительных корней нет".format(disc)
    } else {
        val r1 = (-b - sqrt(disc)) / (2f * a)
        val r2 = (-b + sqrt(disc)) / (2f * a)
        "D = %.2f — корни: x₁=%.2f, x₂=%.2f".format(disc, r1, r2)
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "График: y = ax² + bx + c",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = rootsText,
                style = MaterialTheme.typography.bodyMedium
            )

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                val w = size.width
                val h = size.height

                val xRange = 10f
                val n = 220

                // Посчитаем y для оценивания масштаба
                var minY = Float.POSITIVE_INFINITY
                var maxY = Float.NEGATIVE_INFINITY
                for (i in 0..n) {
                    val x = -xRange + 2f * xRange * (i / n.toFloat())
                    val y = a * x * x + b * x + c
                    minY = minOf(minY, y)
                    maxY = maxOf(maxY, y)
                }

                val yAbsMax = max(1f, max(abs(minY), abs(maxY)))
                val scaleX = (w * 0.45f) / xRange
                val scaleY = (h * 0.40f) / yAbsMax

                val origin = Offset(w / 2f, h / 2f)

                fun toCanvas(x: Float, y: Float): Offset {
                    return Offset(
                        x = origin.x + x * scaleX,
                        y = origin.y - y * scaleY
                    )
                }

                // axes
                drawLine(
                    color = colors.onSurface.copy(alpha = 0.4f),
                    start = Offset(0f, origin.y),
                    end = Offset(w, origin.y),
                    strokeWidth = 3f
                )
                drawLine(
                    color = colors.onSurface.copy(alpha = 0.4f),
                    start = Offset(origin.x, 0f),
                    end = Offset(origin.x, h),
                    strokeWidth = 3f
                )

                // parabola path
                val path = Path()
                for (i in 0..n) {
                    val x = -xRange + 2f * xRange * (i / n.toFloat())
                    val y = a * x * x + b * x + c
                    val p = toCanvas(x, y)
                    if (i == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y)
                }

                drawPath(
                    path = path,
                    color = colors.primary,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 5f)
                )

                // vertex marker
                if (a != 0f) {
                    val vx = -b / (2f * a)
                    val vy = a * vx * vx + b * vx + c
                    val vp = toCanvas(vx, vy)
                    drawCircle(
                        color = colors.secondary,
                        radius = 8f,
                        center = vp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text("a = %.2f".format(a), style = MaterialTheme.typography.bodyMedium)
            Slider(value = a, onValueChange = { a = it }, valueRange = -5f..5f)

            Text("b = %.2f".format(b), style = MaterialTheme.typography.bodyMedium)
            Slider(value = b, onValueChange = { b = it }, valueRange = -10f..10f)

            Text("c = %.2f".format(c), style = MaterialTheme.typography.bodyMedium)
            Slider(value = c, onValueChange = { c = it }, valueRange = -10f..10f)
        }
    }
}
