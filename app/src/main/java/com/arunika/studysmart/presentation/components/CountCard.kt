package com.arunika.studysmart.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CountCard (
	modifier: Modifier = Modifier,
	heading: String ,
	count: String

){
	ElevatedCard(modifier = modifier){
		Column (
			modifier = Modifier
				.background(MaterialTheme.colorScheme.primaryContainer)
				.fillMaxWidth()
				.padding(vertical = 4.dp, horizontal = 12.dp),
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally

		){
			Text(
				text = heading,
				style = MaterialTheme.typography.labelSmall
			)
			Spacer(modifier = Modifier.height(5.dp))
			Text(
				text = count,
				style = MaterialTheme.typography.bodySmall.copy(fontSize = 30.sp)
			)
			Spacer(modifier = Modifier.height(5.dp))

		}
	}

}
@Preview(showBackground = true)
@Composable
fun CountCardPreview(){
	CountCard(modifier = Modifier, "Completed", "10")
}


