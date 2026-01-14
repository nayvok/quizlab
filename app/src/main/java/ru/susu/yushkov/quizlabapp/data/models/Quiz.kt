package ru.susu.yushkov.quizlabapp.data.models

data class Quiz(
    val id: Long = 0,
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val questions: List<Question> = emptyList()
)
