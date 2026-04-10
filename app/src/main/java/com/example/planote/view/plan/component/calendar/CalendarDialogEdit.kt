/*****************************************************************
 *  Package for main screen with circular pager
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.view.plan.component.calendar

/*****************************************************************
 * Imported packages
 ****************************************************************/
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.planote.DarkColorScheme
import com.example.planote.MyAppFont
import com.example.planote.viewModel.plan.PlanCalendarDialogDataHolder
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
sealed class CalendarDialogEditAlert {
    object None : CalendarDialogEditAlert()
    object DiscardChanges : CalendarDialogEditAlert()
    object DeleteEntity : CalendarDialogEditAlert()
    data class DeleteTask(val task: PlanCalendarTaskDomain) : CalendarDialogEditAlert()
}

/*****************************************************************
 * Interfaces
 ****************************************************************/
/*****************************************************************
 * Private functions
 ****************************************************************/
@Composable
private fun CalendarDialogEditContentHeader(
    entity: PlanCalendarEntityDomain,
    type: PlanCalendarType,
    onDismissClick: () -> Unit
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
private fun CalendarDialogEditContentDescription(
    entity: PlanCalendarEntityDomain,
    onTitleChange: (String) -> Unit
){
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "ОПИСАНИЕ",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = entity.title ?: "",
            placeholder = { Text(text = "Введите описание", color = MaterialTheme.colorScheme.onSurface) },
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
private fun CalendarDialogEditContentTasksItem(
    task: PlanCalendarTaskDomain,
    onTaskToggle: (PlanCalendarTaskDomain) -> Unit,
    onTaskEdit: (PlanCalendarTaskDomain) -> Unit,
    onTaskDelete: (PlanCalendarTaskDomain) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), shape = RoundedCornerShape(5.dp))
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(10.dp)
        ) {
            Text(
                text = task.title ?: "Нет описания",
                color = if(task.isDone) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = { onTaskEdit(task) },
                modifier = Modifier.size(20.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.EditNote,
                    contentDescription = "Редактировать",
                    tint = if(task.isDone) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.primary,
                )
            }
            IconButton(
                onClick = { onTaskDelete(task) },
                modifier = Modifier.size(20.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint = if(task.isDone) MaterialTheme.colorScheme.onError.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onError,
                )
            }

            Box(
                modifier = Modifier.size(20.dp)
            ) {
                Checkbox(
                    checked = task.isDone,
                    onCheckedChange = { newValue -> onTaskToggle(task.copy(isDone = newValue)) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        uncheckedColor = MaterialTheme.colorScheme.primary,
                        checkmarkColor = MaterialTheme.colorScheme.surface
                    ),
                )
            }
        }
    }
}

