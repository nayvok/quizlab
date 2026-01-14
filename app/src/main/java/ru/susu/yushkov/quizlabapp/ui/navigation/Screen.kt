package ru.susu.yushkov.quizlabapp.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object PlayList : Screen("play_list")
    data object ManageQuizList : Screen("manage_quiz_list")
    data object QuizEditor : Screen("quiz_editor/{quizId}") {
        fun createRoute(quizId: Long = 0) = "quiz_editor/$quizId"
    }
    data object Game : Screen("game/{quizId}") {
        fun createRoute(quizId: Long) = "game/$quizId"
    }
    data object About : Screen("about")
}
