package com.arunika.studysmart.data.repository

import com.arunika.studysmart.data.local.TaskDao
import com.arunika.studysmart.domain.model.Task
import com.arunika.studysmart.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
	private val taskDao : TaskDao

): TaskRepository {
	override suspend fun upsertTask(task : Task) {
		taskDao.upsertTask(task)
	}

	override suspend fun getTaskById(taskId : Int) : Task? {
		return taskDao.getTaskById(taskId)
	}

	override suspend fun deleteTask(taskId : Int) {
		taskDao.deleteTask(taskId)
	}

	override fun getAllUpcomingTasks() : Flow<List<Task>> {
		return taskDao.getAllTasks()
			.map { tasks -> tasks.filter {  it.isComplete.not() } }
			.map { tasks -> sortedList(tasks) }
	}

	override fun getUpcomingTasksForSubject(subjectId : Int) : Flow<List<Task>> {
		return taskDao.getTasksForSubject(subjectId)
			.map { tasks -> tasks.filter {  it.isComplete.not() } }
			.map { tasks -> sortedList(tasks) }
	}

	override fun getCompletedTasksForSubject(subjectId : Int) : Flow<List<Task>> {
		return taskDao.getTasksForSubject(subjectId)
			.map { tasks -> tasks.filter {  it.isComplete } }
			.map { tasks -> sortedList(tasks) }
	}
	private fun sortedList(tasks : List<Task>) : List<Task>{
		return tasks.sortedWith(compareBy<Task> { it.dueDate }.thenByDescending { it.priority} )

	}
}