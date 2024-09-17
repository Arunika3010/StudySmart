package com.arunika.studysmart.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.arunika.studysmart.domain.model.Session
import com.arunika.studysmart.domain.model.Subject
import com.arunika.studysmart.domain.model.Task

@Database(
	entities = [Subject::class, Task::class, Session::class],
	version = 1,
	exportSchema = false
)

@TypeConverters(ColorListConverter::class)
abstract class AppDatabase: RoomDatabase() {
	abstract fun subjectDao(): SubjectDao
	abstract fun taskDao(): TaskDao
	abstract fun sessionDao(): SessionDao

}