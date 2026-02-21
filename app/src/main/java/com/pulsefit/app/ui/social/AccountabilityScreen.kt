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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.pulsefit.app.data.remote.model.AccountabilityContract

@Composable
fun AccountabilityScreen(
    viewModel: AccountabilityViewModel = hiltViewModel()
) {
    val contracts by viewModel.contracts.collectAsState()
    val selectedProgress by viewModel.selectedProgress.collectAsState()
    val showCreateDialog by viewModel.showCreateDialog.collectAsState()
    val friends by viewModel.friends.collectAsState()
    var selectedContract by remember { mutableStateOf<AccountabilityContract?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Accountability",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "We're in this together",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = viewModel::showCreate,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("New Contract")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (contracts.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No active contracts",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Pair up with a friend to keep each other accountable",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(contracts) { contract ->
                    ContractCard(
                        contract = contract,
                        isSelected = selectedContract?.id == contract.id,
                        progress = if (selectedContract?.id == contract.id) selectedProgress else null,
                        onClick = {
                            selectedContract = contract
                            viewModel.selectContract(contract.id)
                        },
                        onCancel = { viewModel.cancelContract(contract.id) }
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateContractDialog(
            friends = friends.map { it.uid to it.displayName },
            onDismiss = viewModel::hideCreate,
            onCreate = { friendUid, friendName, goal ->
                viewModel.createContract(friendUid, friendName, goal)
            }
        )
    }
}

@Composable
private fun ContractCard(
    contract: AccountabilityContract,
    isSelected: Boolean,
    progress: com.pulsefit.app.data.remote.model.WeeklyProgress?,
    onClick: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "With ${contract.partnerName}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${contract.weeklyGoal}x/week",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (isSelected && progress != null) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "You",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LinearProgressIndicator(
                        progress = { (progress.myCount.toFloat() / progress.weeklyGoal).coerceIn(0f, 1f) },
                        modifier = Modifier.weight(1f).height(8.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${progress.myCount}/${progress.weeklyGoal}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = contract.partnerName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LinearProgressIndicator(
                        progress = { (progress.partnerCount.toFloat() / progress.weeklyGoal).coerceIn(0f, 1f) },
                        modifier = Modifier.weight(1f).height(8.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${progress.partnerCount}/${progress.weeklyGoal}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (progress.myCount >= progress.weeklyGoal && progress.partnerCount >= progress.weeklyGoal) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Both hit the goal this week!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = onCancel) {
                    Text("End Contract", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun CreateContractDialog(
    friends: List<Pair<String, String>>,
    onDismiss: () -> Unit,
    onCreate: (friendUid: String, friendName: String, weeklyGoal: Int) -> Unit
) {
    var selectedFriend by remember { mutableStateOf<Pair<String, String>?>(null) }
    var weeklyGoal by remember { mutableIntStateOf(3) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "New Accountability Contract",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Choose a friend",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (friends.isEmpty()) {
                    Text(
                        text = "Add friends first to create a contract",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    friends.forEach { (uid, name) ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedFriend = uid to name },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedFriend?.first == uid)
                                    MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(
                                text = name,
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Weekly goal: $weeklyGoal workouts",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Slider(
                    value = weeklyGoal.toFloat(),
                    onValueChange = { weeklyGoal = it.toInt() },
                    valueRange = 1f..7f,
                    steps = 5,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            selectedFriend?.let { (uid, name) ->
                                onCreate(uid, name, weeklyGoal)
                            }
                        },
                        enabled = selectedFriend != null
                    ) {
                        Text("Create")
                    }
                }
            }
        }
    }
}
