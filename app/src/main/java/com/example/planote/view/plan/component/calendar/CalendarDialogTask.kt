/*****************************************************************
 *  Package for main screen with circular pager
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.view.plan.component.calendar

/*****************************************************************
 * Imported packages
 ****************************************************************/
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.planote.DarkColorScheme
import com.example.planote.MyAppFont
import com.example.planote.viewModel.plan.PlanCalendarDialogMode
import com.example.planote.viewModel.plan.PlanCalendarEntityDomain
import com.example.planote.viewModel.plan.PlanCalendarLoading
import com.example.planote.viewModel.plan.PlanCalendarTaskDomain
import com.example.planote.viewModel.plan.PlanCalendarType
import com.example.planote.viewModel.plan.PlanCalendarViewModel
import me.trishiraj.shadowglow.shadowGlow
import java.time.format.TextStyle
import java.util.Locale

/*****************************************************************
 * Variables, data, enum
 ****************************************************************/
sealed class CalendarDialogTaskAlert {
    object None : CalendarDialogTaskAlert()
    object DismissChanges : CalendarDialogTaskAlert()
    object DiscardChanges : CalendarDialogTaskAlert()
}

/*****************************************************************
 * Interfaces
 ****************************************************************/
/*****************************************************************
 * Private functions
 ****************************************************************/
@Composable
private fun CalendarDialogTaskContentHeader(
    entity: PlanCalendarEntityDomain,
    type: PlanCalendarType, onDismissClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = { onDismissClick() },
            modifier = Modifier.align(Alignment.CenterStart).size(18.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBackIosNew,
                contentDescription = "Назад",
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
        val month = entity.date.month.getDisplayName(TextStyle.FULL, Locale.getDefault()).replaceFirstChar { it.uppercaseChar() }
        val textHeader = when (type) {
            PlanCalendarType.DAYS -> "$month ${entity.date.dayOfMonth}"
            PlanCalendarType.MONTHS -> "$month ${entity.date.year}"
            PlanCalendarType.YEARS -> "${entity.date.year} year"
        }
        Text(
            text = textHeader,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.align(Alignment.Center)
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .background(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(10.dp)
                )
        ) {
            Text(
                text = "EDIT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
        }
    }
}

@Composable
private fun CalendarDialogTaskContentTitle(
    task: PlanCalendarTaskDomain,
    onTitleChange: (String) -> Unit
){
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "ЗАГОЛОВОК",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = task.title ?: "",
            placeholder = { Text(text = "Введите заголовок", color = MaterialTheme.colorScheme.onSurface) },
            onValueChange = onTitleChange,
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
            shape = RoundedCornerShape(5.dp),
            maxLines = 5,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                unfocusedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
            ),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Редактировать",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(15.dp)
                )
            }
        )
    }
}

@Composable
private fun CalendarDialogTaskContentDescription(
    task: PlanCalendarTaskDomain,
    onDescChange: (String) -> Unit
){
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "ОПИСАНИЕ",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = task.description ?: "",
            placeholder = { Text(text = "Введите описание", color = MaterialTheme.colorScheme.onSurface) },
            onValueChange = onDescChange,
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
            shape = RoundedCornerShape(5.dp),
            maxLines = 5,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                unfocusedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
            ),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Редактировать",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(15.dp)
                )
            }
        )
    }
}

@Composable
private fun CalendarDialogTaskContentFooter(
    onSave: () -> Unit,
    onCancel: () -> Unit
){
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextButton(
            shape = RoundedCornerShape(10.dp),
            onClick = { onCancel() },
            contentPadding = PaddingValues(vertical = 15.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "ОТМЕНИТЬ",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                fontWeight = FontWeight.Medium,
            )
        }
        Button(
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colorScheme.background,
                containerColor = MaterialTheme.colorScheme.primary
            ),
            contentPadding = PaddingValues(vertical = 15.dp),
            onClick = { onSave() },
            modifier = Modifier.fillMaxWidth().shadowGlow(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f), offsetX = 0.dp, offsetY = 0.dp, blurRadius = 17.dp)

        ) {
            Text(text = "ДОБАВИТЬ", fontWeight = FontWeight.Bold)
        }
    }
}

/*****************************************************************
 * Public functions
 ****************************************************************/
