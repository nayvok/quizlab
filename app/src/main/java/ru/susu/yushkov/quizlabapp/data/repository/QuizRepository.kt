package ru.susu.yushkov.quizlabapp.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import ru.susu.yushkov.quizlabapp.data.dao.QuestionDao
import ru.susu.yushkov.quizlabapp.data.dao.QuizDao
import ru.susu.yushkov.quizlabapp.data.entities.QuestionEntity
import ru.susu.yushkov.quizlabapp.data.entities.QuizEntity
import ru.susu.yushkov.quizlabapp.data.models.Question
import ru.susu.yushkov.quizlabapp.data.models.Quiz

class QuizRepository(
    private val quizDao: QuizDao,
    private val questionDao: QuestionDao
) {
    // Get all quizzes as Flow
    fun getAllQuizzes(): Flow<List<Quiz>> {
        return quizDao.getAllQuizzes().combine(
            questionDao.getQuestionsByQuizId(0) // Dummy to trigger initial load
        ) { quizzes, _ ->
            quizzes.map { quizEntity ->
                Quiz(
                    id = quizEntity.id,
                    title = quizEntity.title,
                    createdAt = quizEntity.createdAt,
                    questions = emptyList()
                )
            }
        }
    }

    // Get quiz with questions
    suspend fun getQuizWithQuestions(quizId: Long): Quiz? {
        val quizEntity = quizDao.getQuizById(quizId) ?: return null
        val questionEntities = questionDao.getQuestionsByQuizId(quizId)
        
        // Convert to domain model (simplified - normally you'd collect Flow)
        return Quiz(
            id = quizEntity.id,
            title = quizEntity.title,
            createdAt = quizEntity.createdAt,
            questions = emptyList() // Will be populated from Flow in ViewModel
        )
    }

    // Get questions for a quiz
    fun getQuestionsByQuizId(quizId: Long): Flow<List<Question>> {
        return questionDao.getQuestionsByQuizId(quizId).map { entities ->
            entities.map { entity ->
                Question(
                    id = entity.id,
                    quizId = entity.quizId,
                    text = entity.text,
                    correctAnswer = entity.correctAnswer,
                    wrongAnswers = entity.wrongAnswers.split(",").map { it.trim() }
                )
            }
        }
    }

    // Get question count for a quiz
    fun getQuestionCountByQuizId(quizId: Long): Flow<Int> {
        return questionDao.getQuestionCountByQuizId(quizId)
    }

    // Insert quiz
    suspend fun insertQuiz(quiz: Quiz): Long {
        val quizId = quizDao.insertQuiz(
            QuizEntity(
                id = quiz.id,
                title = quiz.title,
                createdAt = quiz.createdAt
            )
        )

        // Insert questions if any
        if (quiz.questions.isNotEmpty()) {
            val questionEntities = quiz.questions.map { question ->
                QuestionEntity(
                    id = question.id,
                    quizId = quizId,
                    text = question.text,
                    correctAnswer = question.correctAnswer,
                    wrongAnswers = question.wrongAnswers.joinToString(", ")
                )
            }
            questionDao.insertQuestions(questionEntities)
        }

        return quizId
    }

    // Insert question
    suspend fun insertQuestion(question: Question): Long {
        return questionDao.insertQuestion(
            QuestionEntity(
                id = question.id,
                quizId = question.quizId,
                text = question.text,
                correctAnswer = question.correctAnswer,
                wrongAnswers = question.wrongAnswers.joinToString(", ")
            )
        )
    }

    // Update quiz
    suspend fun updateQuiz(quiz: Quiz) {
        quizDao.updateQuiz(
            QuizEntity(
                id = quiz.id,
                title = quiz.title,
                createdAt = quiz.createdAt
            )
        )
    }

    // Delete quiz
    suspend fun deleteQuiz(quizId: Long) {
        quizDao.deleteQuizById(quizId)
        // Questions will be deleted automatically due to CASCADE
    }

    // Delete question
    suspend fun deleteQuestion(questionId: Long) {
        val question = questionDao.getQuestionById(questionId)
        question?.let { questionDao.deleteQuestion(it) }
    }
}
