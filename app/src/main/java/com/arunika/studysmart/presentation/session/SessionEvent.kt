package com.arunika.studysmart.presentation.session

import com.arunika.studysmart.domain.model.Session
import com.arunika.studysmart.domain.model.Subject

sealed class SessionEvent {

	data class OnRelatedToSubjectChange(val subject: Subject) : SessionEvent()

	data class SaveSession(val duration: Long) : SessionEvent()

	data class OnDeleteSessionButtonClick(val session: Session) : SessionEvent()

	data object DeleteSession : SessionEvent()

	data object NotifyToUpdateSubject : SessionEvent()

	data class UpdateSubjectIdAndRelatedSubject(
		val subjectId: Int?,
		val relatedToSubject: String?
	) : SessionEvent()

}