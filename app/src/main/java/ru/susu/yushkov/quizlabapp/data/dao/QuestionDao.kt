package ru.susu.yushkov.quizlabapp.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.susu.yushkov.quizlabapp.data.entities.QuestionEntity

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions WHERE quizId = :quizId")
    fun getQuestionsByQuizId(quizId: Long): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE quizId = :quizId")
    suspend fun getQuestionsByQuizIdSync(quizId: Long): List<QuestionEntity>

    @Query("SELECT COUNT(*) FROM questions WHERE quizId = :quizId")
    fun getQuestionCountByQuizId(quizId: Long): Flow<Int>

    @Query("SELECT * FROM questions WHERE id = :questionId")
    suspend fun getQuestionById(questionId: Long): QuestionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Update
    suspend fun updateQuestion(question: QuestionEntity)

    @Delete
    suspend fun deleteQuestion(question: QuestionEntity)

    @Query("DELETE FROM questions WHERE quizId = :quizId")
    suspend fun deleteQuestionsByQuizId(quizId: Long)
}
