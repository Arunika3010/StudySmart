package com.arunika.studysmart.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arunika.studysmart.R
import com.arunika.studysmart.domain.model.Session
import com.arunika.studysmart.util.changeMillisToDateString
import com.arunika.studysmart.util.toHours

fun LazyListScope.studySessionsList(
	sectionTitle: String ,
	emptyListText: String ,
	sessionLists: List<Session> ,
	onDeleteIconClick :(Session) -> Unit

	){
	item {
		Text(
			text = sectionTitle,
			style = MaterialTheme.typography.bodyLarge,
			modifier = Modifier.padding(start = 12.dp, top = 20.dp)

		)
	}

	if (sessionLists.isEmpty()){
		item {
			Column(
				modifier = Modifier.fillMaxWidth(),
				horizontalAlignment = Alignment.CenterHorizontally

			) {
				Image(
					modifier = Modifier
						.size(120.dp)
						.padding(top = 12.dp),
					painter = painterResource(id = R.drawable.desk_lamp) ,
					contentDescription =emptyListText
				)
				Spacer(modifier = Modifier.height(12.dp))
				Text(
					modifier = Modifier.padding(top = 12.dp, bottom = 12.dp),
					text = emptyListText,
					style = MaterialTheme.typography.bodyMedium,
					color = Color.Gray,
					textAlign = TextAlign.Center,
				)

			}
		}

	}
	items(sessionLists){
			session ->
		StudySessionCard(
			modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
			session = session,
			onDeleteIconClick = { onDeleteIconClick(session) }
		)
	}
	item {
		Spacer(modifier = Modifier.height(12.dp))
	}


}

@Composable
private fun StudySessionCard(
	modifier: Modifier = Modifier ,
	session : Session ,
	onDeleteIconClick: () -> Unit = {}
){
	Card(modifier = modifier.padding(top = 5.dp, bottom = 5.dp)) {
		Row (
			modifier = Modifier.fillMaxWidth(),
			verticalAlignment = Alignment.CenterVertically

		){
			Column (modifier = Modifier.padding(start = 15.dp)){
				Text(
					text = session.relatedToSubject,
					style = MaterialTheme.typography.titleMedium,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
				)
				Text(
					text = session.date.changeMillisToDateString() ,
					style = MaterialTheme.typography.bodyMedium
				)
			}
			Spacer(modifier = Modifier.weight(1f))
			Text(
				text =" ${session.duration.toHours()} hr" ,
				style = MaterialTheme.typography.bodyMedium
			)
			IconButton(onClick = onDeleteIconClick ){
				Icon(
					imageVector = Icons.Default.Delete ,

					contentDescription = "Delete")
			}


		}

	}
}

