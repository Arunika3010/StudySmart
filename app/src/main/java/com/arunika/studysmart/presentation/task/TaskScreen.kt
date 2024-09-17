package com.arunika.studysmart.presentation.task

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arunika.studysmart.presentation.components.DeleteDialog
import com.arunika.studysmart.presentation.components.SubjectListBottomSheet
import com.arunika.studysmart.presentation.components.TaskCheckBox
import com.arunika.studysmart.presentation.components.TaskDatePicker
import com.arunika.studysmart.util.CurrentOrFutureSelectableDates
import com.arunika.studysmart.util.Priority
import com.arunika.studysmart.util.SnackBarEvent
import com.arunika.studysmart.util.changeMillisToDateString
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Instant

data class TaskScreenNavArgs(
	val taskId : Int? ,
	val subjectId : Int?
)

@Destination(navArgsDelegate = TaskScreenNavArgs::class)
@Composable
fun TaskScreenRoute(
	navigator : DestinationsNavigator
) {
	val viewModel : TaskViewModel = hiltViewModel()
	val state by viewModel.state.collectAsStateWithLifecycle()
	TaskScreen(
		state = state ,
		onEvent = viewModel::onEvent ,
		snackBarEvent = viewModel.snackBarEventFlow ,
		onBackButtonClick = {
			navigator.navigateUp()
		}
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskScreen(
	state : TaskState ,
	onEvent : (TaskEvent) -> Unit ,
	snackBarEvent : SharedFlow<SnackBarEvent> ,
	onBackButtonClick : () -> Unit
) {

	var isDeleteDialogOpen by rememberSaveable { mutableStateOf(false) }
	var isDatePickerOpen by rememberSaveable { mutableStateOf(false) }
	val datePickerState = rememberDatePickerState(
		initialSelectedDateMillis = Instant.now().toEpochMilli() ,
		selectableDates = CurrentOrFutureSelectableDates
	)
	var taskTitleError by rememberSaveable { mutableStateOf<String?>("") }
	taskTitleError = when {
		state.title.isBlank() -> "Please enter task title"
		state.title.length < 4 -> "Task title is too short"
		state.title.length > 30 -> "Task title is too long"
		else -> null
	}
	val scope = rememberCoroutineScope()
	val sheetState = rememberModalBottomSheetState()
	var isBottomSheetOpen by remember { mutableStateOf(false) }
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
	DeleteDialog(
		isDialogOpen = isDeleteDialogOpen ,
		title = "Delete Task?" ,
		bodyText = "Are you sure you want to delete this task? " +
				"This action cannot be undone." ,
		onDismiss = { isDeleteDialogOpen = false } ,
		onConfirm = {
			onEvent(TaskEvent.DeleteTask)
			isDeleteDialogOpen = false
		}
	)
	TaskDatePicker(
		state = datePickerState ,
		isDialogOpen = isDatePickerOpen ,
		onDismissRequest = { isDatePickerOpen = false } ,
		onConfirm = {
			onEvent(TaskEvent.OnDueDateChange(dueDate = datePickerState.selectedDateMillis))
			isDatePickerOpen = false
		}
	)
	SubjectListBottomSheet(
		sheetState = sheetState ,
		isOpen = isBottomSheetOpen ,
		subjectList = state.subjects ,
		onSubjectClick = { subject ->
			onEvent(TaskEvent.OnRelatedSubjectSelect(subject))
			isBottomSheetOpen = false
		} ,
		onDismissRequest = {
			scope.launch {
				sheetState.hide()
			}.invokeOnCompletion {
				if (! sheetState.isVisible) {
					isBottomSheetOpen = false
				}
			}
		}
	)
	Scaffold(
		snackbarHost = { SnackbarHost(hostState = snackBarHostState) } ,
		topBar = {
			TaskScreenTopBar(
				isTaskExist = state.currentTaskId != null ,
				isComplete = state.isTaskComplete ,
				checkBoxBorderColor = state.priority.color ,
				onBackClick = onBackButtonClick ,
				onDeleteClick = { isDeleteDialogOpen = true } ,
				onCheckBoxClick = { onEvent(TaskEvent.OnIsCompleteChange) }
			)
		}
	) { paddingValues ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.verticalScroll(state = rememberScrollState())
				.padding(paddingValues)
				.padding(horizontal = 12.dp)
		) {
			OutlinedTextField(
				modifier = Modifier.fillMaxWidth() ,
				value = state.title ,
				onValueChange = { onEvent(TaskEvent.OnTitleChange(it)) } ,
				label = { Text(text = "Title") } ,
				singleLine = true ,
				isError = taskTitleError != null && state.title.isNotBlank() ,
				supportingText = { Text(text = taskTitleError.orEmpty()) }
			)
			Spacer(modifier = Modifier.height(10.dp))
			OutlinedTextField(
				modifier = Modifier.fillMaxWidth() ,
				value = state.description ,
				onValueChange = { onEvent(TaskEvent.OnDescriptionChange(it)) } ,
				label = { Text(text = "Description") }
			)
			Spacer(modifier = Modifier.height(20.dp))
			Text(
				text = "Due Date" ,
				style = MaterialTheme.typography.bodySmall
			)
			Row(
				modifier = Modifier.fillMaxWidth() ,
				horizontalArrangement = Arrangement.SpaceBetween ,
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = state.dueDate.changeMillisToDateString() ,
					style = MaterialTheme.typography.bodyLarge
				)
				IconButton(onClick = {
					isDatePickerOpen = true
				}) {
					Icon(
						imageVector = Icons.Default.DateRange ,
						contentDescription = "Select Due Date"
					)
				}

			}
			Spacer(modifier = Modifier.height(10.dp))
			Text(
				text = "Priority" ,
				style = MaterialTheme.typography.bodySmall
			)
			Spacer(modifier = Modifier.height(10.dp))
			Row(
				modifier = Modifier.fillMaxWidth()
			) {
				Priority.entries.forEach { priority ->
					PriorityButton(
						modifier = Modifier.weight(1f) ,
						label = priority.title ,
						backgroundColor = priority.color ,
						borderColor = if (priority == state.priority) Color.White else Color.Transparent ,
						labelColor = if (priority == state.priority) Color.White else Color.White.copy(
							alpha = 0.7f
						) ,
						onClick = { onEvent(TaskEvent.OnPriorityChange(priority)) }
					)

				}

			}
			Spacer(modifier = Modifier.height(30.dp))
			Text(
				text = "Related to Subject" ,
				style = MaterialTheme.typography.bodySmall
			)
			Row(
				modifier = Modifier.fillMaxWidth() ,
				horizontalArrangement = Arrangement.SpaceBetween ,
				verticalAlignment = Alignment.CenterVertically
			) {
				val firstSubject = state.subjects.firstOrNull()?.name ?: ""
				Text(
					text = state.relatedSubject ?: firstSubject ,
					style = MaterialTheme.typography.bodyLarge
				)
				IconButton(onClick = { isBottomSheetOpen = true }) {
					Icon(
						imageVector = Icons.Default.ArrowDropDown ,
						contentDescription = "Select Subject"
					)
				}

			}
			Button(
				onClick = { onEvent(TaskEvent.SaveTask) } ,
				enabled = taskTitleError == null ,
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 20.dp)
			)
			{
				Text(text = "Save Task")

			}

		}


	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreenTopBar(
	isTaskExist : Boolean ,
	isComplete : Boolean ,
	checkBoxBorderColor : Color ,
	onBackClick : () -> Unit ,
	onDeleteClick : () -> Unit ,
	onCheckBoxClick : () -> Unit ,
) {
	TopAppBar(
		navigationIcon = {
			IconButton(onClick = onBackClick) {
				Icon(
					imageVector = Icons.AutoMirrored.Filled.ArrowBack ,
					contentDescription = "Navigate Back"
				)
			}
		} ,
		title = {
			Text(
				text = "Task" ,
				style = MaterialTheme.typography.headlineSmall
			)
		} ,
		actions = {
			if (isTaskExist) {
				TaskCheckBox(
					isCompleted = isComplete ,
					borderColor = checkBoxBorderColor ,
					onCheckBoxClick = onCheckBoxClick
				)
				IconButton(onClick = onDeleteClick) {
					Icon(
						imageVector = Icons.Default.Delete ,
						contentDescription = "Delete Task"
					)
				}
			}
		}
	)

}

@Composable
private fun PriorityButton(
	modifier : Modifier = Modifier ,
	label : String ,
	backgroundColor : Color ,
	borderColor : Color ,
	labelColor : Color ,
	onClick : () -> Unit
) {
	Box(
		modifier = modifier
			.background(color = backgroundColor)
			.padding(5.dp)
			.clickable { onClick() }
			.border(1.dp , borderColor , RoundedCornerShape(5.dp))
			.padding(5.dp) ,
		contentAlignment = Alignment.Center
	) {
		Text(
			text = label ,
			color = labelColor ,
			style = MaterialTheme.typography.bodyLarge
		)
	}

}



