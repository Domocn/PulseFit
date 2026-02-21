package com.pulsefit.app.ui.group

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pulsefit.app.data.remote.model.GroupType

@Composable
fun CreateGroupScreen(
    onGroupCreated: () -> Unit,
    viewModel: CreateGroupViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(GroupType.FAMILY) }
    val isCreating by viewModel.isCreating.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Create Group",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Group Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Group Type",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            FilterChip(
                selected = selectedType == GroupType.FAMILY,
                onClick = { selectedType = GroupType.FAMILY },
                label = { Text("Family") }
            )
            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            FilterChip(
                selected = selectedType == GroupType.CORPORATE,
                onClick = { selectedType = GroupType.CORPORATE },
                label = { Text("Corporate") }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (selectedType == GroupType.FAMILY)
                "Encouragement-focused. Up to 10 members."
            else
                "Includes leaderboard and team stats. Up to 50 members.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description (optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4
        )

        if (error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error!!,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { viewModel.createGroup(name, selectedType, description, onGroupCreated) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !isCreating && name.isNotBlank()
        ) {
            Text(if (isCreating) "Creating..." else "Create Group")
        }
    }
}
