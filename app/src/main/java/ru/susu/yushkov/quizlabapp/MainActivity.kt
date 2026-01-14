package ru.susu.yushkov.quizlabapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import ru.susu.yushkov.quizlabapp.ui.navigation.NavGraph
import ru.susu.yushkov.quizlabapp.ui.theme.QuizLabAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuizLabAppTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}