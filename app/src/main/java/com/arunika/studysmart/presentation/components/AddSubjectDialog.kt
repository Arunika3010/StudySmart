package com.arunika.studysmart.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arunika.studysmart.domain.model.Subject

@Composable
fun AddSubjectDialog(
	isDialogOpen: Boolean,
	title: String = "Add/Update Subject",
	selectedColors: List<Color>,
	onColorSelected: (List<Color>) -> Unit,
	subjectName: String,
	goalStudyHours: String,
	onSubjectNameChange: (String) -> Unit,
	onGoalStudyHoursChange: (String) -> Unit,
	onDismiss: () -> Unit,
	onSave: () -> Unit
){
	var subjectNameError by rememberSaveable { mutableStateOf <String?>(null) }
	var goalStudyHoursError by rememberSaveable { mutableStateOf <String?>(null) }

	subjectNameError = when {
		subjectName.isBlank() -> "Please enter subject name"
		subjectName.length < 2 -> "Subject name is too short"
		subjectName.length > 20 -> "Subject name is too long"
		else -> null
	}
	goalStudyHoursError = when {
		goalStudyHours.isBlank() -> "Please enter goal study hours"
		goalStudyHours.toFloatOrNull() == null -> "Invalid number"
		goalStudyHours.toFloat() < 1f -> "Please set a minimum of 1 hour"
		goalStudyHours.toFloat() > 1000f -> "Please set a maximum of 1000 hours"
		else -> null
	}

	if (isDialogOpen) {
		AlertDialog(
			onDismissRequest = { onDismiss() },
			title = { Text(text = title) },
			text = {
				Column {
					Row (
						modifier = Modifier
							.padding(bottom = 16.dp)
							.fillMaxWidth(),
						horizontalArrangement = Arrangement.SpaceAround
					) {
						Subject.subjectCardColors.forEach {
								color -> Box(modifier = Modifier
							.size(24.dp)
							.clip(CircleShape)
							.clickable { onColorSelected(color) }
							.border(
								width = 2.dp ,
								color = if (color == selectedColors) Color.Black else Color.Transparent ,
								shape = CircleShape
							)
							.background(Brush.verticalGradient(colors = color)))
						}

					}
					OutlinedTextField(
						value = subjectName ,
						onValueChange = onSubjectNameChange,
						label = { Text(text = "Subject Name") },
						keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text) ,
						singleLine = true,
						modifier = Modifier.fillMaxWidth(),
						isError = subjectNameError != null && subjectName.isNotBlank(),
						supportingText = { Text(text = subjectNameError.orEmpty()) }


					)
					Spacer(modifier = Modifier.height(10.dp))
					OutlinedTextField(
						value = goalStudyHours ,
						onValueChange = onGoalStudyHoursChange,
						label = { Text(text = "Goal Study Hours") },
						keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) ,
						singleLine = true,
						modifier = Modifier.fillMaxWidth(),
						isError = goalStudyHoursError != null && goalStudyHours.isNotBlank(),
						supportingText = { Text(text = goalStudyHoursError.orEmpty()) }
					)
				}

			},
			dismissButton = {
				TextButton(onClick = { onDismiss() }) {
					Text(text = "Cancel")
				}
			},
			confirmButton = {
				TextButton(
					enabled = subjectNameError == null && goalStudyHoursError == null,
					onClick = onSave
				) {
					Text(text = "Save")
				}
			}

		)

	}

}
@Preview(showBackground = true)
@Composable
fun AddSubjectDialogPreview(){
	AddSubjectDialog(isDialogOpen = true, onDismiss = {}, onSave = {}, selectedColors = Subject.subjectCardColors[0], onColorSelected = {}, subjectName = "", goalStudyHours = "", onSubjectNameChange = {}, onGoalStudyHoursChange = {})
}