package ru.susu.yushkov.quizlabapp.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ru.susu.yushkov.quizlabapp.data.models.Question
import ru.susu.yushkov.quizlabapp.data.repository.Contact
import ru.susu.yushkov.quizlabapp.data.repository.ContactRepository
import ru.susu.yushkov.quizlabapp.ui.components.QuestionItem
import ru.susu.yushkov.quizlabapp.ui.viewmodels.QuizViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizEditorScreen(
    navController: NavController,
    quizId: Long,
    viewModel: QuizViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val contactRepository = remember { ContactRepository() }

    var quizTitle by remember { mutableStateOf("") }
    var questions by remember { mutableStateOf(listOf<Question>()) }
    var showAddQuestionDialog by remember { mutableStateOf(false) }
    var showContactImportSettingsDialog by remember { mutableStateOf(false) }
    var showEditQuestionDialog by remember { mutableStateOf<Int?>(null) }
    var pendingContactQuestions by remember { mutableStateOf<List<Question>>(emptyList()) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showContactImportSettingsDialog = true
        }
    }

    LaunchedEffect(quizId) {
        if (quizId > 0) {
            viewModel.loadQuizWithQuestions(quizId)
        }
    }

    val currentQuiz by viewModel.currentQuiz.collectAsState()
    val loadedQuestions by viewModel.questions.collectAsState()

    LaunchedEffect(currentQuiz, loadedQuestions) {
        currentQuiz?.let {
            quizTitle = it.title
        }
        if (loadedQuestions.isNotEmpty()) {
            questions = loadedQuestions
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (quizId > 0) "Редактировать викторину" else "Создать викторину") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (quizTitle.isNotEmpty() && questions.isNotEmpty()) {
                                if (quizId > 0) {
                                    viewModel.updateQuiz(quizId, quizTitle, questions)
                                } else {
                                    viewModel.createQuiz(quizTitle, questions)
                                }
                                navController.popBackStack()
                            }
                        }
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Сохранить")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddQuestionDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить вопрос")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = quizTitle,
                onValueChange = { quizTitle = it },
                label = { Text("Название викторины") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Из контактов")
                }
            }

            Text(
                text = "Вопросы: ${questions.size}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (questions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Нет вопросов. Добавьте вопросы!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(questions) { index, question ->
                        QuestionItem(
                            question = question,
                            questionNumber = index + 1,
                            onDeleteClick = {
                                questions = questions.filterIndexed { i, _ -> i != index }
                            },
                            onEditClick = {
                                showEditQuestionDialog = index
                            }
                        )
                    }
                }
            }
        }

        if (showAddQuestionDialog) {
            AddQuestionDialog(
                onDismiss = { showAddQuestionDialog = false },
                onConfirm = { question ->
                    questions = questions + question
                    showAddQuestionDialog = false
                }
            )
        }

        // Диалог настроек импорта контактов
        if (showContactImportSettingsDialog) {
            ContactImportSettingsDialog(
                onDismiss = { showContactImportSettingsDialog = false },
                onConfirm = { settings ->
                    showContactImportSettingsDialog = false
                    scope.launch {
                        val contacts = contactRepository.getContacts(context)
                        val contactQuestions = generateContactQuestions(
                            contacts,
                            settings.questionCount,
                            settings.questionFormat
                        )
                        pendingContactQuestions = contactQuestions
                        // Навигация к preview экрану будет через новый роут
                        // Пока добавляем напрямую с возможностью редактирования
                        questions = questions + contactQuestions
                    }
                }
            )
        }

        // Диалог редактирования вопроса
        showEditQuestionDialog?.let { index ->
            val question = questions[index]
            EditQuestionDialog(
                question = question,
                onDismiss = { showEditQuestionDialog = null },
                onConfirm = { editedQuestion: Question ->
                    questions = questions.mapIndexed { i: Int, q: Question ->
                        if (i == index) editedQuestion else q
                    }
                    showEditQuestionDialog = null
                }
            )
        }
    }
}

