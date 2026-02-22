package com.pulsefit.app.ui.plan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.pulsefit.app.data.model.DayOfWeek
import com.pulsefit.app.data.model.Equipment
import com.pulsefit.app.data.model.EquipmentProfile
import com.pulsefit.app.data.model.WorkoutEnvironment
import com.pulsefit.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EquipmentSetupViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val gson = Gson()

    private val _environment = MutableStateFlow(WorkoutEnvironment.GYM)
    val environment: StateFlow<WorkoutEnvironment> = _environment

    private val _selectedEquipment = MutableStateFlow<Set<Equipment>>(
        Equipment.entries.filter { it.defaultForGym }.toSet()
    )
    val selectedEquipment: StateFlow<Set<Equipment>> = _selectedEquipment

    private val _selectedDays = MutableStateFlow<Set<DayOfWeek>>(
        setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)
    )
    val selectedDays: StateFlow<Set<DayOfWeek>> = _selectedDays

    private val _workoutsPerWeek = MutableStateFlow(4)
    val workoutsPerWeek: StateFlow<Int> = _workoutsPerWeek

    private val _preferredDuration = MutableStateFlow(30)
    val preferredDuration: StateFlow<Int> = _preferredDuration

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    init {
        viewModelScope.launch {
            val profile = userRepository.getUserProfileOnce() ?: return@launch
            val equipProfile = profile.getEquipmentProfile()
            _environment.value = equipProfile.environment
            _selectedEquipment.value = equipProfile.availableEquipment
            _selectedDays.value = equipProfile.preferredWorkoutDays
            _workoutsPerWeek.value = equipProfile.workoutsPerWeek
            _preferredDuration.value = equipProfile.preferredDurationMinutes
        }
    }

    fun setEnvironment(env: WorkoutEnvironment) {
        _environment.value = env
        // Apply default equipment for the selected environment
        _selectedEquipment.value = when (env) {
            WorkoutEnvironment.GYM -> Equipment.entries.filter { it.defaultForGym }.toSet()
            WorkoutEnvironment.HOME -> Equipment.entries.filter { it.defaultForHome }.toSet()
            WorkoutEnvironment.OUTDOOR -> setOf(Equipment.BODYWEIGHT)
            WorkoutEnvironment.HOTEL -> setOf(Equipment.BODYWEIGHT, Equipment.RESISTANCE_BANDS)
        }
    }

    fun toggleEquipment(equip: Equipment) {
        val current = _selectedEquipment.value.toMutableSet()
        if (equip in current) current.remove(equip) else current.add(equip)
        // Always keep bodyweight
        current.add(Equipment.BODYWEIGHT)
        _selectedEquipment.value = current
    }

    fun toggleDay(day: DayOfWeek) {
        val current = _selectedDays.value.toMutableSet()
        if (day in current) current.remove(day) else current.add(day)
        _selectedDays.value = current
    }

    fun setWorkoutsPerWeek(count: Int) {
        _workoutsPerWeek.value = count.coerceIn(1, 7)
    }

    fun setPreferredDuration(minutes: Int) {
        _preferredDuration.value = minutes.coerceIn(10, 60)
    }

    fun save() {
        viewModelScope.launch {
            _isSaving.value = true
            val equipProfile = EquipmentProfile(
                environment = _environment.value,
                availableEquipment = _selectedEquipment.value,
                preferredWorkoutDays = _selectedDays.value,
                workoutsPerWeek = _workoutsPerWeek.value,
                preferredDurationMinutes = _preferredDuration.value
            )
            val json = gson.toJson(equipProfile)
            val profile = userRepository.getUserProfileOnce()
            if (profile != null) {
                userRepository.saveUserProfile(profile.copy(equipmentProfileJson = json))
            }
            _isSaving.value = false
        }
    }
}
