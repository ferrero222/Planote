/*****************************************************************
 *  Package for main screen with circular pager
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.view.plan.component.week

/*****************************************************************
 * Imported packages
 ****************************************************************/
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.planote.DarkColorScheme
import com.example.planote.MyAppFont
import com.example.planote.PreviewContainer
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
import java.time.format.TextStyle
import java.util.Locale

/*****************************************************************
 * Variables, data, enum
 ****************************************************************/
sealed class WeekDialogDayTaskAlert {
    object None : WeekDialogDayTaskAlert()
    object DismissChanges : WeekDialogDayTaskAlert()
    object DiscardChanges : WeekDialogDayTaskAlert()
}

/*****************************************************************
 * Interfaces
 ****************************************************************/
/*****************************************************************
 * Private functions
 ****************************************************************/
@Composable
private fun WeekDialogDayTaskContent(
    dialogState: PlanWeekDialogDayDataHolder,
    loading: PlanWeekLoading,
    onDismissClick: () -> Unit,
    onTimeChange: (LocalTime) -> Unit,
    onTitleChange: (String) -> Unit,
    onDescChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
) {
    WeekLoading(loading) {
        Column(
            verticalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier.fillMaxSize().padding(25.dp),
        ) {
            WeekDialogDayTaskContentHeader(
                day = dialogState.day,
                onDismissClick = onDismissClick,
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(15.dp),
                modifier = Modifier.weight(1f).fillMaxWidth(),
            ) {
                WeekDialogDayTaskContentTime(
                    task = dialogState.editingTask,
                    onTimeChange = onTimeChange,
                )
                WeekDialogDayTaskContentTitle(
                    task = dialogState.editingTask,
                    onTitleChange = onTitleChange,
                )
                WeekDialogDayTaskContentDescription(
                    task = dialogState.editingTask,
                    onDescChange = onDescChange,
                )
            }
            WeekDialogDayTaskContentFooter(
                onSave = onSave,
                onCancel = onCancel,
            )
        }
    }
}

@Composable
private fun WeekDialogDayTaskContentHeader(
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
            fontSize = 20.sp,
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
private fun WeekDialogDayTaskContentTime(
    task: PlanWeekDayTaskDomain,
    onTimeChange: (LocalTime) -> Unit
) {
    var hoursText by remember(task.id) { mutableStateOf(task.time.hour.toString().padStart(2, '0')) }
    var minutesText by remember(task.id) { mutableStateOf(task.time.minute.toString().padStart(2, '0')) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = hoursText,
                onValueChange = { hours ->
                    if (hours.length <= 2) {
                        hoursText = hours
                        val hour = hours.toIntOrNull()?.coerceIn(0, 23) ?: 0
                        val minute = task.time.minute
                        onTimeChange(LocalTime.of(hour, minute))
                    }
                },
                label = {
                    Text(
                        text = "Ч",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    )
                },
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                ),
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier.weight(1f).align(Alignment.CenterVertically),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                )
            )
            Text(
                text = ":",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 8.dp).align(Alignment.CenterVertically)
            )
            OutlinedTextField(
                value = minutesText,
                onValueChange = { minutes ->
                    if (minutes.length <= 2) {
                        minutesText = minutes
                        val minute = minutes.toIntOrNull()?.coerceIn(0, 59) ?: 0
                        val hour = task.time.hour
                        onTimeChange(LocalTime.of(hour, minute))
                    }
                },
                label = {
                    Text(
                        text = "M",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    )
                },                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                ),
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier.weight(1f).align(Alignment.CenterVertically),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                )
            )
        }
    }
}

