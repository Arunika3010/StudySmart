package com.arunika.studysmart.presentation.subject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arunika.studysmart.presentation.components.AddSubjectDialog
import com.arunika.studysmart.presentation.components.CountCard
import com.arunika.studysmart.presentation.components.DeleteDialog
import com.arunika.studysmart.presentation.components.studySessionsList
import com.arunika.studysmart.presentation.components.tasksList
import com.arunika.studysmart.presentation.destinations.TaskScreenRouteDestination
import com.arunika.studysmart.presentation.task.TaskScreenNavArgs
import com.arunika.studysmart.util.SnackBarEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

data class SubjectScreenNavArgs(
	val subjectId : Int
)


@Destination(navArgsDelegate = SubjectScreenNavArgs::class)
@Composable
fun SubjectScreenRoute(
	navigator : DestinationsNavigator
) {
	val viewModel : SubjectViewModel = hiltViewModel()
	val state by viewModel.state.collectAsStateWithLifecycle()
	SubjectScreen(
		state = state ,
		onEvent = viewModel::onEvent ,
		snackBarEvent = viewModel.snackBarEventFlow ,
		onBackButtonClick = { navigator.navigateUp() } ,
		onAddTaskButtonClick = {
			val navArg = TaskScreenNavArgs(taskId = null , subjectId = state.currentSubjectId)
			navigator.navigate(TaskScreenRouteDestination(navArgs = navArg))
		} ,
		onTaskCardClick = { taskId ->
			val navArg = TaskScreenNavArgs(taskId = taskId , subjectId = null)
			navigator.navigate(TaskScreenRouteDestination(navArgs = navArg))
		}
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubjectScreen(
	state : SubjectState ,
	onEvent : (SubjectEvent) -> Unit ,
	snackBarEvent : SharedFlow<SnackBarEvent> ,
	onBackButtonClick : () -> Unit ,
	onAddTaskButtonClick : () -> Unit ,
	onTaskCardClick : (Int?) -> Unit ,
) {
	var isDeleteSessionDialogOpen by rememberSaveable { mutableStateOf(false) }
	val listState = rememberLazyListState()
	val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
	val isFABExpended by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }
	var isEditSubjectDialogOpen by rememberSaveable { mutableStateOf(false) }
	var isDeleteSubjectDialogOpen by rememberSaveable { mutableStateOf(false) }

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

				SnackBarEvent.NavigateUp -> {
					onBackButtonClick()
				}
			}
		}
	}

	LaunchedEffect(key1 = state.studiedHours, key2 = state.goalStudyHours) {
		onEvent(SubjectEvent.UpdateProgress)
	}

	AddSubjectDialog(
		isDialogOpen = isEditSubjectDialogOpen ,
		onDismiss = { isEditSubjectDialogOpen = false } ,
		onSave = {
			onEvent(SubjectEvent.UpdateSubject)
			isEditSubjectDialogOpen = false
		} ,
		selectedColors = state.subjectCardColors ,
		onColorSelected = { onEvent(SubjectEvent.OnSubjectCardColorChange(it)) } ,
		subjectName = state.subjectName ,
		goalStudyHours = state.goalStudyHours ,
		onSubjectNameChange = { onEvent(SubjectEvent.OnSubjectNameChange(it)) } ,
		onGoalStudyHoursChange = { onEvent(SubjectEvent.OnGoalStudyHoursChange(it)) }
	)
	DeleteDialog(
		isDialogOpen = isDeleteSessionDialogOpen ,
		title = "Delete Session" ,
		bodyText = "Are you sure you want to delete this session? Your studied hours will be reduced " +
				"by this session time. This action cannot be undone." ,
		onDismiss = { isDeleteSessionDialogOpen = false } ,
		onConfirm = {
			onEvent(SubjectEvent.DeleteSession)
			isDeleteSessionDialogOpen = false
		}
	)
	DeleteDialog(
		isDialogOpen = isDeleteSubjectDialogOpen ,
		title = "Delete Subject" ,
		bodyText = "Are you sure you want to delete this subject? " +
				"All related tasks and sessions will be permanently deleted. This action cannot be undone." ,
		onDismiss = { isDeleteSubjectDialogOpen = false } ,
		onConfirm = {
			onEvent(SubjectEvent.DeleteSubject)
			isDeleteSubjectDialogOpen = false
		}
	)
	Scaffold(
		snackbarHost = { SnackbarHost(hostState = snackBarHostState) } ,
		modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection) ,
		topBar = {
			SubjectScreenTopBar(
				title = state.subjectName ,
				scrollBehavior = scrollBehavior ,
				onNavigateBack = onBackButtonClick ,
				onDeleteClick = { isDeleteSubjectDialogOpen = true } ,
				onEditClick = { isEditSubjectDialogOpen = true }
			)
		} ,
		floatingActionButton = {
			ExtendedFloatingActionButton(
				onClick = onAddTaskButtonClick  ,
				icon = { Icon(imageVector = Icons.Default.Add , contentDescription = "Add") } ,
				text = { Text(text = "Add Task") } ,
				expanded = isFABExpended
			)
		}

	) { paddingValues ->
		LazyColumn(
			state = listState ,
			modifier = Modifier
				.fillMaxSize()
				.padding(paddingValues)
		) {
			item {
				SubjectOverviewSection(
					modifier = Modifier
						.fillMaxSize()
						.padding(12.dp) ,
					goalStudyHours = state.goalStudyHours ,
					studiedHours = state.studiedHours.toString() ,
					progress = state.progress
				)
			}
			tasksList(
				sectionTitle = "UPCOMING TASKS" ,
				emptyListText = "You don't have any upcoming tasks. \n " +
						"Click the + button to add new task" ,
				taskLists = state.upcomingTasks ,
				onTaskCardClick = onTaskCardClick ,
				onCheckBoxClick = { onEvent(SubjectEvent.OnTaskIsCompleteChange(it)) }
			)
			tasksList(
				sectionTitle = "COMPLETED TASKS" ,
				emptyListText = "You don't have any completed tasks. \n " +
						"Click the check box on completion of task" ,
				taskLists = state.completedTasks ,
				onTaskCardClick = onTaskCardClick ,
				onCheckBoxClick = { onEvent(SubjectEvent.OnTaskIsCompleteChange(it)) }
			)
			studySessionsList(
				sectionTitle = "RECENT STUDY SESSIONS" ,
				emptyListText = "You don't have any recent study sessions. \n" +
						"Start a study session to begin recording your progress" ,
				sessionLists = state.recentSessions ,
				onDeleteIconClick = {
					onEvent(SubjectEvent.OnDeleteSessionButtonClick(it))
					isDeleteSessionDialogOpen = true
				}
			)

		}

	}

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubjectScreenTopBar(
	title : String ,
	scrollBehavior : TopAppBarScrollBehavior ,
	onNavigateBack : () -> Unit ,
	onDeleteClick : () -> Unit ,
	onEditClick : () -> Unit
) {
	LargeTopAppBar(
		scrollBehavior = scrollBehavior ,
		navigationIcon = {
			IconButton(onClick = onNavigateBack) {
				Icon(
					imageVector = Icons.AutoMirrored.Filled.ArrowBack ,
					contentDescription = "Navigate Back"
				)
			}
		} ,
		title = {
			Text(
				text = title ,
				maxLines = 1 ,
				overflow = TextOverflow.Ellipsis ,
				style = MaterialTheme.typography.headlineSmall
			)
		} ,
		actions = {
			IconButton(onClick = onDeleteClick) {
				Icon(
					imageVector = Icons.Default.Delete ,
					contentDescription = "Delete subject"
				)
			}
			IconButton(onClick = onEditClick) {
				Icon(
					imageVector = Icons.Default.Edit ,
					contentDescription = "Edit subject"
				)
			}
		}
	)

}

