/*****************************************************************
 *  Package for main screen with circular pager
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.view.plan.component.week

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
import androidx.compose.material.icons.filled.Schedule
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
import com.example.planote.viewModel.plan.PlanWeekDayDomain
import com.example.planote.viewModel.plan.PlanWeekDayTaskDomain
import com.example.planote.viewModel.plan.PlanWeekDialogDayDataHolder
import com.example.planote.viewModel.plan.PlanWeekDialogMode
import com.example.planote.viewModel.plan.PlanWeekLoading
import com.example.planote.viewModel.plan.PlanWeekViewModel
import me.trishiraj.shadowglow.shadowGlow
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/*****************************************************************
 * Variables, data, enum
 ****************************************************************/
sealed class WeekDialogDayEditAlert {
    object None : WeekDialogDayEditAlert()
    object DiscardChanges : WeekDialogDayEditAlert()
    object DeleteEntity : WeekDialogDayEditAlert()
    data class DeleteTask(val task: PlanWeekDayTaskDomain) : WeekDialogDayEditAlert()
}

/*****************************************************************
 * Interfaces
 ****************************************************************/
/*****************************************************************
 * Private functions
 ****************************************************************/
@Composable
private fun WeekDialogDayEditContentHeader(
    day: PlanWeekDayDomain,
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
        val date = LocalDate.now().with(DayOfWeek.MONDAY).plusDays(day.num.toLong())
        val month = date.month.getDisplayName(TextStyle.FULL, Locale.getDefault()).replaceFirstChar { it.uppercaseChar() }
        val textHeader = "$month ${date.dayOfMonth}"
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
private fun WeekDialogDayEditContentDescription(
    day: PlanWeekDayDomain,
    onTitleChange: (String) -> Unit
){
    Column{
        Text(
            text = "ОПИСАНИЕ",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = day.title ?: "",
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
private fun WeekDialogDayEditContentTasksItem(
    task: PlanWeekDayTaskDomain,
    onTaskEdit: (PlanWeekDayTaskDomain) -> Unit,
    onTaskDelete: (PlanWeekDayTaskDomain) -> Unit
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
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if(task.time != LocalTime.MIDNIGHT) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    modifier = Modifier.padding(end = 5.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = task.time.format(DateTimeFormatter.ofPattern("HH:mm")),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            IconButton(
                onClick = { onTaskEdit(task) },
                modifier = Modifier.size(20.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.EditNote,
                    contentDescription = "Редактировать",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            IconButton(
                onClick = { onTaskDelete(task) },
                modifier = Modifier.size(20.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint = MaterialTheme.colorScheme.onError,
                )
            }
        }
    }
}

@Composable
private fun WeekDialogDayEditContentTasks(
    tasks: List<PlanWeekDayTaskDomain>,
    onTaskEdit: (PlanWeekDayTaskDomain) -> Unit,
    onTaskDelete: (PlanWeekDayTaskDomain) -> Unit
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
                    WeekDialogDayEditContentTasksItem(
                        task = task,
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
            onClick = { onTaskEdit(PlanWeekDayTaskDomain()) },
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
private fun WeekDialogDayEditContentFooter(
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
            modifier = Modifier.fillMaxWidth().shadowGlow(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f), offsetX = 0.dp, offsetY = 0.dp, blurRadius = 17.dp)

        ) {
            Text(text = "СОХРАНИТЬ", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun WeekDialogDayEditAlertHandler(
    weekDialogAlert: WeekDialogDayEditAlert,
    viewModel: PlanWeekViewModel,
    dialogStateChange: (PlanWeekDialogMode) -> Unit,
    weekDialogState: PlanWeekDialogDayDataHolder,
    onDismiss: () -> Unit
) {
    when(weekDialogAlert) {
        is WeekDialogDayEditAlert.DiscardChanges -> {
            WeekAlert(
                title = "Отменить изменения?",
                description = "Несохранённые изменения будут потеряны",
                confirmText = "Отменить",
                dismissText = "Вернуться",
                onConfirm = {
                    onDismiss()
                    viewModel.discardDayAndTasks()
                    if(weekDialogState.day.title.isNullOrEmpty() && weekDialogState.tasks.isEmpty())
                        dialogStateChange(PlanWeekDialogMode.IDLE)
                    else
                        dialogStateChange(PlanWeekDialogMode.DAYVIEW)
                },
                onDismiss = {
                    onDismiss()
                }
            )
        }
        is WeekDialogDayEditAlert.DeleteEntity -> {
            WeekAlert(
                title = "Удалить запись?",
                description = "Все данные будут удалены",
                confirmText = "Удалить",
                dismissText = "Отменить",
                onConfirm = {
                    onDismiss()
                    viewModel.clearDayAndTasks()
                    dialogStateChange(PlanWeekDialogMode.IDLE)
                },
                onDismiss = {
                    onDismiss()
                }
            )
        }
        is WeekDialogDayEditAlert.DeleteTask -> {
            val task = weekDialogAlert.task
            WeekAlert(
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
        is WeekDialogDayEditAlert.None -> { }
    }
}

/*****************************************************************
 * Public functions
 ****************************************************************/
@Composable
fun WeekDialogDayEditContent(
    viewModel: PlanWeekViewModel = hiltViewModel(),
    dialogStateChange: (PlanWeekDialogMode) -> Unit
) {
    val weekDialogState by viewModel.dialogDayState.collectAsStateWithLifecycle()
    var weekDialogAlert by remember { mutableStateOf<WeekDialogDayEditAlert>(WeekDialogDayEditAlert.None) }
    WeekLoading(weekDialogState.loading)
    {
        Column(
            verticalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier.fillMaxSize().padding(25.dp),
        ) {
            WeekDialogDayEditContentHeader( //header
                day = weekDialogState.day,
                onDismissClick = {
                    weekDialogAlert = WeekDialogDayEditAlert.DiscardChanges
                }
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(15.dp),
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) {
                WeekDialogDayEditContentDescription( //description
                    day = weekDialogState.day,
                    onTitleChange = { newTitle -> viewModel.updateDay(weekDialogState.day.copy(title = newTitle))}
                )
                WeekDialogDayEditContentTasks( //tasks
                    tasks = weekDialogState.tasks,
                    onTaskEdit = {task ->
                        viewModel.loadEditTask(task)
                        dialogStateChange(PlanWeekDialogMode.DAYTASK)
                    },
                    onTaskDelete = { task ->
                        weekDialogAlert = WeekDialogDayEditAlert.DeleteTask(task = task)
                    },
                )
            }

            WeekDialogDayEditContentFooter( //footer
                onSave = {
                    viewModel.saveDayAndTasks()
                    if(weekDialogState.day.title.isNullOrEmpty() && weekDialogState.tasks.isEmpty())
                        dialogStateChange(PlanWeekDialogMode.IDLE)
                    else
                        dialogStateChange(PlanWeekDialogMode.DAYVIEW)
                },
                onDelete = {
                    weekDialogAlert = WeekDialogDayEditAlert.DeleteEntity
                },
            )
        }

        WeekDialogDayEditAlertHandler(
            weekDialogAlert = weekDialogAlert,
            viewModel = viewModel,
            dialogStateChange = dialogStateChange,
            weekDialogState = weekDialogState,
            onDismiss = { weekDialogAlert = WeekDialogDayEditAlert.None }
        )
    }
}

/*****************************************************************
 * Preview functions
 ****************************************************************/
@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun WeekDialogDayEditContentPreview() {
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
                WeekLoading(PlanWeekLoading.Idle)
                {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(15.dp),
                        modifier = Modifier.fillMaxSize().padding(25.dp),
                    ) {
                        WeekDialogDayEditContentHeader( //header
                            day = PlanWeekDayDomain(),
                            onDismissClick = {}
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(15.dp),
                            modifier = Modifier.weight(1f).fillMaxWidth()
                        ) {
                            WeekDialogDayEditContentDescription( //description
                                day = PlanWeekDayDomain(id = 1, title = "Пример названия"),
                                onTitleChange = {}
                            )
                            WeekDialogDayEditContentTasks( //tasks
                                tasks = listOf(
                                    PlanWeekDayTaskDomain(id = 1, title = "Задача 1", description = "Основной план", time = LocalTime.NOON),
                                    PlanWeekDayTaskDomain(id = 2, title = "Задача 2", description = "Запасной варианfffdsfsfsdfdsfsdfdsтdsadsadsadsadad", time = LocalTime.NOON),
                                    PlanWeekDayTaskDomain(id = 3, title = "Задача 3", description = "")),
                                onTaskEdit = {},
                                onTaskDelete = {},
                            )
                        }

                        WeekDialogDayEditContentFooter( //footer
                            onSave = {},
                            onDelete = {},
                        )
                    }
                }
            }
        }
    }
}
