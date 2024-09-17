package com.arunika.studysmart.presentation.task

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arunika.studysmart.domain.model.Task
import com.arunika.studysmart.domain.repository.SubjectRepository
import com.arunika.studysmart.domain.repository.TaskRepository
import com.arunika.studysmart.presentation.navArgs
import com.arunika.studysmart.util.Priority
import com.arunika.studysmart.util.SnackBarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
	private val taskRepository: TaskRepository,
	private val subjectRepository: SubjectRepository,
	savedStateHandle : SavedStateHandle
): ViewModel() {

	private val _state = MutableStateFlow(TaskState())
	private val _snackBarEventFlow = MutableSharedFlow<SnackBarEvent>()
	private val navArgs : TaskScreenNavArgs = savedStateHandle.navArgs()
	val snackBarEventFlow = _snackBarEventFlow.asSharedFlow()
	val state = combine(
		_state,
		subjectRepository.getAllSubjects()
	){ state, subjects ->
		state.copy(
			subjects = subjects
		)
	}.stateIn(
		viewModelScope,
		SharingStarted.WhileSubscribed(5000),
		TaskState()
	)
	init {
		fetchTask()
		fetchSubject()
	}

	fun onEvent(event: TaskEvent){
		when(event){
			is TaskEvent.OnTitleChange -> {
				_state.update {
					it.copy(
						title = event.title
					)
				}
			}
			is TaskEvent.OnDescriptionChange -> {
				_state.update {
					it.copy(
						description = event.description
					)
				}
			}
			is TaskEvent.OnDueDateChange -> {
				_state.update {
					it.copy(
						dueDate = event.dueDate
					)
				}
			}
			TaskEvent.OnIsCompleteChange -> {
				_state.update {
					it.copy(
						isTaskComplete = !it.isTaskComplete
					)
				}
			}
			is TaskEvent.OnPriorityChange -> {
				_state.update {
					it.copy(
						priority = event.priority
					)
				}
			}
			is TaskEvent.OnRelatedSubjectSelect ->{
				_state.update {
					it.copy(
						relatedSubject = event.subject.name,
						subjectId = event.subject.subjectId
					)
				}
			}

			TaskEvent.SaveTask -> saveTask()
			TaskEvent.DeleteTask -> deleteTask()
		}
	}

	private fun deleteTask() {
		viewModelScope.launch {
			try {
				val currentTaskId = state.value.currentTaskId
				if (currentTaskId != null) {
					withContext(Dispatchers.IO) {
						taskRepository.deleteTask(currentTaskId)
					}
					_snackBarEventFlow.emit(
						SnackBarEvent.ShowSnackBar(
							message = "Task Deleted Successfully"
						)
					)
					_snackBarEventFlow.emit(
						SnackBarEvent.NavigateUp
					)
				}else{
					_snackBarEventFlow.emit(
						SnackBarEvent.ShowSnackBar(
							message = "No Task to delete"
						)
					)
				}


			}catch (e : Exception){
				_snackBarEventFlow.emit(
					SnackBarEvent.ShowSnackBar(
						message = e.message ?: "Couldn't delete task. ${e.localizedMessage}",
						duration = SnackbarDuration.Long
					)
				)
			}
		}
	}

	private fun saveTask() {
		viewModelScope.launch {
			val state = state.value
			if(state.subjectId == null || state.relatedSubject == null){
				_snackBarEventFlow.emit(
					SnackBarEvent.ShowSnackBar(
						message = "Please select subject related to the task"
					)
				)
				return@launch
			}
			try {
				taskRepository.upsertTask(
					task = Task(
						taskId = state.currentTaskId ,
						taskSubjectId = state.subjectId ,
						title = state.title ,
						description = state.description ,
						dueDate = state.dueDate ?: Instant.now().toEpochMilli() ,
						priority = state.priority.value ,
						relatedToSubject = state.relatedSubject ,
						isComplete = state.isTaskComplete
					)

				)
				_snackBarEventFlow.emit(
					SnackBarEvent.ShowSnackBar(
						message = "Task Saved Successfully"
					)
				)
				_snackBarEventFlow.emit(
					SnackBarEvent.NavigateUp
				)
			}catch (e: Exception){
				_snackBarEventFlow.emit(
					SnackBarEvent.ShowSnackBar(
						message = e.message ?: "Couldn't save task. ${e.localizedMessage}"
					)
				)

			}
		}
	}

	private fun fetchTask(){
		viewModelScope.launch {
			navArgs.taskId?.let { taskId ->
				taskRepository.getTaskById(taskId)?.let { task ->
					_state.update {
						it.copy(
							title = task.title,
							description = task.description,
							dueDate = task.dueDate,
							isTaskComplete = task.isComplete,
							priority = Priority.fromInt(task.priority),
							relatedSubject = task.relatedToSubject,
							subjectId = task.taskSubjectId,
							currentTaskId = task.taskId
						)
					}
				}

			}

		}

	}

	private fun fetchSubject(){
		viewModelScope.launch {
			navArgs.subjectId?.let { id ->
				subjectRepository.getSubjectById(id)?.let { subject ->
					_state.update {
						it.copy(
							relatedSubject = subject.name,
							subjectId = subject.subjectId,
						)
					}
				}

			}
		}
	}


}