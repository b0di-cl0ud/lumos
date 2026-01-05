package com.example.lumos.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lumos.R
import com.example.lumos.ui.theme.LumosTheme

@Composable
fun Welcome(
    onScanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.White, Color.Black),
                    startY = 1000f,
                    endY = 2500f
                )
            )
    ) {
        Column (
            modifier = modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.padding(20.dp))

            Image(
                //modifier = Modifier.size(100.dp), pour changer la taille de l'image
                painter = painterResource(id = R.drawable.lumos),
                contentDescription = "Logo lamp",
            )

            Text(
                modifier = Modifier.padding(top = 25.dp),
                text = stringResource(R.string.welcome),
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.padding(30.dp))

            ElevatedButton(
                onClick = {
                    onScanClick()
                },
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = Color.LightGray, // Couleur de fond du bouton
                    contentColor = Color.Black   // Couleur du texte du bouton
                )
            ) {
                Text(
                    text = stringResource(R.string.start_scan)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomePreview() {
    LumosTheme {
        Welcome(
            onScanClick = {}
        )
    }
}