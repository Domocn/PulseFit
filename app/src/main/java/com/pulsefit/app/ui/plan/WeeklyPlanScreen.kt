package com.pulsefit.app.ui.plan

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.pulsefit.app.data.model.PlannedDay
import com.pulsefit.app.data.model.PlannedDayType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyPlanScreen(
    onBack: () -> Unit,
    onNavigateToEquipmentSetup: () -> Unit,
    viewModel: WeeklyPlanViewModel = hiltViewModel()
) {
    val weeklyPlan by viewModel.weeklyPlan.collectAsState()
    val hasEquipmentProfile by viewModel.hasEquipmentProfile.collectAsState()
    val calendarSynced by viewModel.calendarSynced.collectAsState()
    val syncError by viewModel.syncError.collectAsState()
    val availableCalendars by viewModel.availableCalendars.collectAsState()
    val selectedCalendarId by viewModel.selectedCalendarId.collectAsState()
    val context = LocalContext.current
    var showCalendarPicker by remember { mutableStateOf(false) }
    var showTemplatePicker by remember { mutableStateOf(false) }
    var selectedDayIndex by remember { mutableStateOf(-1) }

    val calendarPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.all { it }
        if (granted) {
            viewModel.loadCalendars(context)
            showCalendarPicker = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Weekly Plan") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToEquipmentSetup) {
                        Icon(Icons.Default.Settings, contentDescription = "Equipment Setup")
                    }
                    IconButton(onClick = { viewModel.generatePlan() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Regenerate Plan")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Setup prompt if no equipment profile
            if (!hasEquipmentProfile) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Set Up Your Equipment",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                "Tell us what equipment you have for a more personalized plan.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = onNavigateToEquipmentSetup) {
                                Text("Set Up Equipment")
                            }
                        }
                    }
                }
            }

            // Plan header
            weeklyPlan?.let { plan ->
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "This Week",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        val workoutCount = plan.days.count { it.type == PlannedDayType.WORKOUT }
                        val restCount = plan.days.count { it.type == PlannedDayType.REST || it.type == PlannedDayType.ACTIVE_RECOVERY }
                        Text(
                            "$workoutCount workouts, $restCount rest",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Calendar sync button
                item {
                    OutlinedButton(
                        onClick = {
                            val hasPermission = ContextCompat.checkSelfPermission(
                                context, Manifest.permission.WRITE_CALENDAR
                            ) == PackageManager.PERMISSION_GRANTED
                            if (hasPermission) {
                                viewModel.loadCalendars(context)
                                showCalendarPicker = true
                            } else {
                                calendarPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.READ_CALENDAR,
                                        Manifest.permission.WRITE_CALENDAR
                                    )
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (calendarSynced) "Synced to Calendar" else "Sync to Calendar")
                        if (calendarSynced) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Synced",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Sync error message
                    syncError?.let { error ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                // Day cards
                items(plan.days.size) { index ->
                    val day = plan.days[index]
                    val canRemove = day.type == PlannedDayType.WORKOUT || day.type == PlannedDayType.CHALLENGE
                    PlannedDayCard(
                        day = day,
                        onClick = {
                            selectedDayIndex = index
                            showTemplatePicker = true
                        },
                        onRemove = if (canRemove) {{ viewModel.removeDay(context, index) }} else null
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    // Calendar picker dialog
    if (showCalendarPicker) {
        CalendarPickerDialog(
            calendars = availableCalendars,
            selectedId = selectedCalendarId,
            onSelect = { viewModel.selectCalendar(it) },
            onConfirm = {
                showCalendarPicker = false
                viewModel.syncToCalendar(context)
            },
            onDismiss = { showCalendarPicker = false }
        )
    }

    // Template picker bottom sheet
    if (showTemplatePicker) {
        TemplatePickerSheet(
            templates = viewModel.allTemplates,
            onSelect = { template ->
                viewModel.changeTemplate(context, selectedDayIndex, template)
                showTemplatePicker = false
            },
            onDismiss = { showTemplatePicker = false }
        )
    }
}

@Composable
private fun CalendarPickerDialog(
    calendars: List<com.pulsefit.app.util.DeviceCalendar>,
    selectedId: Long?,
    onSelect: (Long) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose Calendar") },
        text = {
            if (calendars.isEmpty()) {
                Text(
                    "Loading calendars...",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    calendars.forEach { cal ->
                        val isSelected = cal.id == selectedId
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics(mergeDescendants = true) {
                                    contentDescription = "${cal.displayName.ifEmpty { "Unnamed calendar" }}, ${cal.accountName}" +
                                        if (isSelected) ", selected" else ""
                                }
                                .clickable(role = Role.RadioButton) { onSelect(cal.id) }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (isSelected) Icons.Default.RadioButtonChecked
                                else Icons.Default.RadioButtonUnchecked,
                                contentDescription = if (isSelected) "Selected" else "Not selected",
                                tint = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = cal.displayName.ifEmpty { "Unnamed calendar" },
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = cal.accountName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = selectedId != null || calendars.isEmpty()
            ) {
                Text(if (calendars.isEmpty()) "Use Default" else "Sync")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun PlannedDayCard(
    day: PlannedDay,
    onClick: () -> Unit = {},
    onRemove: (() -> Unit)? = null
) {
    val (containerColor, contentColor) = when (day.type) {
        PlannedDayType.WORKOUT -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurface
        PlannedDayType.REST -> MaterialTheme.colorScheme.surface to MaterialTheme.colorScheme.onSurfaceVariant
        PlannedDayType.ACTIVE_RECOVERY -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        PlannedDayType.CHALLENGE -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
    }

    val icon = when (day.type) {
        PlannedDayType.WORKOUT -> Icons.Default.FitnessCenter
        PlannedDayType.REST -> Icons.Default.SelfImprovement
        PlannedDayType.ACTIVE_RECOVERY -> Icons.Default.SelfImprovement
        PlannedDayType.CHALLENGE -> Icons.Default.SportsScore
    }

    val dayDescription = buildString {
        append("${day.dayOfWeek.label}, ${day.type.label}")
        if (day.templateName != null) append(", ${day.templateName}")
        if (day.durationMinutes > 0) append(", ${day.durationMinutes} minutes")
        if (day.focus.isNotBlank()) append(", ${day.focus}")
        append(". Tap to change template")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                contentDescription = dayDescription
            }
            .clickable(role = Role.Button, onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = if (onRemove != null) 4.dp else 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = day.dayOfWeek.label,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                    Text(
                        text = day.type.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = contentColor.copy(alpha = 0.7f)
                    )
                }
                if (day.templateName != null) {
                    Text(
                        text = day.templateName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = contentColor
                    )
                }
                Text(
                    text = day.focus,
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.7f)
                )
                if (day.durationMinutes > 0) {
                    Text(
                        text = "${day.durationMinutes} min",
                        style = MaterialTheme.typography.labelSmall,
                        color = contentColor.copy(alpha = 0.5f)
                    )
                }
                if (day.calendarEventId != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = "In calendar",
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "In calendar",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            if (onRemove != null) {
                IconButton(onClick = onRemove) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Remove workout",
                        tint = contentColor.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
