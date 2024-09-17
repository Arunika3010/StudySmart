package com.arunika.studysmart.di

import com.arunika.studysmart.data.repository.SessionRepositoryImpl
import com.arunika.studysmart.data.repository.SubjectRepositoryImpl
import com.arunika.studysmart.data.repository.TaskRepositoryImpl
import com.arunika.studysmart.domain.repository.SessionRepository
import com.arunika.studysmart.domain.repository.SubjectRepository
import com.arunika.studysmart.domain.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

	@Singleton
	@Binds
	abstract fun bindSubjectRepository(
		impl: SubjectRepositoryImpl
	): SubjectRepository

	@Singleton
	@Binds
	abstract fun bindTaskRepository(
		impl: TaskRepositoryImpl
	): TaskRepository

	@Singleton
	@Binds
	abstract fun bindSessionRepository(
		impl: SessionRepositoryImpl
	): SessionRepository


}