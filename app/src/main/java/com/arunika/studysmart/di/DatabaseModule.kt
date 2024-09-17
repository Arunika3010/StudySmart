package com.arunika.studysmart.di

import android.app.Application
import androidx.room.Room
import com.arunika.studysmart.data.local.AppDatabase
import com.arunika.studysmart.data.local.SessionDao
import com.arunika.studysmart.data.local.SubjectDao
import com.arunika.studysmart.data.local.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

	@Provides
	@Singleton
	fun provideDatabase(
		application: Application
	): AppDatabase{
		return Room.databaseBuilder(
			application,
			AppDatabase::class.java,
			"studysmart.db"
		)
			.build()

	}

	@Provides
	@Singleton
	fun provideSubjectDao(database: AppDatabase): SubjectDao {
		return database.subjectDao()
	}

	@Provides
	@Singleton
	fun taskSubjectDao(database: AppDatabase): TaskDao {
		return database.taskDao()
	}

	@Provides
	@Singleton
	fun sessionSubjectDao(database: AppDatabase): SessionDao {
		return database.sessionDao()
	}

}