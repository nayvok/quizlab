package ru.susu.yushkov.quizlabapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.susu.yushkov.quizlabapp.data.database.AppDatabase
import ru.susu.yushkov.quizlabapp.data.models.Question
import ru.susu.yushkov.quizlabapp.data.models.Quiz
import ru.susu.yushkov.quizlabapp.data.repository.QuizRepository

class QuizViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: QuizRepository

    private val _quizzes = MutableStateFlow<List<Quiz>>(emptyList())
    val quizzes: StateFlow<List<Quiz>> = _quizzes.asStateFlow()

    private val _currentQuiz = MutableStateFlow<Quiz?>(null)
    val currentQuiz: StateFlow<Quiz?> = _currentQuiz.asStateFlow()

    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = QuizRepository(database.quizDao(), database.questionDao())
        loadQuizzes()
    }

    private fun loadQuizzes() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getAllQuizzes().collect { quizList ->
                _quizzes.value = quizList
                _isLoading.value = false
            }
        }
    }

    fun loadQuizWithQuestions(quizId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            val quiz = repository.getQuizWithQuestions(quizId)
            _currentQuiz.value = quiz

            // Load questions separately
            repository.getQuestionsByQuizId(quizId).collect { questionList ->
                _questions.value = questionList
                _isLoading.value = false
            }
        }
    }

    fun createQuiz(title: String, questions: List<Question>) {
        viewModelScope.launch {
            val quiz = Quiz(
                title = title,
                questions = questions
            )
            repository.insertQuiz(quiz)
        }
    }

    fun updateQuiz(quizId: Long, title: String, questions: List<Question>) {
        viewModelScope.launch {
            val quiz = Quiz(
                id = quizId,
                title = title,
                questions = questions
            )
            repository.updateQuiz(quiz)
        }
    }

    fun addQuestion(quizId: Long, question: Question) {
        viewModelScope.launch {
            repository.insertQuestion(question.copy(quizId = quizId))
        }
    }

    fun deleteQuiz(quizId: Long) {
        viewModelScope.launch {
            repository.deleteQuiz(quizId)
        }
    }

    fun deleteQuestion(questionId: Long) {
        viewModelScope.launch {
            repository.deleteQuestion(questionId)
        }
    }

    fun clearCurrentQuiz() {
        _currentQuiz.value = null
        _questions.value = emptyList()
    }
}
