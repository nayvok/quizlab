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
import ru.susu.yushkov.quizlabapp.data.repository.QuizRepository

data class GameState(
    val currentQuestionIndex: Int = 0,
    val score: Int = 0,
    val totalQuestions: Int = 0,
    val questions: List<Question> = emptyList(),
    val isGameFinished: Boolean = false,
    val selectedAnswer: String? = null,
    val isAnswerCorrect: Boolean? = null
)

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: QuizRepository

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = QuizRepository(database.quizDao(), database.questionDao())
    }

    fun startGame(quizId: Long) {
        viewModelScope.launch {
            repository.getQuestionsByQuizId(quizId).collect { questions ->
                _gameState.value = GameState(
                    questions = questions.shuffled(),
                    totalQuestions = questions.size
                )
            }
        }
    }

    fun selectAnswer(answer: String) {
        val currentState = _gameState.value
        val currentQuestion = currentState.questions.getOrNull(currentState.currentQuestionIndex)
        
        if (currentQuestion != null) {
            val isCorrect = answer == currentQuestion.correctAnswer
            _gameState.value = currentState.copy(
                selectedAnswer = answer,
                isAnswerCorrect = isCorrect,
                score = if (isCorrect) currentState.score + 1 else currentState.score
            )
        }
    }

    fun nextQuestion() {
        val currentState = _gameState.value
        val nextIndex = currentState.currentQuestionIndex + 1

        if (nextIndex >= currentState.totalQuestions) {
            _gameState.value = currentState.copy(
                isGameFinished = true,
                selectedAnswer = null,
                isAnswerCorrect = null
            )
        } else {
            _gameState.value = currentState.copy(
                currentQuestionIndex = nextIndex,
                selectedAnswer = null,
                isAnswerCorrect = null
            )
        }
    }

    fun resetGame() {
        _gameState.value = GameState()
    }

    fun getCurrentQuestion(): Question? {
        val state = _gameState.value
        return state.questions.getOrNull(state.currentQuestionIndex)
    }

    fun getAllAnswers(): List<String> {
        val question = getCurrentQuestion() ?: return emptyList()
        return (question.wrongAnswers + question.correctAnswer).shuffled()
    }
}