@Composable
private fun WeekDialogDayTaskContentTitle(
    task: PlanWeekDayTaskDomain,
    onTitleChange: (String) -> Unit
){
    Column(modifier = Modifier.fillMaxWidth()) { //Description
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
private fun WeekDialogDayTaskContentDescription(
    task: PlanWeekDayTaskDomain,
    onDescChange: (String) -> Unit
){
    Column(modifier = Modifier.fillMaxWidth()) { //Description
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
private fun WeekDialogDayTaskContentFooter(
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

@Composable
private fun WeekDialogDayTaskAlertHandler(
    weekDialogAlert: WeekDialogDayTaskAlert,
    viewModel: PlanWeekViewModel,
    dialogStateChange: (PlanWeekDialogMode) -> Unit,
    onDismiss: () -> Unit
) {
    when(weekDialogAlert) {
        is WeekDialogDayTaskAlert.DismissChanges -> {
            WeekAlert(
                title = "Вернуться назад?",
                description = "Несохранённые изменения будут потеряны",
                confirmText = "Вернуться",
                dismissText = "Отменить",
                onConfirm = {
                    onDismiss()
                    viewModel.discardEditTask()
                    dialogStateChange(PlanWeekDialogMode.DAYEDIT)
                },
                onDismiss = {
                    onDismiss()
                }
            )
        }
        is WeekDialogDayTaskAlert.DiscardChanges -> {
            WeekAlert(
                title = "Отменить изменения?",
                description = "Несохранённые изменения будут потеряны",
                confirmText = "Отменить",
                dismissText = "Вернуться",
                onConfirm = {
                    onDismiss()
                    viewModel.discardEditTask()
                    dialogStateChange(PlanWeekDialogMode.DAYEDIT)
                },
                onDismiss = {
                    onDismiss()
                }
            )
        }
        is WeekDialogDayTaskAlert.None -> { }
    }
}

/*****************************************************************
 * Public functions
 ****************************************************************/
@Composable
fun WeekDialogDayTaskContent(
    viewModel: PlanWeekViewModel = hiltViewModel(),
    dialogStateChange: (PlanWeekDialogMode) -> Unit
) {
    val dialogState by viewModel.dialogDayState.collectAsStateWithLifecycle()
    var weekDialogAlert by remember { mutableStateOf<WeekDialogDayTaskAlert>(WeekDialogDayTaskAlert.None) }

    WeekDialogDayTaskContent(
        dialogState = dialogState,
        loading = dialogState.loading,
        onDismissClick = { weekDialogAlert = WeekDialogDayTaskAlert.DismissChanges },
        onTimeChange = { newTime -> viewModel.updateEditTask(dialogState.editingTask.copy(time = newTime)) },
        onTitleChange = { newTitle -> viewModel.updateEditTask(dialogState.editingTask.copy(title = newTitle)) },
        onDescChange = { newDesc -> viewModel.updateEditTask(dialogState.editingTask.copy(description = newDesc)) },
        onSave = {
            viewModel.saveEditTask()
            dialogStateChange(PlanWeekDialogMode.DAYEDIT)
        },
        onCancel = { weekDialogAlert = WeekDialogDayTaskAlert.DiscardChanges },
    )

    WeekDialogDayTaskAlertHandler(
        weekDialogAlert = weekDialogAlert,
        viewModel = viewModel,
        dialogStateChange = dialogStateChange,
        onDismiss = { weekDialogAlert = WeekDialogDayTaskAlert.None }
    )
}

/*****************************************************************
 * Preview functions
 ****************************************************************/
@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun WeekDialogDayTaskContentPreview(
    day: PlanWeekDayDomain = PlanWeekDayDomain(
        id = 1,
        num = 0,
        title = "План на понедельник"
    ),
    editingTask: PlanWeekDayTaskDomain = PlanWeekDayTaskDomain(
        id = 1,
        title = "Новая задача",
        description = "Описание задачи",
        time = LocalTime.of(14, 30)
    ),
    loading: PlanWeekLoading = PlanWeekLoading.Idle
) {
    PreviewContainer{
        WeekDialogCard{
            WeekDialogDayTaskContent(
                dialogState = PlanWeekDialogDayDataHolder(day = day, editingTask = editingTask),
                loading = loading,
                onDismissClick = {},
                onTimeChange = {},
                onTitleChange = {},
                onDescChange = {},
                onSave = {},
                onCancel = {},
            )
        }
    }
}