@Composable
private fun CalendarDialogEditContentTasks(
    tasks: List<PlanCalendarTaskDomain>,
    onTaskToggle: (PlanCalendarTaskDomain) -> Unit,
    onTaskEdit: (PlanCalendarTaskDomain) -> Unit,
    onTaskDelete: (PlanCalendarTaskDomain) -> Unit
) {
    val listState = rememberLazyListState()
    Column{
        Box(modifier = Modifier.fillMaxWidth()){
            Text(
                text = "ЗАДАЧИ",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.CenterStart)
            )
            if(tasks.isNotEmpty()){
                Text(
                    text = "${tasks.size} АКТИВНЫХ",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }

        Box(modifier = Modifier.heightIn(max = 250.dp)) {
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(tasks.filterNot { it.title.isNullOrBlank() && it.description.isNullOrBlank() }) { task ->
                    CalendarDialogEditContentTasksItem(
                        task = task,
                        onTaskToggle = onTaskToggle,
                        onTaskEdit = onTaskEdit,
                        onTaskDelete = onTaskDelete
                    )
                }
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                AnimatedVisibility(
                    visible = listState.canScrollForward,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    enter = fadeIn(animationSpec = tween(150)),
                    exit = fadeOut(animationSpec = tween(150))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                        MaterialTheme.colorScheme.surface
                                    )
                                )
                            )
                    )
                }
            }
        }

        TextButton(
            shape = RoundedCornerShape(5.dp),
            onClick = { onTaskEdit(PlanCalendarTaskDomain()) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .border(width = 1.dp,
                    shape = RoundedCornerShape(5.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text("Добавить задачу", fontSize = 14.sp)
        }
    }
}

@Composable
private fun CalendarDialogEditContentFooter(
    onSave: () -> Unit,
    onDelete: () -> Unit
){
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextButton(
            shape = RoundedCornerShape(10.dp),
            onClick = { onDelete() },
            contentPadding = PaddingValues(vertical = 15.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp,
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.onError.copy(alpha = 0.15f))
        ) {
            Text(
                text = "ОЧИСТИТЬ ВСЁ",
                color = MaterialTheme.colorScheme.onError.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.shadowGlow(color = MaterialTheme.colorScheme.onError.copy(alpha = 0.08f), offsetX = 0.dp, offsetY = 0.dp, blurRadius = 17.dp)
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
            modifier = Modifier
                .fillMaxWidth()
                .shadowGlow(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                    offsetX = 0.dp,
                    offsetY = 0.dp,
                    blurRadius = 17.dp)

        ) {
            Text(text = "СОХРАНИТЬ", fontWeight = FontWeight.Bold)
        }
    }
}

/*****************************************************************
 * Private functions
 ****************************************************************/
@Composable
private fun CalendarDialogEditAlertHandler(
    calendarDialogAlert: CalendarDialogEditAlert,
    viewModel: PlanCalendarViewModel,
    dialogStateChange: (PlanCalendarDialogMode) -> Unit,
    calendarDialogState: PlanCalendarDialogDataHolder,
    onDismiss: () -> Unit
) {
    when(calendarDialogAlert) {
        is CalendarDialogEditAlert.DiscardChanges -> {
            CalendarAlert(
                title = "Отменить изменения?",
                description = "Несохранённые изменения будут потеряны",
                confirmText = "Отменить",
                dismissText = "Вернуться",
                onConfirm = {
                    onDismiss()
                    viewModel.discardEntityAndTasks()
                    if(calendarDialogState.entity.title.isNullOrEmpty() && calendarDialogState.tasks.isEmpty())
                        dialogStateChange(PlanCalendarDialogMode.IDLE)
                    else
                        dialogStateChange(PlanCalendarDialogMode.VIEW)
                },
                onDismiss = {
                    onDismiss()
                }
            )
        }
        is CalendarDialogEditAlert.DeleteEntity -> {
            CalendarAlert(
                title = "Удалить запись?",
                description = "Все данные будут удалены",
                confirmText = "Удалить",
                dismissText = "Отменить",
                onConfirm = {
                    onDismiss()
                    viewModel.clearEntityAndTasks()
                    dialogStateChange(PlanCalendarDialogMode.IDLE)
                },
                onDismiss = {
                    onDismiss()
                }
            )
        }
        is CalendarDialogEditAlert.DeleteTask -> {
            val task = calendarDialogAlert.task
            CalendarAlert(
                title = "Удалить задачу?",
                description = "Эта задача будет удалена",
                confirmText = "Удалить",
                dismissText = "Отменить",
                onConfirm = {
                    onDismiss()
                    viewModel.deleteTask(task)
                },
                onDismiss = {
                    onDismiss()
                }
            )
        }
        is CalendarDialogEditAlert.None -> { }
    }
}

@Composable
fun CalendarDialogEditContent(
    viewModel: PlanCalendarViewModel = hiltViewModel(),
    dialogStateChange: (PlanCalendarDialogMode) -> Unit
) {
    val calendarDialogState by viewModel.dialogState.collectAsStateWithLifecycle()
    val calendarDataState by viewModel.dataState.collectAsStateWithLifecycle()
    var calendarDialogAlert by remember { mutableStateOf<CalendarDialogEditAlert>(CalendarDialogEditAlert.None) }
    CalendarLoading(calendarDialogState.loading)
    {
        Column(
            verticalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier.fillMaxSize().padding(25.dp),
        ) {
            CalendarDialogEditContentHeader( //header
                entity = calendarDialogState.entity,
                type = calendarDataState.type,
                onDismissClick = {
                    calendarDialogAlert = CalendarDialogEditAlert.DiscardChanges
                }
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(15.dp),
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) {
                CalendarDialogEditContentDescription( //description
                    entity = calendarDialogState.entity,
                    onTitleChange = { newTitle -> viewModel.updateEntity(calendarDialogState.entity.copy(title = newTitle))}
                )
                CalendarDialogEditContentTasks( //tasks
                    tasks = calendarDialogState.tasks,
                    onTaskToggle = {
                        task -> viewModel.updateTask(task)
                    },
                    onTaskEdit = {task ->
                        viewModel.loadEditTask(task)
                        dialogStateChange(PlanCalendarDialogMode.TASK)
                    },
                    onTaskDelete = { task ->
                        calendarDialogAlert = CalendarDialogEditAlert.DeleteTask(task = task)
                    },
                )
            }

            CalendarDialogEditContentFooter( //footer
                onSave = {
                    viewModel.saveEntityAndTasks()
                    if(calendarDialogState.entity.title.isNullOrEmpty() && calendarDialogState.tasks.isEmpty())
                        dialogStateChange(PlanCalendarDialogMode.IDLE)
                    else
                        dialogStateChange(PlanCalendarDialogMode.VIEW)
                },
                onDelete = {
                    calendarDialogAlert = CalendarDialogEditAlert.DeleteEntity
                },
            )
        }

        CalendarDialogEditAlertHandler(
            calendarDialogAlert = calendarDialogAlert,
            viewModel = viewModel,
            dialogStateChange = dialogStateChange,
            calendarDialogState = calendarDialogState,
            onDismiss = { calendarDialogAlert = CalendarDialogEditAlert.None }
        )
    }
}

/*****************************************************************
 * Preview functions
 ****************************************************************/
@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun CalendarDialogEditContentPreview() {
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
                CalendarLoading(PlanCalendarLoading.Idle)
                {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(15.dp),
                        modifier = Modifier.fillMaxSize().padding(25.dp),
                    ) {
                        CalendarDialogEditContentHeader( //header
                            entity = PlanCalendarEntityDomain(),
                            type = PlanCalendarType.DAYS,
                            onDismissClick = {}
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(15.dp),
                            modifier = Modifier.weight(1f).fillMaxWidth()
                        ) {
                            CalendarDialogEditContentDescription( //description
                                entity = PlanCalendarEntityDomain(title = "Пример описанисая"),
                                onTitleChange = {}
                            )
                            CalendarDialogEditContentTasks( //tasks
                                tasks = listOf(
                                    PlanCalendarTaskDomain(id = 1, title = "Задача 1", description = "Основной план", isDone = true),
                                    PlanCalendarTaskDomain(id = 2, title = "Задача 2", description = "Запасной варианfffdsfsfsdfdsfsdfdsтdsadsadsadsadad", isDone = false),
                                    PlanCalendarTaskDomain(id = 3, title = "Задача 3", description = "", isDone = false)),
                                onTaskToggle = {},
                                onTaskEdit = {},
                                onTaskDelete = {},
                            )
                        }

                        CalendarDialogEditContentFooter( //footer
                            onSave = {},
                            onDelete = {},
                        )
                    }
                }
            }
        }
    }
}