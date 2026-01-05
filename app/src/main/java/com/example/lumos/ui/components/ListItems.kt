package com.example.lumos.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lumos.R
import com.example.lumos.ui.theme.LumosTheme

@Composable
fun ElementList(
    title: String = "Mon titre",
    content: String = "Mon contenu",
    image: Int = R.drawable.bulb,
    onClick: () -> Unit = {}
) {
    Spacer(modifier = Modifier.padding(5.dp))
    Card(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.Gray)
    ) {
        Row(
            modifier = Modifier.padding(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                modifier = Modifier.size(50.dp),
                painter = painterResource(image),
                contentDescription = "Logo lamp",
            )
            Column {
                Text(title)
                Text(content)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ElementListPreview() {
    LumosTheme {
        ElementList(
        )
    }
}