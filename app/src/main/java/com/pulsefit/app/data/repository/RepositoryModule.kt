package com.pulsefit.app.data.repository

import com.pulsefit.app.domain.repository.BodyMeasurementRepository
import com.pulsefit.app.domain.repository.ExerciseLogRepository
import com.pulsefit.app.domain.repository.GroupSessionRepository
import com.pulsefit.app.domain.repository.MuscleFatigueRepository
import com.pulsefit.app.domain.repository.PersonalRecordRepository
import com.pulsefit.app.domain.repository.UserRepository
import com.pulsefit.app.domain.repository.WorkoutRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindWorkoutRepository(impl: WorkoutRepositoryImpl): WorkoutRepository

    @Binds
    @Singleton
    abstract fun bindExerciseLogRepository(impl: ExerciseLogRepositoryImpl): ExerciseLogRepository

    @Binds
    @Singleton
    abstract fun bindBodyMeasurementRepository(impl: BodyMeasurementRepositoryImpl): BodyMeasurementRepository

    @Binds
    @Singleton
    abstract fun bindPersonalRecordRepository(impl: PersonalRecordRepositoryImpl): PersonalRecordRepository

    @Binds
    @Singleton
    abstract fun bindMuscleFatigueRepository(impl: MuscleFatigueRepositoryImpl): MuscleFatigueRepository

    @Binds
    @Singleton
    abstract fun bindGroupSessionRepository(impl: GroupSessionRepositoryImpl): GroupSessionRepository
}
