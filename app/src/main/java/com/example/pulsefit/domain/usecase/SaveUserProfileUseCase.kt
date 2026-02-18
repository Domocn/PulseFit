package com.example.pulsefit.domain.usecase

import com.example.pulsefit.domain.model.UserProfile
import com.example.pulsefit.domain.repository.UserRepository
import javax.inject.Inject

class SaveUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(profile: UserProfile) {
        userRepository.saveUserProfile(profile)
    }
}