@Composable
private fun SubjectOverviewSection(
	modifier : Modifier ,
	goalStudyHours : String ,
	studiedHours : String ,
	progress : Float
) {
	val percentageProgress = remember(progress) {
		(progress * 100).toInt().coerceIn(0 , 100)

	}
	Row(
		modifier = modifier ,
		horizontalArrangement = Arrangement.SpaceAround ,
		verticalAlignment = Alignment.CenterVertically ,
	) {
		CountCard(
			heading = "Goal Study Hours" ,
			count = goalStudyHours ,
			modifier = Modifier.weight(1f)
		)
		Spacer(
			modifier = Modifier.width(10.dp)
		)
		CountCard(
			heading = "Studied Hours" ,
			count = studiedHours ,
			modifier = Modifier.weight(1f)
		)
		Spacer(
			modifier = Modifier.width(10.dp)
		)
		Box(
			modifier = Modifier.size(75.dp) ,
			contentAlignment = Alignment.Center
		) {
			CircularProgressIndicator(
				progress = { 1f } ,
				modifier = Modifier.fillMaxSize() ,
				color = MaterialTheme.colorScheme.surfaceVariant ,
				strokeWidth = 4.dp ,
				strokeCap = StrokeCap.Round ,
			)
			CircularProgressIndicator(
				progress = { progress } ,
				modifier = Modifier.fillMaxSize() ,
				strokeWidth = 4.dp ,
				strokeCap = StrokeCap.Round ,
			)
			Text(
				text = "$percentageProgress%" ,
				style = MaterialTheme.typography.bodyLarge
			)
		}


	}

}

