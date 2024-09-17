package com.arunika.studysmart.presentation.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arunika.studysmart.R
import com.arunika.studysmart.domain.model.Session
import com.arunika.studysmart.domain.model.Subject
import com.arunika.studysmart.domain.model.Task
import com.arunika.studysmart.presentation.components.AddSubjectDialog
import com.arunika.studysmart.presentation.components.CountCard
import com.arunika.studysmart.presentation.components.DeleteDialog
import com.arunika.studysmart.presentation.components.SubjectCard
import com.arunika.studysmart.presentation.components.studySessionsList
import com.arunika.studysmart.presentation.components.tasksList
import com.arunika.studysmart.presentation.destinations.SessionScreenRouteDestination
import com.arunika.studysmart.presentation.destinations.SubjectScreenRouteDestination
import com.arunika.studysmart.presentation.destinations.TaskScreenRouteDestination
import com.arunika.studysmart.presentation.subject.SubjectScreenNavArgs
import com.arunika.studysmart.presentation.task.TaskScreenNavArgs
import com.arunika.studysmart.util.SnackBarEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@RootNavGraph(start = true)
@Destination
@Composable
fun DashboardScreenRoute(
	navigator : DestinationsNavigator
) {

	val viewModel : DashboardViewModel = hiltViewModel()
	val state by viewModel.state.collectAsStateWithLifecycle()
	val tasks by viewModel.tasks.collectAsStateWithLifecycle()
	val recentSessionsList by viewModel.sessions.collectAsStateWithLifecycle()
	DashboardScreen(
		state = state ,
		tasks = tasks ,
		recentSessions = recentSessionsList ,
		onEvent = viewModel::onEvent ,
		snackBarEvent = viewModel.snackBarEventFlow ,
		onSubjectCardClick = { subjectId ->
			subjectId?.let {
				val navArg = SubjectScreenNavArgs(subjectId = it)
				navigator.navigate(SubjectScreenRouteDestination(navArgs = navArg))

			}
		} ,
		onTaskCardClick = { taskId ->
			val navArg = TaskScreenNavArgs(taskId = taskId , subjectId = null)
			navigator.navigate(TaskScreenRouteDestination(navArgs = navArg))
		} ,
		onStartStudySessionClick = {
			navigator.navigate(SessionScreenRouteDestination())
		})
}

