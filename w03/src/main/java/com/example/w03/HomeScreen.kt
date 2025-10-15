package com.example.w03

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier.Companion
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Companion.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Ninomae ina'nis",
            style = MaterialTheme.typography.headlineMedium.copy(color = Color(0xFF9C27B0)) // 보라색 적용
        )

        Spacer(modifier = Modifier.Companion.height(16.dp))

        Image(
            painter = painterResource(id = R.drawable.img),
            contentDescription = "ina image",
            modifier = Modifier.Companion
                .size(250.dp)
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.Companion.height(16.dp))

        Text(
            text = "귀여움",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.Companion.height(24.dp))

        Button(
            onClick = {
                val intent =
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/@NinomaeInanis"))
                context.startActivity(intent)
            }
        ) {
            Text("방송 보러가기")
        }
    }
}