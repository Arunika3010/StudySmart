package com.arunika.studysmart.presentation.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arunika.studysmart.R
import com.arunika.studysmart.domain.model.Task
import com.arunika.studysmart.util.Priority
import com.arunika.studysmart.util.changeMillisToDateString

fun LazyListScope.tasksList(
	sectionTitle: String ,
	emptyListText: String ,
	taskLists: List<Task>,
	onTaskCardClick: (Int?) -> Unit,
	onCheckBoxClick: (Task) -> Unit
){
	item {
		Text(
			text = sectionTitle,
			style = MaterialTheme.typography.bodyLarge,
			modifier = Modifier.padding(start = 12.dp, top = 20.dp)

		)
	}

	if (taskLists.isEmpty()){
		item {
			Column(
				modifier = Modifier.fillMaxWidth(),
				horizontalAlignment = Alignment.CenterHorizontally

			) {
				Image(
					modifier = Modifier
						.size(120.dp)
						.padding(top = 12.dp),
					painter = painterResource(id = R.drawable.check_list) ,
					contentDescription =emptyListText
				)
				Spacer(modifier = Modifier.height(12.dp))
				Text(
					modifier = Modifier.padding(top = 12.dp),
					text = emptyListText,
					style = MaterialTheme.typography.bodyMedium,
					color = Color.Gray,
					textAlign = TextAlign.Center,
				)

			}
		}

	}
	items(taskLists){
		task ->
		TaskCard(
			modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
			task = task,
			onClick = { onTaskCardClick(task.taskId) },
			onCheckBoxClick = { onCheckBoxClick(task) }
		)
	}


}

@Composable
private fun TaskCard(
	modifier: Modifier = Modifier,
	task: Task,
	onClick: () -> Unit = {},
	onCheckBoxClick: () -> Unit = {}
){
	ElevatedCard(modifier = modifier.clickable{ onClick() }) {
		Row (
			modifier = Modifier
				.fillMaxWidth()
				.padding(8.dp),
			verticalAlignment = Alignment.CenterVertically

		){
			TaskCheckBox(
				isCompleted = task.isComplete,
				borderColor = Priority.fromInt(task.priority).color ,
				onCheckBoxClick = { onCheckBoxClick() }
			)
			Spacer(modifier = Modifier.width(10.dp))
			Column {
				Text(
					text = task.title,
					style = MaterialTheme.typography.titleMedium,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
					textDecoration = if (task.isComplete){
						TextDecoration.LineThrough
					}else{
						TextDecoration.None
					}
				)
				Text(
					text = task.dueDate.changeMillisToDateString()  ,
					style = MaterialTheme.typography.bodyMedium
				)
			}

		}

	}
}

@Preview(showBackground = true)
@Composable
fun TaskCardPreview(){
	TaskCard(
		task = Task(
			0,1 ,"Math Test","Math Test",0L,1,"Math", false
		)
	)
}