package com.pulsefit.app.ui.recovery

import androidx.lifecycle.ViewModel
import com.pulsefit.app.data.exercise.RecoveryContentRegistry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class RecoveryContentViewModel @Inject constructor(
    private val registry: RecoveryContentRegistry
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<RecoveryContentRegistry.RecoveryCategory?>(null)
    val selectedCategory: StateFlow<RecoveryContentRegistry.RecoveryCategory?> = _selectedCategory

    val allItems: List<RecoveryContentRegistry.RecoveryItem> = registry.getAll()

    fun selectCategory(category: RecoveryContentRegistry.RecoveryCategory?) {
        _selectedCategory.value = category
    }

    fun getFilteredItems(): List<RecoveryContentRegistry.RecoveryItem> {
        val cat = _selectedCategory.value
        return if (cat != null) registry.getByCategory(cat) else allItems
    }
}
