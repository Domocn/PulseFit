package com.example.pulsefit.domain.usecase

import com.example.pulsefit.domain.model.UserProfile
import com.example.pulsefit.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<UserProfile?> = userRepository.getUserProfile()

    suspend fun once(): UserProfile? = userRepository.getUserProfileOnce()
}
