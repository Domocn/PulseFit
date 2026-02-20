package com.pulsefit.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pulsefit.app.ble.ConnectionStatus
import com.pulsefit.app.ui.theme.ZonePeak
import com.pulsefit.app.ui.theme.ZoneRest
import com.pulsefit.app.ui.theme.ZoneWarmUp

@Composable
fun ConnectionStatusIndicator(status: ConnectionStatus, modifier: Modifier = Modifier) {
    val (color, label) = when (status) {
        ConnectionStatus.DISCONNECTED -> ZoneRest to "No HR"
        ConnectionStatus.SCANNING -> ZoneWarmUp to "Scanning"
        ConnectionStatus.CONNECTING -> ZoneWarmUp to "Connecting"
        ConnectionStatus.CONNECTED -> MaterialTheme.colorScheme.secondary to "Connected"
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