@Composable
private fun CalendarDialogTaskAlertHandler(
    calendarDialogAlert: CalendarDialogTaskAlert,
    viewModel: PlanCalendarViewModel,
    dialogStateChange: (PlanCalendarDialogMode) -> Unit,
    onDismiss: () -> Unit
) {
    when(calendarDialogAlert) {
        is CalendarDialogTaskAlert.DismissChanges -> {
            CalendarAlert(
                title = "Вернуться назад?",
                description = "Несохранённые изменения будут потеряны",
                confirmText = "Вернуться",
                dismissText = "Отменить",
                onConfirm = {
                    onDismiss()
                    viewModel.discardEditTask()
                    dialogStateChange(PlanCalendarDialogMode.EDIT)
                },
                onDismiss = {
                    onDismiss()
                }
            )
        }
        is CalendarDialogTaskAlert.DiscardChanges -> {
            CalendarAlert(
                title = "Отменить изменения?",
                description = "Несохранённые изменения будут потеряны",
                confirmText = "Отменить",
                dismissText = "Вернуться",
                onConfirm = {
                    onDismiss()
                    viewModel.discardEditTask()
                    dialogStateChange(PlanCalendarDialogMode.EDIT)
                },
                onDismiss = {
                    onDismiss()
                }
            )
        }
        is CalendarDialogTaskAlert.None -> { }
    }
}

@Composable
fun CalendarDialogTaskContent(
    viewModel: PlanCalendarViewModel = hiltViewModel(),
    dialogStateChange: (PlanCalendarDialogMode) -> Unit
) {
    val calendarDialogState by viewModel.dialogState.collectAsStateWithLifecycle()
    val calendarDataState by viewModel.dataState.collectAsStateWithLifecycle()
    var calendarDialogAlert by remember { mutableStateOf<CalendarDialogTaskAlert>(CalendarDialogTaskAlert.None) }
    CalendarLoading(calendarDialogState.loading) {
        Column(
            verticalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier.fillMaxSize().padding(25.dp),
        ) {
            CalendarDialogTaskContentHeader(
                entity = calendarDialogState.entity,
                type = calendarDataState.type,
                onDismissClick = {
                    calendarDialogAlert = CalendarDialogTaskAlert.DismissChanges
                }
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(15.dp),
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) {
                CalendarDialogTaskContentTitle(
                    task = calendarDialogState.editingTask,
                    onTitleChange = { newTitle ->
                        viewModel.updateEditTask(calendarDialogState.editingTask.copy(title = newTitle))
                    }
                )
                CalendarDialogTaskContentDescription(
                    task = calendarDialogState.editingTask,
                    onDescChange = { newDesc ->
                        viewModel.updateEditTask(calendarDialogState.editingTask.copy(description = newDesc))
                    }
                )
            }

            CalendarDialogTaskContentFooter(
                onSave = {
                    viewModel.saveEditTask()
                    dialogStateChange(PlanCalendarDialogMode.EDIT)
                },
                onCancel = {
                    calendarDialogAlert = CalendarDialogTaskAlert.DiscardChanges
                },
            )
        }

        CalendarDialogTaskAlertHandler(
            calendarDialogAlert = calendarDialogAlert,
            viewModel = viewModel,
            dialogStateChange = dialogStateChange,
            onDismiss = { calendarDialogAlert = CalendarDialogTaskAlert.None }
        )
    }
}

/*****************************************************************
 * Preview functions
 ****************************************************************/
@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun CalendarDialogTaskContentPreview() {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = MyAppFont,
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(35.dp)){
            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .width(340.dp)
                    .height(650.dp)
                    .border(
                        width = 1.dp,
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.17f)
                    )
            ) {
                CalendarLoading(PlanCalendarLoading.Idle) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(15.dp),
                        modifier = Modifier.fillMaxSize().padding(25.dp),
                    ) {
                        CalendarDialogTaskContentHeader(
                            entity = PlanCalendarEntityDomain(),
                            type = PlanCalendarType.DAYS,
                            onDismissClick = {}
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(15.dp),
                            modifier = Modifier.weight(1f).fillMaxWidth()
                        ) {
                            CalendarDialogTaskContentTitle(
                                task = PlanCalendarTaskDomain(id = 1, title = "Задача 1", description = "Основной план", isDone = true),
                                onTitleChange = {}
                            )
                            CalendarDialogTaskContentDescription(
                                task = PlanCalendarTaskDomain(id = 1, title = "Задача 1", description = "Основной план", isDone = true),
                                onDescChange = {}
                            )
                        }

                        CalendarDialogTaskContentFooter(
                            onSave = {},
                            onCancel = {},
                        )
                    }
                }
            }
        }
    }
}
