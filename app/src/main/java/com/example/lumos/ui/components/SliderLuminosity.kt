package com.example.lumos.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.lumos.R

@Composable
fun SliderLuminosity(
    sliderPosition: Float,
    onValueChange: (Float) -> Unit
) {
    Row(
        modifier = Modifier
            .width(200.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.baseline_light_mode_24),
            contentDescription = "Icône personnalisée",
            modifier = Modifier.size(22.dp)
        )

        Slider(
            value = sliderPosition,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .height(4.dp),
            colors = SliderDefaults.colors(
                thumbColor = Color.Black, // Couleur du curseur
                activeTrackColor = Color.Black, // Piste active en noir
                inactiveTrackColor = Color.Gray // Piste inactive en gris
            ),
            valueRange = 1f..100f // Plage de 0 à 100%
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Pourcentage de l'intensité lumineuse
        Text(
            text = "${sliderPosition.toInt()}%",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SliderLuminosityPreview() {
    SliderLuminosity(
        sliderPosition = 50f,
        onValueChange = {}
    )
}