package com.arunika.studysmart.presentation.session

import com.arunika.studysmart.domain.model.Session
import com.arunika.studysmart.domain.model.Subject

data class SessionState(
	val subjects: List<Subject> = emptyList() ,
	val sessions: List<Session> = emptyList() ,
	val relatedToSubject: String? = "",
	val subjectId: Int? = null,
	val session: Session? = null
)
