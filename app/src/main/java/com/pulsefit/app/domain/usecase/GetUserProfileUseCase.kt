package com.pulsefit.app.domain.usecase

import com.pulsefit.app.domain.model.UserProfile
import com.pulsefit.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<UserProfile?> = userRepository.getUserProfile()

    suspend fun once(): UserProfile? = userRepository.getUserProfileOnce()
}
