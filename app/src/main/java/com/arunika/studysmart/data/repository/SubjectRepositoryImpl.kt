package com.arunika.studysmart.data.repository

import com.arunika.studysmart.data.local.SessionDao
import com.arunika.studysmart.data.local.SubjectDao
import com.arunika.studysmart.data.local.TaskDao
import com.arunika.studysmart.domain.model.Subject
import com.arunika.studysmart.domain.repository.SubjectRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SubjectRepositoryImpl @Inject constructor(
	private val subjectDao : SubjectDao,
	private val taskDao : TaskDao,
	private val sessionDao : SessionDao
): SubjectRepository {
	override suspend fun upsertSubject(subject : Subject) {
		subjectDao.upsertSubject(subject)
	}

	override fun getTotalSubjectCount() : Flow<Int> {
		return subjectDao.getTotalSubjectCount()
	}

	override fun getTotalGoalHours() : Flow<Float> {
		return subjectDao.getTotalGoalHours()
	}

	override suspend fun getSubjectById(subjectId : Int) : Subject? {
		return subjectDao.getSubjectById(subjectId)
	}

	override suspend fun deleteSubject(subjectId : Int) {
		subjectDao.deleteSubject(subjectId)
		taskDao.deleteTasksBySubjectId(subjectId)
		sessionDao.deleteSessionsBySubjectId(subjectId)

	}

	override fun getAllSubjects() : Flow<List<Subject>> {
		return subjectDao.getAllSubjects()
	}
}