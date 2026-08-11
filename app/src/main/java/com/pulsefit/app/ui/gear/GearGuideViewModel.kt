package com.pulsefit.app.ui.gear

import androidx.lifecycle.ViewModel
import com.pulsefit.app.data.exercise.GearGuideRegistry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class GearGuideViewModel @Inject constructor(
    private val registry: GearGuideRegistry
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<GearGuideRegistry.GearCategory?>(null)
    val selectedCategory: StateFlow<GearGuideRegistry.GearCategory?> = _selectedCategory

    val allItems: List<GearGuideRegistry.GearItem> = registry.getAll()

    fun selectCategory(category: GearGuideRegistry.GearCategory?) {
        _selectedCategory.value = category
    }

    fun getFilteredItems(): List<GearGuideRegistry.GearItem> {
        val cat = _selectedCategory.value
        return if (cat != null) registry.getByCategory(cat) else allItems
    }
}
