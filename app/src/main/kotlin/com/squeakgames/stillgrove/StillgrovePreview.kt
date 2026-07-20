package com.squeakgames.stillgrove

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.squeakgames.stillgrove.render.AmbientSoundscape
import com.squeakgames.stillgrove.render.BiomeHealth

@Composable
fun StillgrovePreview(
    soundscape: AmbientSoundscape = remember { AmbientSoundscape() },
) {
    val biomeHealth by soundscape.currentBiome.collectAsState()
    val activeLayers by soundscape.activeLayers.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF0D0D1A),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BiomeStatusCard(biomeHealth)
            Spacer(modifier = Modifier.height(16.dp))
            SoundLayersPanel(layers = activeLayers)
            Spacer(modifier = Modifier.height(16.dp))
            BiomeIndicator(biomeHealth)
        }
    }
}

@Composable
private fun BiomeStatusCard(health: BiomeHealth) {
    val color = when (health) {
        BiomeHealth.THRIVING -> Color(0xFF4CAF50)
        BiomeHealth.STABLE -> Color(0xFF81C784)
        BiomeHealth.ENTROPIC -> Color(0xFFFF9800)
        BiomeHealth.DEEPLY_ENTROPIC -> Color(0xFFE53935)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.15f),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Biome: ${health.name}",
                style = MaterialTheme.typography.headlineSmall,
                color = color,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = when (health) {
                    BiomeHealth.THRIVING -> "The grove is flourishing."
                    BiomeHealth.STABLE -> "The grove is calm."
                    BiomeHealth.ENTROPIC -> "The grove is fading."
                    BiomeHealth.DEEPLY_ENTROPIC -> "The grove needs your care."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f),
            )
        }
    }
}

@Composable
private fun SoundLayersPanel(layers: List<AmbientSoundscape.SoundLayer>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.05f),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Soundscape Layers",
                style = MaterialTheme.typography.titleSmall,
                color = Color.White.copy(alpha = 0.6f),
            )
            Spacer(modifier = Modifier.height(8.dp))
            layers.forEach { layer ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = layer.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f),
                    )
                    Text(
                        text = "${layer.frequency.toInt()}Hz",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.5f),
                    )
                }
            }
        }
    }
}

@Composable
private fun BiomeIndicator(health: BiomeHealth) {
    val indicatorColor = when (health) {
        BiomeHealth.THRIVING -> Color(0xFF4CAF50)
        BiomeHealth.STABLE -> Color(0xFF81C784)
        BiomeHealth.ENTROPIC -> Color(0xFFFF9800)
        BiomeHealth.DEEPLY_ENTROPIC -> Color(0xFFE53935)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(4.dp)
                .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(2.dp)),
        ) {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(4.dp)
                    .background(indicatorColor, RoundedCornerShape(2.dp)),
            )
        }
    }
}

private val AmbientSoundscape.SoundLayer.name: String get() = this.name
