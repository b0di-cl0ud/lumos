package com.example.lumos.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.atan2
import kotlin.math.min
import kotlin.math.PI

@Composable
fun ColorPicker(
    modifier: Modifier = Modifier
        .size(200.dp)
        .rotate(270f),
    onColorSelected: (Color) -> Unit
) {
    // Taille de la palette
    val diameter = 200.dp
    val radiusPx = with(LocalDensity.current) { diameter.toPx() / 2 }
    val selectedColor = remember { mutableStateOf(Color.White) }

    // Créer un Brush pour la palette circulaire
    val colorWheelBrush = remember {
        ShaderBrush(
            android.graphics.ComposeShader(
                android.graphics.SweepGradient(
                    radiusPx,
                    radiusPx,
                    intArrayOf(
                        Color.Red.toArgb(),
                        Color.Magenta.toArgb(),
                        Color.Blue.toArgb(),
                        Color.Cyan.toArgb(),
                        Color.Green.toArgb(),
                        Color.Yellow.toArgb(),
                        Color.Red.toArgb()
                    ),
                    null
                ),
                android.graphics.RadialGradient(
                    radiusPx,
                    radiusPx,
                    radiusPx,
                    intArrayOf(Color.White.toArgb(), Color.Transparent.toArgb()),
                    floatArrayOf(0.1f, 0.5f),
                    android.graphics.Shader.TileMode.CLAMP
                ),
                android.graphics.PorterDuff.Mode.SRC_OVER
            )
        )
    }

    // Palette circulaire
    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    // Vérifier si le point touché est dans le cercle
                    val center = Offset(radiusPx, radiusPx)
                    val distance = (offset - center).getDistance()
                    if (distance <= radiusPx) {
                        val angle = (atan2(center.y - offset.y, offset.x - center.x) * (180 / PI)).toFloat()
                        val normalizedAngle = (angle + 360) % 360
                        val saturation = distance / radiusPx // Distance détermine la saturation
                        selectedColor.value = calculateColor(normalizedAngle, saturation)
                        onColorSelected(selectedColor.value)
                    }
                }
            }
    ) {
        // Dessiner la palette
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = colorWheelBrush,
                radius = radiusPx,
                center = Offset(radiusPx, radiusPx)
            )
        }
    }
}

// Calcul de la couleur en fonction de l'angle et de la saturation
private fun calculateColor(angle: Float, saturation: Float): Color {
    val hsv = floatArrayOf(angle, saturation, 1f) // Teinte, saturation, luminosité
    return Color(android.graphics.Color.HSVToColor(hsv))
}

@Preview(showBackground = true)
@Composable
fun ColorPickerPreview() {
    ColorPicker(onColorSelected = {})
}