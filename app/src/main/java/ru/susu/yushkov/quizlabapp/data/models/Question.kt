package ru.susu.yushkov.quizlabapp.data.models

data class Question(
    val id: Long = 0,
    val quizId: Long = 0,
    val text: String,
    val correctAnswer: String,
    val wrongAnswers: List<String>
)
