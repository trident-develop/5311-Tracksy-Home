package com.bailing.lark.roll.ui.icons

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class UtilityIconKind {
    Electricity, Water, Gas, Heating, Internet, Building,
    House, Calendar, Bell, Check, Chevron, Eye, Activity, Overview, Reminders,
    Calculator, Plus, Trash, Back
}

@Composable
fun UtilityIcon(
    kind: UtilityIconKind,
    tint: Color,
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    strokeWidth: Dp = 1.8.dp,
    filled: Boolean = false
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val sw = strokeWidth.toPx()
        val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
        when (kind) {
            UtilityIconKind.Electricity -> drawElectricity(w, h, tint, stroke, filled)
            UtilityIconKind.Water -> drawWater(w, h, tint, stroke, filled)
            UtilityIconKind.Gas -> drawGas(w, h, tint, stroke, filled)
            UtilityIconKind.Heating -> drawHeating(w, h, tint, stroke)
            UtilityIconKind.Internet -> drawInternet(w, h, tint, stroke)
            UtilityIconKind.Building -> drawBuilding(w, h, tint, stroke)
            UtilityIconKind.House -> drawHouse(w, h, tint, stroke)
            UtilityIconKind.Calendar -> drawCalendar(w, h, tint, stroke)
            UtilityIconKind.Bell -> drawBell(w, h, tint, stroke, filled)
            UtilityIconKind.Check -> drawCheck(w, h, tint, stroke)
            UtilityIconKind.Chevron -> drawChevron(w, h, tint, stroke)
            UtilityIconKind.Eye -> drawEye(w, h, tint, stroke)
            UtilityIconKind.Activity -> drawActivity(w, h, tint, stroke)
            UtilityIconKind.Overview -> drawOverview(w, h, tint, stroke)
            UtilityIconKind.Reminders -> drawBell(w, h, tint, stroke, filled)
            UtilityIconKind.Calculator -> drawCalculator(w, h, tint, stroke)
            UtilityIconKind.Plus -> drawPlus(w, h, tint, stroke)
            UtilityIconKind.Trash -> drawTrash(w, h, tint, stroke)
            UtilityIconKind.Back -> drawBack(w, h, tint, stroke)
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCalculator(
    w: Float, h: Float, tint: Color, stroke: Stroke
) {
    val left = w * 0.18f
    val top = h * 0.10f
    val right = w * 0.82f
    val bottom = h * 0.90f
    val r = w * 0.08f
    val outer = Path().apply {
        addRoundRect(
            androidx.compose.ui.geometry.RoundRect(
                left = left, top = top, right = right, bottom = bottom,
                radiusX = r, radiusY = r
            )
        )
    }
    drawPath(outer, tint, style = stroke)
    val screenBottom = top + h * 0.22f
    drawLine(
        color = tint,
        start = Offset(left, screenBottom),
        end = Offset(right, screenBottom),
        strokeWidth = stroke.width
    )
    val gridTop = screenBottom + h * 0.06f
    val gridBottom = bottom - h * 0.05f
    val gridLeft = left + w * 0.04f
    val gridRight = right - w * 0.04f
    val rows = 3
    val cols = 3
    for (rIdx in 0 until rows) {
        for (cIdx in 0 until cols) {
            val cx = gridLeft + (gridRight - gridLeft) * (cIdx + 0.5f) / cols
            val cy = gridTop + (gridBottom - gridTop) * (rIdx + 0.5f) / rows
            drawCircle(color = tint, radius = w * 0.025f, center = Offset(cx, cy))
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPlus(
    w: Float, h: Float, tint: Color, stroke: Stroke
) {
    drawLine(
        color = tint,
        start = Offset(w * 0.5f, h * 0.18f),
        end = Offset(w * 0.5f, h * 0.82f),
        strokeWidth = stroke.width,
        cap = StrokeCap.Round
    )
    drawLine(
        color = tint,
        start = Offset(w * 0.18f, h * 0.5f),
        end = Offset(w * 0.82f, h * 0.5f),
        strokeWidth = stroke.width,
        cap = StrokeCap.Round
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawTrash(
    w: Float, h: Float, tint: Color, stroke: Stroke
) {
    drawLine(
        color = tint,
        start = Offset(w * 0.10f, h * 0.28f),
        end = Offset(w * 0.90f, h * 0.28f),
        strokeWidth = stroke.width,
        cap = StrokeCap.Round
    )
    drawLine(
        color = tint,
        start = Offset(w * 0.38f, h * 0.20f),
        end = Offset(w * 0.62f, h * 0.20f),
        strokeWidth = stroke.width,
        cap = StrokeCap.Round
    )
    val body = Path().apply {
        moveTo(w * 0.20f, h * 0.32f)
        lineTo(w * 0.26f, h * 0.90f)
        lineTo(w * 0.74f, h * 0.90f)
        lineTo(w * 0.80f, h * 0.32f)
    }
    drawPath(body, tint, style = stroke)
    drawLine(
        color = tint,
        start = Offset(w * 0.40f, h * 0.45f),
        end = Offset(w * 0.40f, h * 0.78f),
        strokeWidth = stroke.width,
        cap = StrokeCap.Round
    )
    drawLine(
        color = tint,
        start = Offset(w * 0.60f, h * 0.45f),
        end = Offset(w * 0.60f, h * 0.78f),
        strokeWidth = stroke.width,
        cap = StrokeCap.Round
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBack(
    w: Float, h: Float, tint: Color, stroke: Stroke
) {
    val path = Path().apply {
        moveTo(w * 0.62f, h * 0.20f)
        lineTo(w * 0.32f, h * 0.50f)
        lineTo(w * 0.62f, h * 0.80f)
    }
    drawPath(path, tint, style = stroke)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawElectricity(
    w: Float, h: Float, tint: Color, stroke: Stroke, filled: Boolean
) {
    val path = Path().apply {
        moveTo(w * 0.58f, h * 0.05f)
        lineTo(w * 0.20f, h * 0.55f)
        lineTo(w * 0.46f, h * 0.55f)
        lineTo(w * 0.34f, h * 0.95f)
        lineTo(w * 0.82f, h * 0.42f)
        lineTo(w * 0.55f, h * 0.42f)
        lineTo(w * 0.68f, h * 0.05f)
        close()
    }
    if (filled) drawPath(path, tint) else drawPath(path, tint, style = stroke)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawWater(
    w: Float, h: Float, tint: Color, stroke: Stroke, filled: Boolean
) {
    val path = Path().apply {
        moveTo(w * 0.5f, h * 0.05f)
        cubicTo(
            w * 0.85f, h * 0.40f,
            w * 0.95f, h * 0.70f,
            w * 0.5f, h * 0.95f
        )
        cubicTo(
            w * 0.05f, h * 0.70f,
            w * 0.15f, h * 0.40f,
            w * 0.5f, h * 0.05f
        )
        close()
    }
    if (filled) drawPath(path, tint) else drawPath(path, tint, style = stroke)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawGas(
    w: Float, h: Float, tint: Color, stroke: Stroke, filled: Boolean
) {
    val path = Path().apply {
        moveTo(w * 0.5f, h * 0.05f)
        cubicTo(
            w * 0.85f, h * 0.30f,
            w * 0.95f, h * 0.65f,
            w * 0.7f, h * 0.85f
        )
        cubicTo(
            w * 0.55f, h * 0.95f,
            w * 0.30f, h * 0.95f,
            w * 0.18f, h * 0.75f
        )
        cubicTo(
            w * 0.10f, h * 0.55f,
            w * 0.30f, h * 0.45f,
            w * 0.40f, h * 0.55f
        )
        cubicTo(
            w * 0.42f, h * 0.30f,
            w * 0.42f, h * 0.20f,
            w * 0.5f, h * 0.05f
        )
        close()
    }
    if (filled) drawPath(path, tint) else drawPath(path, tint, style = stroke)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHeating(
    w: Float, h: Float, tint: Color, stroke: Stroke
) {
    val left = w * 0.15f
    val top = h * 0.20f
    val right = w * 0.85f
    val bottom = h * 0.85f
    val r = w * 0.10f
    val rect = Path().apply {
        addRoundRect(
            androidx.compose.ui.geometry.RoundRect(
                left = left, top = top, right = right, bottom = bottom,
                radiusX = r, radiusY = r
            )
        )
    }
    drawPath(rect, tint, style = stroke)
    val cols = 4
    val pad = w * 0.10f
    val innerW = (right - left) - pad * 2
    for (i in 0 until cols) {
        val x = left + pad + innerW * (i + 0.5f) / cols
        drawLine(
            color = tint,
            start = Offset(x, top + h * 0.10f),
            end = Offset(x, bottom - h * 0.10f),
            strokeWidth = stroke.width,
            cap = StrokeCap.Round
        )
    }
    drawLine(
        color = tint,
        start = Offset(left + r * 0.6f, top - h * 0.06f),
        end = Offset(left + r * 0.6f, top),
        strokeWidth = stroke.width,
        cap = StrokeCap.Round
    )
    drawLine(
        color = tint,
        start = Offset(right - r * 0.6f, top - h * 0.06f),
        end = Offset(right - r * 0.6f, top),
        strokeWidth = stroke.width,
        cap = StrokeCap.Round
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawInternet(
    w: Float, h: Float, tint: Color, stroke: Stroke
) {
    val cx = w * 0.5f
    val cy = h * 0.85f
    fun arc(rPx: Float) {
        val sweepStart = 200f
        val sweepDelta = 140f
        drawArc(
            color = tint,
            startAngle = sweepStart,
            sweepAngle = sweepDelta,
            useCenter = false,
            topLeft = Offset(cx - rPx, cy - rPx),
            size = Size(rPx * 2, rPx * 2),
            style = stroke
        )
    }
    arc(w * 0.40f)
    arc(w * 0.25f)
    drawCircle(color = tint, radius = w * 0.05f, center = Offset(cx, cy - h * 0.02f))
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBuilding(
    w: Float, h: Float, tint: Color, stroke: Stroke
) {
    val left = w * 0.15f
    val top = h * 0.15f
    val right = w * 0.85f
    val bottom = h * 0.92f
    val r = w * 0.05f
    val outline = Path().apply {
        addRoundRect(
            androidx.compose.ui.geometry.RoundRect(
                left = left, top = top, right = right, bottom = bottom,
                radiusX = r, radiusY = r
            )
        )
    }
    drawPath(outline, tint, style = stroke)
    val rows = 3
    val cols = 3
    val ww = w * 0.10f
    val wh = h * 0.10f
    val innerW = right - left
    val innerH = bottom - top
    for (rIdx in 0 until rows) {
        for (cIdx in 0 until cols) {
            val x = left + innerW * (cIdx + 1) / (cols + 1) - ww / 2
            val y = top + innerH * (rIdx + 1) / (rows + 1) - wh / 2
            val win = Path().apply {
                addRoundRect(
                    androidx.compose.ui.geometry.RoundRect(
                        left = x, top = y, right = x + ww, bottom = y + wh,
                        radiusX = w * 0.015f, radiusY = w * 0.015f
                    )
                )
            }
            drawPath(win, tint, style = stroke)
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHouse(
    w: Float, h: Float, tint: Color, stroke: Stroke
) {
    val path = Path().apply {
        moveTo(w * 0.10f, h * 0.50f)
        lineTo(w * 0.50f, h * 0.10f)
        lineTo(w * 0.90f, h * 0.50f)
        lineTo(w * 0.90f, h * 0.90f)
        lineTo(w * 0.10f, h * 0.90f)
        close()
    }
    drawPath(path, tint, style = stroke)
    val door = Path().apply {
        moveTo(w * 0.42f, h * 0.90f)
        lineTo(w * 0.42f, h * 0.65f)
        lineTo(w * 0.58f, h * 0.65f)
        lineTo(w * 0.58f, h * 0.90f)
    }
    drawPath(door, tint, style = stroke)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCalendar(
    w: Float, h: Float, tint: Color, stroke: Stroke
) {
    val left = w * 0.12f
    val top = h * 0.20f
    val right = w * 0.88f
    val bottom = h * 0.92f
    val r = w * 0.08f
    val rect = Path().apply {
        addRoundRect(
            androidx.compose.ui.geometry.RoundRect(
                left = left, top = top, right = right, bottom = bottom,
                radiusX = r, radiusY = r
            )
        )
    }
    drawPath(rect, tint, style = stroke)
    drawLine(
        color = tint,
        start = Offset(left, top + h * 0.18f),
        end = Offset(right, top + h * 0.18f),
        strokeWidth = stroke.width
    )
    drawLine(
        color = tint,
        start = Offset(left + w * 0.20f, top - h * 0.05f),
        end = Offset(left + w * 0.20f, top + h * 0.10f),
        strokeWidth = stroke.width,
        cap = StrokeCap.Round
    )
    drawLine(
        color = tint,
        start = Offset(right - w * 0.20f, top - h * 0.05f),
        end = Offset(right - w * 0.20f, top + h * 0.10f),
        strokeWidth = stroke.width,
        cap = StrokeCap.Round
    )
    drawCircle(color = tint, radius = w * 0.04f, center = Offset(w * 0.5f, h * 0.65f))
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBell(
    w: Float, h: Float, tint: Color, stroke: Stroke, filled: Boolean
) {
    val path = Path().apply {
        moveTo(w * 0.5f, h * 0.10f)
        cubicTo(
            w * 0.85f, h * 0.10f,
            w * 0.80f, h * 0.55f,
            w * 0.88f, h * 0.72f
        )
        lineTo(w * 0.12f, h * 0.72f)
        cubicTo(
            w * 0.20f, h * 0.55f,
            w * 0.15f, h * 0.10f,
            w * 0.5f, h * 0.10f
        )
        close()
    }
    if (filled) drawPath(path, tint) else drawPath(path, tint, style = stroke)
    drawLine(
        color = tint,
        start = Offset(w * 0.40f, h * 0.85f),
        end = Offset(w * 0.60f, h * 0.85f),
        strokeWidth = stroke.width,
        cap = StrokeCap.Round
    )
    drawCircle(color = tint, radius = w * 0.04f, center = Offset(w * 0.5f, h * 0.05f))
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCheck(
    w: Float, h: Float, tint: Color, stroke: Stroke
) {
    val path = Path().apply {
        moveTo(w * 0.18f, h * 0.55f)
        lineTo(w * 0.42f, h * 0.78f)
        lineTo(w * 0.85f, h * 0.28f)
    }
    drawPath(path, tint, style = stroke)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawChevron(
    w: Float, h: Float, tint: Color, stroke: Stroke
) {
    val path = Path().apply {
        moveTo(w * 0.35f, h * 0.20f)
        lineTo(w * 0.65f, h * 0.50f)
        lineTo(w * 0.35f, h * 0.80f)
    }
    drawPath(path, tint, style = stroke)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawEye(
    w: Float, h: Float, tint: Color, stroke: Stroke
) {
    val path = Path().apply {
        moveTo(w * 0.05f, h * 0.5f)
        cubicTo(
            w * 0.25f, h * 0.15f,
            w * 0.75f, h * 0.15f,
            w * 0.95f, h * 0.5f
        )
        cubicTo(
            w * 0.75f, h * 0.85f,
            w * 0.25f, h * 0.85f,
            w * 0.05f, h * 0.5f
        )
        close()
    }
    drawPath(path, tint, style = stroke)
    drawCircle(color = tint, radius = w * 0.10f, center = Offset(w * 0.5f, h * 0.5f))
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawActivity(
    w: Float, h: Float, tint: Color, stroke: Stroke
) {
    val path = Path().apply {
        moveTo(w * 0.05f, h * 0.55f)
        lineTo(w * 0.28f, h * 0.55f)
        lineTo(w * 0.40f, h * 0.25f)
        lineTo(w * 0.55f, h * 0.80f)
        lineTo(w * 0.68f, h * 0.45f)
        lineTo(w * 0.78f, h * 0.55f)
        lineTo(w * 0.95f, h * 0.55f)
    }
    drawPath(path, tint, style = stroke)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawOverview(
    w: Float, h: Float, tint: Color, stroke: Stroke
) {
    val r = w * 0.10f
    fun cell(left: Float, top: Float, right: Float, bottom: Float) {
        val p = Path().apply {
            addRoundRect(
                androidx.compose.ui.geometry.RoundRect(
                    left = left, top = top, right = right, bottom = bottom,
                    radiusX = r, radiusY = r
                )
            )
        }
        drawPath(p, tint, style = stroke)
    }
    cell(w * 0.10f, h * 0.10f, w * 0.46f, h * 0.46f)
    cell(w * 0.54f, h * 0.10f, w * 0.90f, h * 0.46f)
    cell(w * 0.10f, h * 0.54f, w * 0.46f, h * 0.90f)
    cell(w * 0.54f, h * 0.54f, w * 0.90f, h * 0.90f)
}
