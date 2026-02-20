package com.pulsefit.app.domain.usecase

import com.pulsefit.app.domain.model.UserProfile
import com.pulsefit.app.domain.repository.UserRepository
import javax.inject.Inject

class SaveUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(profile: UserProfile) {
        userRepository.saveUserProfile(profile)
    }
}
