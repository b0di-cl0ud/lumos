package com.example.lumos.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.lumos.R
import com.example.lumos.ui.theme.LumosTheme
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lumos.ui.components.ColorPicker
import com.example.lumos.ui.components.SliderLuminosity
import com.example.lumos.ui.screens.scan.ScanViewModel


@Composable
fun LampSettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: ScanViewModel = viewModel(),
) {
    var sliderPosition by remember { mutableStateOf(viewModel.lightBulb.sliderPosition) }
    var selectedColor by remember { mutableStateOf(viewModel.lightBulb.color) }
    var isLedOn by remember { mutableStateOf(viewModel.lightBulb.isLedOn) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.White, Color.Black),
                    startY = 1000f,
                    endY = 2500f
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Image de la lampe
            Image(
                modifier = Modifier
                    .size(100.dp)
                    .background(selectedColor, CircleShape),
                painter = painterResource(id = R.drawable.baseline_lightbulb_circle_24),
                contentDescription = "Lampe"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Bouton switch LED
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isLedOn) stringResource(R.string.on) else stringResource(R.string.off),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
                Switch(
                    checked = isLedOn,
                    onCheckedChange = {
                        isLedOn = it
                        viewModel.toggleLed()
                        viewModel.updateLedState(it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Yellow,
                        checkedTrackColor = Color.LightGray,
                        uncheckedThumbColor = Color.Black,
                        uncheckedTrackColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = stringResource(R.string.intensity),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Curseur intensité lumineuse
            SliderLuminosity(
                sliderPosition = sliderPosition,
                onValueChange = {
                    sliderPosition = it
                    viewModel.updateSliderPosition(it)
                }
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = stringResource(R.string.light_color),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Palette de couleurs
            ColorPicker(
                onColorSelected = {
                    selectedColor = it
                    viewModel.updateSelectedColor(it)
                },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LampSettingsScreenPreview() {
    LumosTheme {
        LampSettingsScreen(
            modifier = Modifier.fillMaxSize()
        )
    }
}