@Composable
fun EditQuestionDialog(
    question: Question,
    onDismiss: () -> Unit,
    onConfirm: (Question) -> Unit
) {
    var questionText by remember { mutableStateOf(question.text) }
    var correctAnswer by remember { mutableStateOf(question.correctAnswer) }
    var wrongAnswers by remember {
        mutableStateOf(
            if (question.wrongAnswers.isEmpty()) listOf("") else question.wrongAnswers
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Редактировать вопрос") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = questionText,
                    onValueChange = { questionText = it },
                    label = { Text("Текст вопроса") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = correctAnswer,
                    onValueChange = { correctAnswer = it },
                    label = { Text("Правильный ответ") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Неправильные ответы:",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )

                wrongAnswers.forEachIndexed { index, wrongAnswer ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = wrongAnswer,
                            onValueChange = { newValue ->
                                wrongAnswers = wrongAnswers.toMutableList().apply {
                                    this[index] = newValue
                                }
                            },
                            label = { Text("Неправильный ответ ${index + 1}") },
                            modifier = Modifier.weight(1f)
                        )
                        if (wrongAnswers.size > 1) {
                            IconButton(
                                onClick = {
                                    wrongAnswers = wrongAnswers.filterIndexed { i, _ -> i != index }
                                }
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Удалить",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }

                TextButton(
                    onClick = {
                        wrongAnswers = wrongAnswers + ""
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Добавить неправильный ответ")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val validWrongAnswers = wrongAnswers.filter { it.isNotEmpty() }
                    if (questionText.isNotEmpty() && correctAnswer.isNotEmpty() && validWrongAnswers.isNotEmpty()) {
                        onConfirm(
                            Question(
                                id = question.id,
                                quizId = question.quizId,
                                text = questionText,
                                correctAnswer = correctAnswer,
                                wrongAnswers = validWrongAnswers
                            )
                        )
                    }
                }
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

@Composable
fun AddQuestionDialog(
    onDismiss: () -> Unit,
    onConfirm: (Question) -> Unit
) {
    var questionText by remember { mutableStateOf("") }
    var correctAnswer by remember { mutableStateOf("") }
    var wrongAnswers by remember { mutableStateOf(listOf("")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить вопрос") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = questionText,
                    onValueChange = { questionText = it },
                    label = { Text("Текст вопроса") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = correctAnswer,
                    onValueChange = { correctAnswer = it },
                    label = { Text("Правильный ответ") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Неправильные ответы:",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )

                wrongAnswers.forEachIndexed { index, wrongAnswer ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = wrongAnswer,
                            onValueChange = { newValue ->
                                wrongAnswers = wrongAnswers.toMutableList().apply {
                                    this[index] = newValue
                                }
                            },
                            label = { Text("Неправильный ответ ${index + 1}") },
                            modifier = Modifier.weight(1f)
                        )
                        if (wrongAnswers.size > 1) {
                            IconButton(
                                onClick = {
                                    wrongAnswers = wrongAnswers.filterIndexed { i, _ -> i != index }
                                }
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Удалить",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }

                TextButton(
                    onClick = {
                        wrongAnswers = wrongAnswers + ""
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Добавить неправильный ответ")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val validWrongAnswers = wrongAnswers.filter { it.isNotEmpty() }
                    if (questionText.isNotEmpty() && correctAnswer.isNotEmpty() && validWrongAnswers.isNotEmpty()) {
                        onConfirm(
                            Question(
                                text = questionText,
                                correctAnswer = correctAnswer,
                                wrongAnswers = validWrongAnswers
                            )
                        )
                    }
                }
            ) {
                Text("Добавить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

fun generateContactQuestions(
    contacts: List<Contact>,
    count: Int = 10,
    format: QuestionFormat = QuestionFormat.PhoneToName
): List<Question> {
    // Убираем дубликаты по имени контакта, оставляя только первый номер для каждого имени
    val uniqueContacts = contacts.distinctBy { it.name }

    // Берем нужное количество уникальных контактов
    val selectedContacts = uniqueContacts.shuffled().take(count)

    return selectedContacts.map { contact ->
        // Для неправильных ответов используем другие уникальные контакты
        val otherContacts = uniqueContacts
            .filter { it.name != contact.name }
            .shuffled()
            .take(3)

        val wrongAnswers = otherContacts.map {
            when (format) {
                QuestionFormat.PhoneToName -> it.name
                QuestionFormat.NameToPhone -> it.phoneNumber
            }
        }

        Question(
            text = when (format) {
                QuestionFormat.PhoneToName -> "Чей это номер: ${contact.phoneNumber}?"
                QuestionFormat.NameToPhone -> "Какой номер у ${contact.name}?"
            },
            correctAnswer = when (format) {
                QuestionFormat.PhoneToName -> contact.name
                QuestionFormat.NameToPhone -> contact.phoneNumber
            },
            wrongAnswers = wrongAnswers
        )
    }
}
