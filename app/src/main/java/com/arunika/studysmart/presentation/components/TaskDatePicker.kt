package com.arunika.studysmart.presentation.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDatePicker(
	state : DatePickerState ,
	isDialogOpen : Boolean,
	confirmText : String = "Ok",
	dismissText : String = "Cancel",
	onDismissRequest : () -> Unit,
	onConfirm : () -> Unit
){
	if (isDialogOpen){
		DatePickerDialog(
			onDismissRequest = onDismissRequest ,
			confirmButton = {
				TextButton(onClick = onConfirm) {
					Text(text = confirmText)

				}
			},
			dismissButton = {
				TextButton(onClick = onDismissRequest) {
					Text(text = dismissText)
				}
			},
			content = {
				DatePicker(
					state = state,
					title = { Text(text = "Select Date") },
				)
			}
		)

	}


}
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun TaskDatePickerPreview(){
	TaskDatePicker(state = rememberDatePickerState() , isDialogOpen = true , onDismissRequest = { /*TODO*/ } , onConfirm = { /*TODO*/ })
}