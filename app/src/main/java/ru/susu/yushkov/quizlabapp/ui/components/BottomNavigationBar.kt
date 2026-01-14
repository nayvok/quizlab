package ru.susu.yushkov.quizlabapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import ru.susu.yushkov.quizlabapp.ui.navigation.Screen

@Composable
fun BottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry.value?.destination

    NavigationBar(modifier = modifier) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Играть") },
            label = { Text("Играть") },
            selected = currentDestination?.hierarchy?.any { it.route == Screen.PlayList.route } == true,
            onClick = {
                navController.navigate(Screen.PlayList.route) {
                    popUpTo(Screen.PlayList.route) {
                        inclusive = true
                    }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Edit, contentDescription = "Управление") },
            label = { Text("Управление") },
            selected = currentDestination?.hierarchy?.any { it.route == Screen.ManageQuizList.route } == true,
            onClick = {
                navController.navigate(Screen.ManageQuizList.route) {
                    popUpTo(Screen.ManageQuizList.route) {
                        inclusive = true
                    }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Info, contentDescription = "О приложении") },
            label = { Text("О приложении") },
            selected = currentDestination?.hierarchy?.any { it.route == Screen.About.route } == true,
            onClick = {
                navController.navigate(Screen.About.route) {
                    popUpTo(Screen.About.route) {
                        inclusive = true
                    }
                }
            }
        )
    }
}
