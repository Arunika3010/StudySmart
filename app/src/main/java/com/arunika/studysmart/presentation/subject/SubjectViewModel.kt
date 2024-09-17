package com.arunika.studysmart.presentation.subject

import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arunika.studysmart.domain.model.Subject
import com.arunika.studysmart.domain.model.Task
import com.arunika.studysmart.domain.repository.SessionRepository
import com.arunika.studysmart.domain.repository.SubjectRepository
import com.arunika.studysmart.domain.repository.TaskRepository
import com.arunika.studysmart.presentation.navArgs
import com.arunika.studysmart.util.SnackBarEvent
import com.arunika.studysmart.util.toHours
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
import javax.inject.Inject

@HiltViewModel
class SubjectViewModel @Inject constructor(
	private val subjectRepository : SubjectRepository ,
	private val sessionRepository : SessionRepository ,
	private val taskRepository : TaskRepository ,
	savedStateHandle : SavedStateHandle
) : ViewModel() {
	private val navArgs : SubjectScreenNavArgs = savedStateHandle.navArgs()


	private val _state = MutableStateFlow(SubjectState())
	val state = combine(
		_state ,
		taskRepository.getUpcomingTasksForSubject(navArgs.subjectId) ,
		taskRepository.getCompletedTasksForSubject(navArgs.subjectId) ,
		sessionRepository.getRecentTenSessionsForSubject(navArgs.subjectId) ,
		sessionRepository.getTotalSessionsDurationBySubject(navArgs.subjectId)
	) { state , upcomingTasks , completedTasks , recentSessions , totalSessionsDuration ->
		state.copy(
			upcomingTasks = upcomingTasks ,
			completedTasks = completedTasks ,
			recentSessions = recentSessions ,
			studiedHours = totalSessionsDuration.toHours()
		)
	}.stateIn(
		viewModelScope ,
		SharingStarted.WhileSubscribed(5000) ,
		SubjectState()
	)
	private val _snackBarEventFlow = MutableSharedFlow<SnackBarEvent>()
	val snackBarEventFlow = _snackBarEventFlow.asSharedFlow()

	init {
		fetchSubject()
	}

	fun onEvent(event : SubjectEvent){
		when(event) {
			is SubjectEvent.OnGoalStudyHoursChange -> {
				_state.update {
					it.copy(
						goalStudyHours = event.hours
					)
				}
			}
			is SubjectEvent.OnSubjectCardColorChange -> {
				_state.update {
					it.copy(
						subjectCardColors = event.colors
					)
				}
			}
			is SubjectEvent.OnSubjectNameChange -> {
				_state.update {
					it.copy(
						subjectName = event.name
					)
				}
			}
			SubjectEvent.UpdateSubject -> updateSubject()
			SubjectEvent.DeleteSession -> deleteSession()
			SubjectEvent.DeleteSubject -> deleteSubject()
			is SubjectEvent.OnDeleteSessionButtonClick -> {
				_state.update {
					it.copy(
						session = event.session
					)
				}
			}

			is SubjectEvent.OnTaskIsCompleteChange -> {
				updateTask(event.task)
			}
			SubjectEvent.UpdateProgress -> {
				val goalHours = state.value.goalStudyHours.toFloatOrNull() ?: 1f
				_state.update {
					it.copy(
						progress = (state.value.studiedHours / goalHours).coerceIn(0f , 1f)
					)
				}

			}

			else -> {}
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
				if(task.isComplete){
					_snackBarEventFlow.emit(
						SnackBarEvent.ShowSnackBar(
							message = "Saved in Upcoming Tasks"
						)
					)

				}else{
					_snackBarEventFlow.emit(
						SnackBarEvent.ShowSnackBar(
							message = "Saved in Completed Tasks"
						)
					)

				}

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

	private fun deleteSubject() {
		viewModelScope.launch {
			try {
				val currentSubjectId = state.value.currentSubjectId
				if (currentSubjectId != null) {
					withContext(Dispatchers.IO) {
						subjectRepository.deleteSubject(currentSubjectId)
					}
					_snackBarEventFlow.emit(
						SnackBarEvent.ShowSnackBar(
							message = "Subject Deleted Successfully"
						)
					)
					_snackBarEventFlow.emit(
						SnackBarEvent.NavigateUp
					)
				}else{
					_snackBarEventFlow.emit(
						SnackBarEvent.ShowSnackBar(
							message = "No Subjects to delete"
						)
					)
				}


			}catch (e : Exception){
				_snackBarEventFlow.emit(
					SnackBarEvent.ShowSnackBar(
						message = e.message ?: "Couldn't delete subject. ${e.localizedMessage}",
						duration = SnackbarDuration.Long
					)
				)
			}
		}
	}

	private fun updateSubject() {
		viewModelScope.launch {
			try {
				subjectRepository.upsertSubject(
					subject = Subject(
						subjectId = state.value.currentSubjectId ,
						name = state.value.subjectName ,
						goalHours = state.value.goalStudyHours.toFloatOrNull() ?: 1f ,
						colors = state.value.subjectCardColors.map { it.toArgb() }
					)
				)
				_snackBarEventFlow.emit(
					SnackBarEvent.ShowSnackBar(
						message = "Subject Updated Successfully"
					)
				)

			}catch (e : Exception){
				_snackBarEventFlow.emit(
					SnackBarEvent.ShowSnackBar(
						message = e.message ?: "Couldn't update subject. ${e.localizedMessage}",
						duration = SnackbarDuration.Long
					)
				)
			}
		}
	}

	private fun fetchSubject(){
		viewModelScope.launch {
			subjectRepository.getSubjectById(navArgs.subjectId)?.let { subject ->
				_state.update {
					it.copy(
						subjectName = subject.name ,
						goalStudyHours = subject.goalHours.toString() ,
						subjectCardColors = subject.colors.map { color -> Color(color) },
						currentSubjectId = subject.subjectId
						)
				}
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