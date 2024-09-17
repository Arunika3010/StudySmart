package com.arunika.studysmart.domain.repository

import com.arunika.studysmart.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

	suspend fun upsertTask(task: Task)

	suspend fun getTaskById(taskId: Int): Task?

	suspend fun deleteTask(taskId: Int)

	fun getAllUpcomingTasks(): Flow<List<Task>>

	fun getUpcomingTasksForSubject(subjectId: Int): Flow<List<Task>>

	fun getCompletedTasksForSubject(subjectId: Int): Flow<List<Task>>

}