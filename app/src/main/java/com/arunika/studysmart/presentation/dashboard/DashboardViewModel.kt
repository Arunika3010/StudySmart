package com.arunika.studysmart.presentation.dashboard

import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arunika.studysmart.domain.model.Session
import com.arunika.studysmart.domain.model.Subject
import com.arunika.studysmart.domain.model.Task
import com.arunika.studysmart.domain.repository.SessionRepository
import com.arunika.studysmart.domain.repository.SubjectRepository
import com.arunika.studysmart.domain.repository.TaskRepository
import com.arunika.studysmart.util.SnackBarEvent
import com.arunika.studysmart.util.toHours
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
	private val subjectRepository: SubjectRepository,
	private val sessionRepository: SessionRepository,
	private val taskRepository: TaskRepository
): ViewModel() {
	private val _state = MutableStateFlow(DashboardState())
	val state = combine(
		_state,
		subjectRepository.getTotalSubjectCount(),
		subjectRepository.getTotalGoalHours(),
		subjectRepository.getAllSubjects(),
		sessionRepository.getTotalSessionsDuration()
	){ state, totalSubjectCount, totalGoalHours, subjects, totalSessionsDuration ->
		state.copy(
			totalSubjectCount = totalSubjectCount,
			totalGoalHours = totalGoalHours,
			subjects = subjects,
			totalStudiedHours = totalSessionsDuration.toHours()
		)

	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5000),
		initialValue = DashboardState()
	)

	val tasks : StateFlow<List<Task>> = taskRepository.getAllUpcomingTasks().stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5000),
		initialValue = emptyList()
	)

	val sessions : StateFlow<List<Session>> = sessionRepository.getRecentFiveSessions().stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5000),
		initialValue = emptyList()
	)
	private val _snackBarEventFlow = MutableSharedFlow<SnackBarEvent>()
	val snackBarEventFlow = _snackBarEventFlow.asSharedFlow()

	fun onEvent(event : DashboardEvent){
		when(event){
			DashboardEvent.DeleteSession -> deleteSession()
			is DashboardEvent.OnDeleteSessionButtonClick -> {
				_state.update {
					it.copy(
						session = event.session
					)
				}
			}
			is DashboardEvent.OnGoalStudyHoursChange -> {
				_state.update {
					it.copy(
						goalStudyHours = event.hours
					)
				}
			}
			is DashboardEvent.OnSubjectCardColorChange -> {
				_state.update {
					it.copy(
						subjectCardColors = event.colors
					)
				}
			}
			is DashboardEvent.OnSubjectNameChange -> {
				_state.update {
					it.copy(
						subjectName = event.name
					)
				}
			}
			is DashboardEvent.OnTaskIsCompleteChange -> {
				updateTask(event.task)
			}
			DashboardEvent.SaveSubject -> saveSubject()
		}
	}

	private fun updateTask(task : Task) {

		viewModelScope.launch {
			try{
				taskRepository.upsertTask(
					task = task.copy(
						isComplete = !task.isComplete
					)
				)
				_snackBarEventFlow.emit(
					SnackBarEvent.ShowSnackBar(
						message = "Saved in Completed Tasks"
					)
				)
			}catch (e: Exception){
				_snackBarEventFlow.emit(
					SnackBarEvent.ShowSnackBar(
						message = e.message ?: "Couldn't update task. ${e.localizedMessage}",
						duration = SnackbarDuration.Long
					)
				)
			}


		}

	}

	private fun saveSubject() {
		viewModelScope.launch {
			try{
				subjectRepository.upsertSubject(
					subject = Subject(
						name = state.value.subjectName,
						goalHours = state.value.goalStudyHours.toFloatOrNull() ?: 1f,
						colors = state.value.subjectCardColors.map { it.toArgb() }

					)
				)
				_state.update {
					it.copy(
						subjectName = "",
						goalStudyHours = "",
						subjectCardColors = Subject.subjectCardColors.random()
					)
				}
				_snackBarEventFlow.emit(
					SnackBarEvent.ShowSnackBar(
						message = "Subject Saved Successfully"
					)
				)
			}catch (e: Exception){
				_snackBarEventFlow.emit(
					SnackBarEvent.ShowSnackBar(
						message = e.message ?: "Couldn't save subject. ${e.localizedMessage}",
						duration = SnackbarDuration.Long
					)
				)
			}


		}
	}

	private fun deleteSession(){
		viewModelScope.launch {
			try {
				state.value.session?.let {
					sessionRepository.deleteSession(it)
					_snackBarEventFlow.emit(
						SnackBarEvent.ShowSnackBar(message = "Session Deleted Successfully.")
					)
				}

			}catch (e : Exception){
				_snackBarEventFlow.emit(
					SnackBarEvent.ShowSnackBar(
						message = e.message ?: "Couldn't delete session. ${e.localizedMessage}" ,
						duration = SnackbarDuration.Long
					)
				)
			}
		}
	}

}