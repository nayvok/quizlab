package ru.susu.yushkov.quizlabapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ru.susu.yushkov.quizlabapp.ui.viewmodels.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    navController: NavController,
    quizId: Long,
    viewModel: GameViewModel = viewModel()
) {
    val gameState by viewModel.gameState.collectAsState()

    LaunchedEffect(quizId) {
        viewModel.startGame(quizId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Викторина") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                gameState.questions.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                gameState.isGameFinished -> {
                    GameFinishedScreen(
                        score = gameState.score,
                        totalQuestions = gameState.totalQuestions,
                        onPlayAgain = {
                            viewModel.startGame(quizId)
                        },
                        onExit = {
                            navController.popBackStack()
                        }
                    )
                }
                else -> {
                    val currentQuestion = viewModel.getCurrentQuestion()
                    val allAnswers = remember(gameState.currentQuestionIndex) {
                        viewModel.getAllAnswers()
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Progress
                        LinearProgressIndicator(
                            progress = { (gameState.currentQuestionIndex + 1).toFloat() / gameState.totalQuestions },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                        )

                        Text(
                            text = "Вопрос ${gameState.currentQuestionIndex + 1} из ${gameState.totalQuestions}",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = "Счет: ${gameState.score}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 32.dp)
                        )

                        // Question
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 32.dp)
                        ) {
                            Text(
                                text = currentQuestion?.text ?: "",
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(24.dp)
                            )
                        }

                        // Answers
                        val currentQuestion = viewModel.getCurrentQuestion()
                        val correctAnswer = currentQuestion?.correctAnswer
                        val hasSelectedAnswer = gameState.selectedAnswer != null
                        
                        allAnswers.forEach { answer ->
                            val isSelected = gameState.selectedAnswer == answer
                            val isCorrectAnswer = answer == correctAnswer
                            val isWrongSelected = isSelected && !isCorrectAnswer
                            val showCorrect = hasSelectedAnswer && isCorrectAnswer

                            Button(
                                onClick = {
                                    if (gameState.selectedAnswer == null) {
                                        viewModel.selectAnswer(answer)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = when {
                                        showCorrect -> Color(0xFF4CAF50) // Зеленый для правильного ответа
                                        isWrongSelected -> Color(0xFFF44336) // Красный для неправильного выбранного
                                        else -> MaterialTheme.colorScheme.primary
                                    }
                                ),
                                enabled = gameState.selectedAnswer == null
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = answer,
                                        fontSize = 16.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (showCorrect) {
                                        Icon(Icons.Default.Check, contentDescription = "Правильно")
                                    } else if (isWrongSelected) {
                                        Icon(Icons.Default.Close, contentDescription = "Неправильно")
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Next button
                        if (gameState.selectedAnswer != null) {
                            Button(
                                onClick = { viewModel.nextQuestion() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                            ) {
                                Text("Далее", fontSize = 18.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GameFinishedScreen(
    score: Int,
    totalQuestions: Int,
    onPlayAgain: () -> Unit,
    onExit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Игра окончена!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text(
            text = "Ваш результат:",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "$score / $totalQuestions",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "${(score.toFloat() / totalQuestions * 100).toInt()}%",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onPlayAgain,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            ) {
                Text("Играть снова", fontSize = 18.sp)
            }

            OutlinedButton(
                onClick = onExit,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            ) {
                Text("Выход", fontSize = 18.sp)
            }
        }
    }
}
