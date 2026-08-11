package com.pulsefit.app.ui.social

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pulsefit.app.domain.model.GroupSession
import com.pulsefit.app.domain.model.GroupSessionParticipant
import com.pulsefit.app.domain.model.GroupSessionStatus
import com.pulsefit.app.domain.model.ParticipantStatus
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupSessionsScreen(
    viewModel: GroupSessionsViewModel,
    onStartWorkout: (String, String) -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var showCreateSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Group Workouts", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Button(
                        onClick = { showCreateSheet = true },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("+ Create")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        if (state.selectedSession != null) {
            SessionDetailView(
                session = state.selectedSession!!,
                participants = state.participants,
                isHost = state.selectedSession!!.hostUserId == "local_user",
                onBack = { viewModel.selectSession(state.selectedSession!!) }, // deselect
                onJoin = { viewModel.joinSession(state.selectedSession!!.id) },
                onLeave = { viewModel.leaveSession(state.selectedSession!!.id) },
                onCancel = { viewModel.cancelSession(state.selectedSession!!.id) },
                onStart = { viewModel.startSession(state.selectedSession!!.id) },
                onStartWorkout = { onStartWorkout(
                    state.selectedSession!!.templateName,
                    state.selectedSession!!.workoutTemplateJson
                ) },
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Upcoming Sessions",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (state.upcomingSessions.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No upcoming group sessions",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Create one to work out together with friends — live or at your own time",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(state.upcomingSessions) { session ->
                        SessionCard(
                            session = session,
                            isJoined = state.myParticipations.any { it.sessionId == session.id },
                            onClick = { viewModel.selectSession(session) },
                            onJoin = { viewModel.joinSession(session.id) },
                            onLeave = { viewModel.leaveSession(session.id) }
                        )
                    }
                }

                if (state.mySessions.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "My Sessions",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(state.mySessions) { session ->
                        SessionCard(
                            session = session,
                            isJoined = true,
                            onClick = { viewModel.selectSession(session) },
                            onJoin = { },
                            onLeave = { }
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    // Create session bottom sheet
    if (showCreateSheet) {
        CreateSessionSheet(
            onDismiss = { showCreateSheet = false },
            onCreate = { name, time, template, json, max, notes ->
                viewModel.createSession(name, time, template, json, max, notes)
                showCreateSheet = false
            }
        )
    }
}

@Composable
private fun SessionCard(
    session: GroupSession,
    isJoined: Boolean,
    onClick: () -> Unit,
    onJoin: () -> Unit,
    onLeave: () -> Unit
) {
    val formatter = remember { DateTimeFormatter.ofPattern("EEE dd MMM · HH:mm") }
    val dateStr = session.scheduledTime.atZone(ZoneId.systemDefault()).format(formatter)

    val statusColor = when (session.status) {
        GroupSessionStatus.LIVE -> Color(0xFF22C55E)
        GroupSessionStatus.SCHEDULED -> Color(0xFF3B82F6)
        GroupSessionStatus.COMPLETED -> Color(0xFF94A3B8)
        GroupSessionStatus.CANCELLED -> Color(0xFFEF4444)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = session.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = session.status.label,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = statusColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${session.participantCount}/${session.maxParticipants} joined",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (session.templateName.isNotBlank()) {
                        Text(
                            text = " · ${session.templateName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                if (session.status == GroupSessionStatus.SCHEDULED || session.status == GroupSessionStatus.LIVE) {
                    if (isJoined) {
                        OutlinedButton(onClick = onLeave) {
                            Text("Leave")
                        }
                    } else {
                        Button(onClick = onJoin) {
                            Text("Join")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionDetailView(
    session: GroupSession,
    participants: List<GroupSessionParticipant>,
    isHost: Boolean,
    onBack: () -> Unit,
    onJoin: () -> Unit,
    onLeave: () -> Unit,
    onCancel: () -> Unit,
    onStart: () -> Unit,
    onStartWorkout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = remember { DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy · HH:mm") }
    val dateStr = session.scheduledTime.atZone(ZoneId.systemDefault()).format(formatter)

    val statusColor = when (session.status) {
        GroupSessionStatus.LIVE -> Color(0xFF22C55E)
        GroupSessionStatus.SCHEDULED -> Color(0xFF3B82F6)
        GroupSessionStatus.COMPLETED -> Color(0xFF94A3B8)
        GroupSessionStatus.CANCELLED -> Color(0xFFEF4444)
    }

    LazyColumn(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        item {
            Spacer(modifier = Modifier.height(8.dp))

            // Back button
            OutlinedButton(onClick = onBack) {
                Text("← Back to sessions")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Session header
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = session.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = session.status.label,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = statusColor
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Host: ${session.hostName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (session.templateName.isNotBlank()) {
                        Text(
                            text = "Workout: ${session.templateName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    session.notes?.let { note ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = note,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        when (session.status) {
                            GroupSessionStatus.SCHEDULED -> {
                                if (isHost) {
                                    Button(onClick = onStart, modifier = Modifier.weight(1f)) {
                                        Text("Start Now")
                                    }
                                    OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
                                        Text("Cancel")
                                    }
                                } else {
                                    Button(onClick = onJoin, modifier = Modifier.weight(1f)) {
                                        Text("Join Session")
                                    }
                                }
                            }
                            GroupSessionStatus.LIVE -> {
                                Button(onClick = onStartWorkout, modifier = Modifier.weight(1f)) {
                                    Text("Start Workout")
                                }
                                OutlinedButton(onClick = onLeave, modifier = Modifier.weight(1f)) {
                                    Text("Leave")
                                }
                            }
                            else -> { /* No actions for completed/cancelled */ }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Participants
        item {
            Text(
                text = "Participants (${participants.size}/${session.maxParticipants})",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (participants.isEmpty()) {
            item {
                Text(
                    text = "No one has joined yet. Be the first!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        } else {
            items(participants) { participant ->
                ParticipantRow(participant = participant)
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun ParticipantRow(participant: GroupSessionParticipant) {
    val statusColor = when (participant.status) {
        ParticipantStatus.COMPLETED -> Color(0xFF22C55E)
        ParticipantStatus.IN_PROGRESS -> Color(0xFFF97316)
        ParticipantStatus.JOINED -> Color(0xFF3B82F6)
        ParticipantStatus.SKIPPED -> Color(0xFF94A3B8)
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = participant.userName.ifEmpty { "Participant" },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            participant.totalVolumeKg?.let { vol ->
                Text(
                    text = "${"%.0f".format(vol)}kg · ${participant.totalSets ?: 0} sets",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Text(
            text = participant.status.label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = statusColor
        )
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateSessionSheet(
    onDismiss: () -> Unit,
    onCreate: (String, Instant, String, String, Int, String?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var name by remember { mutableStateOf("") }
    var templateName by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var maxParticipants by remember { mutableStateOf("10") }
    // Default to tomorrow at 9am
    val defaultTime = remember {
        Instant.now().plusSeconds(86400).atZone(ZoneId.systemDefault())
            .withHour(9).withMinute(0).withSecond(0).toInstant()
    }
    var scheduledTime by remember { mutableStateOf(defaultTime) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text(
                text = "Create Group Session",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Session Name") },
                placeholder = { Text("e.g., Monday Push Day Crew") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = templateName,
                onValueChange = { templateName = it },
                label = { Text("Workout Template (optional)") },
                placeholder = { Text("e.g., Day 1: Endurance") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = maxParticipants,
                    onValueChange = { maxParticipants = it },
                    label = { Text("Max people") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                placeholder = { Text("e.g., We'll start together but you can do it later too") },
                maxLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onCreate(
                            name,
                            scheduledTime,
                            templateName.ifEmpty { name },
                            "", // workout JSON — would be populated from template
                            maxParticipants.toIntOrNull() ?: 10,
                            notes.ifEmpty { null }
                        )
                    }
                },
                enabled = name.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create Session")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
