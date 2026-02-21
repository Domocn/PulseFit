package com.pulsefit.app.ui.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefit.app.data.remote.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _displayName = MutableStateFlow("")
    val displayName: StateFlow<String> = _displayName

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _signInSuccess = MutableStateFlow(false)
    val signInSuccess: StateFlow<Boolean> = _signInSuccess

    fun updateEmail(value: String) { _email.value = value }
    fun updatePassword(value: String) { _password.value = value }
    fun updateDisplayName(value: String) { _displayName.value = value }
    fun clearError() { _error.value = null }

    fun signIn() {
        viewModelScope.launch {
            val emailTrimmed = _email.value.trim()
            if (!isValidEmail(emailTrimmed)) {
                _error.value = "Please enter a valid email address"
                return@launch
            }
            _isLoading.value = true
            _error.value = null
            val result = authRepository.signInWithEmail(emailTrimmed, _password.value)
            result.fold(
                onSuccess = { _signInSuccess.value = true },
                onFailure = { _error.value = it.message ?: "Sign in failed" }
            )
            _isLoading.value = false
        }
    }

    fun signUp() {
        viewModelScope.launch {
            val emailTrimmed = _email.value.trim()
            val nameTrimmed = _displayName.value.trim()

            if (!isValidEmail(emailTrimmed)) {
                _error.value = "Please enter a valid email address"
                return@launch
            }
            val passwordError = validatePassword(_password.value)
            if (passwordError != null) {
                _error.value = passwordError
                return@launch
            }
            if (nameTrimmed.length < 2) {
                _error.value = "Display name must be at least 2 characters"
                return@launch
            }

            _isLoading.value = true
            _error.value = null
            val result = authRepository.signUpWithEmail(emailTrimmed, _password.value, nameTrimmed)
            result.fold(
                onSuccess = { _signInSuccess.value = true },
                onFailure = { _error.value = it.message ?: "Sign up failed" }
            )
            _isLoading.value = false
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = authRepository.signInWithGoogle(idToken)
            result.fold(
                onSuccess = { _signInSuccess.value = true },
                onFailure = { _error.value = it.message ?: "Google sign in failed" }
            )
            _isLoading.value = false
        }
    }

    private fun isValidEmail(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun validatePassword(password: String): String? = when {
        password.length < 8 -> "Password must be at least 8 characters"
        !password.any { it.isDigit() } -> "Password must contain at least one number"
        !password.any { it.isUpperCase() } -> "Password must contain at least one uppercase letter"
        else -> null
    }
}
