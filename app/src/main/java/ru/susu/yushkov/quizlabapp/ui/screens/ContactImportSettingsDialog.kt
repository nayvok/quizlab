package ru.susu.yushkov.quizlabapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class ContactImportSettings(
    val questionCount: Int = 10,
    val questionFormat: QuestionFormat = QuestionFormat.PhoneToName
)

enum class QuestionFormat {
    PhoneToName,  // "Чей это номер: +7XXX?"
    NameToPhone   // "Какой номер у XXX?"
}

@Composable
fun ContactImportSettingsDialog(
    onDismiss: () -> Unit,
    onConfirm: (ContactImportSettings) -> Unit,
    initialSettings: ContactImportSettings = ContactImportSettings()
) {
    var questionCount by remember { mutableStateOf(initialSettings.questionCount) }
    var questionFormat by remember { mutableStateOf(initialSettings.questionFormat) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Настройки импорта") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Количество вопросов
                Column {
                    Text(
                        text = "Количество вопросов: $questionCount",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(5, 10, 15, 20).forEach { count ->
                            FilterChip(
                                selected = questionCount == count,
                                onClick = { questionCount = count },
                                label = { Text("$count") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                HorizontalDivider()

                // Формат вопроса
                Column {
                    Text(
                        text = "Формат вопроса:",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = questionFormat == QuestionFormat.PhoneToName,
                            onClick = { questionFormat = QuestionFormat.PhoneToName },
                            label = { Text("Номер / Имя") },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = questionFormat == QuestionFormat.NameToPhone,
                            onClick = { questionFormat = QuestionFormat.NameToPhone },
                            label = { Text("Имя / Номер") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(ContactImportSettings(questionCount, questionFormat))
                }
            ) {
                Text("Импортировать")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
