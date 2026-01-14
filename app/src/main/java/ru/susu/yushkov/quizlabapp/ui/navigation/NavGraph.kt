package ru.susu.yushkov.quizlabapp.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ru.susu.yushkov.quizlabapp.ui.screens.AboutScreen
import ru.susu.yushkov.quizlabapp.ui.screens.GameScreen
import ru.susu.yushkov.quizlabapp.ui.screens.ManageQuizListScreen
import ru.susu.yushkov.quizlabapp.ui.screens.PlayListScreen
import ru.susu.yushkov.quizlabapp.ui.screens.QuizEditorScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.PlayList.route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        composable(Screen.PlayList.route) {
            PlayListScreen(navController = navController)
        }

        composable(Screen.ManageQuizList.route) {
            ManageQuizListScreen(navController = navController)
        }

        composable(Screen.About.route) {
            AboutScreen(navController = navController)
        }

        composable(
            route = Screen.QuizEditor.route,
            arguments = listOf(
                navArgument("quizId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStackEntry ->
            val quizId = backStackEntry.arguments?.getLong("quizId") ?: 0L
            QuizEditorScreen(
                navController = navController,
                quizId = quizId
            )
        }

        composable(
            route = Screen.Game.route,
            arguments = listOf(
                navArgument("quizId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val quizId = backStackEntry.arguments?.getLong("quizId") ?: 0L
            GameScreen(
                navController = navController,
                quizId = quizId
            )
        }
    }
}