@Composable
private fun DashboardScreen(
	state : DashboardState ,
	tasks : List<Task> ,
	recentSessions : List<Session> ,
	onEvent : (DashboardEvent) -> Unit ,
	snackBarEvent : SharedFlow<SnackBarEvent>,
	onSubjectCardClick : (Int?) -> Unit ,
	onTaskCardClick : (Int?) -> Unit ,
	onStartStudySessionClick : () -> Unit
) {

	var isAddSubjectDialogOpen by rememberSaveable { mutableStateOf(false) }
	var isDeleteSessionDialogOpen by rememberSaveable { mutableStateOf(false) }

	val snackBarHostState = remember { SnackbarHostState() }

	LaunchedEffect(key1 = true) {
		snackBarEvent.collectLatest { event ->
			when (event) {
				is SnackBarEvent.ShowSnackBar -> {
					snackBarHostState.showSnackbar(
						message = event.message ,
						duration = event.duration
					)
				}

				SnackBarEvent.NavigateUp -> {}
			}
		}
	}


	AddSubjectDialog(
		isDialogOpen = isAddSubjectDialogOpen ,
		onDismiss = { isAddSubjectDialogOpen = false } ,
		onSave = {
			onEvent(DashboardEvent.SaveSubject)
			isAddSubjectDialogOpen = false
		} ,
		selectedColors = state.subjectCardColors ,
		onColorSelected = { onEvent(DashboardEvent.OnSubjectCardColorChange(it)) } ,
		subjectName = state.subjectName ,
		goalStudyHours = state.goalStudyHours ,
		onSubjectNameChange = { onEvent(DashboardEvent.OnSubjectNameChange(it)) } ,
		onGoalStudyHoursChange = { onEvent(DashboardEvent.OnGoalStudyHoursChange(it)) }
	)
	DeleteDialog(
		isDialogOpen = isDeleteSessionDialogOpen ,
		title = "Delete Session" ,
		bodyText = "Are you sure you want to delete this session? Your studied hours will be reduced " +
				"by this session time. This action cannot be undone." ,
		onDismiss = { isDeleteSessionDialogOpen = false } ,
		onConfirm = {
			onEvent(DashboardEvent.DeleteSession)
			isDeleteSessionDialogOpen = false
		}
	)

	Scaffold(
		snackbarHost = { SnackbarHost(hostState = snackBarHostState) } ,
		topBar = { DashboardTopAppBar() }
	)
	{ paddingValues ->
		LazyColumn(
			modifier = Modifier
				.fillMaxSize()
				.padding(paddingValues)
		) {
			item {
				CountCardSection(
					modifier = Modifier
						.fillMaxWidth()
						.padding(12.dp) ,
					subjectCount = state.totalSubjectCount.toString() ,
					studiedHours = state.totalStudiedHours.toString() ,
					goalStudyHours = state.totalGoalHours.toString()
				)
			}
			item {
				SubjectCardSection(
					modifier = Modifier
						.fillMaxWidth()
						.padding(12.dp) ,
					subjectList = state.subjects ,
					onAddNewSubjectClick = { isAddSubjectDialogOpen = true } ,
					subjectCardClick = onSubjectCardClick
				)
			}
			item {
				Button(
					onClick = onStartStudySessionClick ,
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = 48.dp , vertical = 12.dp)
				)
				{
					Text(text = "Start Study Session")
				}
			}
			tasksList(
				sectionTitle = "UPCOMING TASKS" ,
				emptyListText = "You don't have any upcoming tasks. \n " +
						"Click the + button in subject screen to add new task" ,
				taskLists = tasks ,
				onTaskCardClick = onTaskCardClick ,
				onCheckBoxClick = { onEvent(DashboardEvent.OnTaskIsCompleteChange(it)) }
			)
			studySessionsList(
				sectionTitle = "RECENT STUDY SESSIONS" ,
				emptyListText = "You don't have any recent study sessions. \n" +
						"Start a study session to begin recording your progress" ,
				sessionLists = recentSessions ,
				onDeleteIconClick = {
					onEvent(DashboardEvent.OnDeleteSessionButtonClick(it))
					isDeleteSessionDialogOpen = true
				}
			)
		}

	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardTopAppBar() {
	CenterAlignedTopAppBar(
		title = {
			Text(
				text = "Study Smart" ,
				style = MaterialTheme.typography.headlineMedium
			)
		})
}

@Composable
private fun CountCardSection(
	modifier : Modifier = Modifier ,
	subjectCount : String ,
	studiedHours : String ,
	goalStudyHours : String
) {
	Row(modifier = modifier) {
		CountCard(
			modifier = Modifier.weight(1f) ,
			heading = "Subject Count" ,
			count = subjectCount
		)
		Spacer(modifier = Modifier.width(10.dp))
		CountCard(
			modifier = Modifier.weight(1f) ,
			heading = "Studied Hours" ,
			count = studiedHours
		)
		Spacer(modifier = Modifier.width(10.dp))
		CountCard(
			modifier = Modifier.weight(1f) ,
			heading = "Goal Study Hours" ,
			count = goalStudyHours
		)
		Spacer(modifier = Modifier.width(10.dp))

	}

}

@Composable
private fun SubjectCardSection(
	modifier : Modifier = Modifier ,
	subjectList : List<Subject> ,
	emptyListText : String = "You don't have any subjects .\n Click the + button to add new subject" ,
	onAddNewSubjectClick : () -> Unit ,
	subjectCardClick : (Int?) -> Unit
) {
	Column(modifier = modifier) {
		Row(
			modifier = Modifier.fillMaxWidth() ,
			verticalAlignment = Alignment.CenterVertically ,
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			Text(
				text = "Subjects" ,
				style = MaterialTheme.typography.bodyLarge ,
				//modifier = Modifier.padding(start = 12.dp)
			)
			IconButton(onClick = onAddNewSubjectClick) {
				Icon(
					imageVector = Icons.Default.Add ,
					contentDescription = "Add Subject"
				)

			}

		}
		if (subjectList.isEmpty()) {
			Image(
				modifier = Modifier
					.size(120.dp)
					.align(Alignment.CenterHorizontally)
					.padding(top = 12.dp) ,
				painter = painterResource(id = R.drawable.book_stack) ,
				contentDescription = emptyListText
			)
			Text(
				modifier = Modifier
					.fillMaxWidth()
					.padding(top = 12.dp) ,
				text = emptyListText ,
				style = MaterialTheme.typography.bodyMedium ,
				color = Color.Gray ,
				textAlign = TextAlign.Center ,
			)
		}
		LazyRow(
			horizontalArrangement = Arrangement.spacedBy(12.dp) ,
			contentPadding = PaddingValues(start = 12.dp , end = 12.dp)
		) {
			items(subjectList) { subject ->
				SubjectCard(
					gradientColors = subject.colors.map { Color(it) } ,
					subjectName = subject.name ,
					onClick = { subjectCardClick(subject.subjectId) }
				)

			}
		}
	}

}



