package com.arunika.studysmart.presentation.task

import com.arunika.studysmart.domain.model.Subject
import com.arunika.studysmart.util.Priority


data class TaskState(
	val title: String = "" ,
	val description: String = "" ,
	val dueDate: Long? = null ,
	val isTaskComplete: Boolean = false ,
	val priority: Priority = Priority.LOW ,
	val relatedSubject: String? = null ,
	val subjects: List<Subject> = emptyList() ,
	val subjectId: Int? = null ,
	val currentTaskId: Int? = null
)
