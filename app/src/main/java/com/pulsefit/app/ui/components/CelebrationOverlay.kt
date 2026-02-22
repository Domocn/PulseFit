package com.pulsefit.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulsefit.app.adhd.CelebrationConfig
import com.pulsefit.app.data.model.CelebrationStyle
import kotlinx.coroutines.delay

@Composable
fun CelebrationOverlay(
    config: CelebrationConfig?,
    message: String = "Well done!",
    onDismiss: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(config) {
        if (config != null) {
            visible = true
            delay(config.durationMs)
            visible = false
            delay(300) // animation out
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    when (config?.style) {
                        CelebrationStyle.OVERKILL -> Icons.Default.EmojiEvents
                        else -> Icons.Default.CheckCircle
                    },
                    contentDescription = null,
                    tint = when (config?.style) {
                        CelebrationStyle.OVERKILL -> MaterialTheme.colorScheme.primary
                        CelebrationStyle.CALM -> MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.secondary
                    },
                    modifier = Modifier.height(
                        when (config?.style) {
                            CelebrationStyle.OVERKILL -> 80.dp
                            CelebrationStyle.CALM -> 40.dp
                            else -> 60.dp
                        }
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = message,
                    fontSize = when (config?.style) {
                        CelebrationStyle.OVERKILL -> 36.sp
                        CelebrationStyle.CALM -> 20.sp
                        else -> 28.sp
                    },
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
