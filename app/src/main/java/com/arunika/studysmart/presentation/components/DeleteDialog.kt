package com.arunika.studysmart.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun DeleteDialog(
	isDialogOpen: Boolean ,
	title: String ,
	bodyText: String ,
	onDismiss: () -> Unit ,
	onConfirm: () -> Unit
){
	if (isDialogOpen) {
		AlertDialog(
			onDismissRequest = { onDismiss() },
			title = { Text(text = title) },
			text = {
				Text(text = bodyText)
			},
			dismissButton = {
				TextButton(onClick = { onDismiss() }) {
					Text(text = "Cancel")
				}
			},
			confirmButton = {
				TextButton(onClick = onConfirm) {
					Text(text = "Delete")
				}
			}

		)

	}

}
@Preview(showBackground = true)
@Composable
fun DeleteDialogPreview(){
	DeleteDialog(
		isDialogOpen = true,
		title = "Delete Subject",
		bodyText = "Are you sure you want to delete this subject?",
		onDismiss = {},
		onConfirm = {}
	)
}