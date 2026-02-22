package com.pulsefit.app.ui.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefit.app.data.remote.GroupRepository
import com.pulsefit.app.data.remote.model.WorkoutGroup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupListViewModel @Inject constructor(
    private val groupRepository: GroupRepository
) : ViewModel() {

    private val _groups = MutableStateFlow<List<WorkoutGroup>>(emptyList())
    val groups: StateFlow<List<WorkoutGroup>> = _groups

    init {
        viewModelScope.launch {
            groupRepository.getMyGroups().collect { _groups.value = it }
        }
    }
}
