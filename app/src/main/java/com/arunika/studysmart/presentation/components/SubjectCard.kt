package com.arunika.studysmart.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.arunika.studysmart.presentation.theme.gradient1
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush.Companion.verticalGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arunika.studysmart.R

@Composable
fun SubjectCard(
	modifier: Modifier = Modifier ,
	gradientColors: List<Color> = gradient1 ,
	subjectName: String ,
	onClick: () -> Unit
) {
	Box (modifier = modifier
		.size(150.dp)
		.clickable { onClick() }
		.background(brush = verticalGradient(gradientColors), shape = MaterialTheme.shapes.medium)
	){
		Column(modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.Center) {
			Image(
				modifier = Modifier.size(70.dp),
				painter = painterResource(id = R.drawable.book_stack) ,
				contentDescription = "Subject Image"
			)
			Text(
				text = subjectName,
				modifier = Modifier.padding(top = 18.dp),
				overflow = TextOverflow.Ellipsis,
				maxLines = 1,
				style = MaterialTheme.typography.headlineSmall,
				color = Color.White
			)
		}
	}
}
@Preview(showBackground = true)
@Composable
fun SubjectCardPreview(){
	SubjectCard(
		subjectName = "Math",
		onClick = {}
	)
}