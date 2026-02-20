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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.planote.view.plan.PlannerDialogType
import com.example.planote.viewModel.plan.PlanCalendarEntityDomain
import com.example.planote.viewModel.plan.PlanCalendarLoadingStatus
import com.example.planote.viewModel.plan.PlanCalendarTaskDomain
import com.example.planote.viewModel.plan.PlanCalendarType
import com.example.planote.viewModel.plan.PlanCalendarViewModel
import me.trishiraj.shadowglow.shadowGlow
import java.time.format.TextStyle
import java.util.Locale

/*****************************************************************
 * Variables, data, enum
 ****************************************************************/
/*****************************************************************
 * Interfaces
 ****************************************************************/
/*****************************************************************
 * Private functions
 ****************************************************************/
@Composable
private fun CalendarDialogEditContentHeader(entity: PlanCalendarEntityDomain, type: PlanCalendarType, onDismissClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = { onDismissClick() },
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 10.dp).size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBackIosNew,
                contentDescription = "Назад",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
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
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.align(Alignment.Center)
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 5.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(15.dp)
                )
                .padding(horizontal = 12.dp, vertical = 5.dp)

        ) {
            Text(
                text = "EDIT",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.background
            )
        }
    }
}

@Composable
private fun CalendarDialogTaskContentDescription(entity: PlanCalendarEntityDomain, onTitleChange: (String) -> Unit){
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) { //Description
        Text(
            text = "ОПИСАНИЕ",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
        )
        OutlinedTextField(
            value = entity.title ?: "",
            placeholder = { Text(text = "", color = MaterialTheme.colorScheme.onSurface) },
            onValueChange = onTitleChange,
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
            )
        )
    }
}

@Composable
private fun CalendarDialogTaskContentTasks(tasks: List<PlanCalendarTaskDomain>, onTaskUpdate: (PlanCalendarTaskDomain) -> Unit){
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
        Text(
            text = "ЗАДАЧИ",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )

        if(!tasks.isEmpty()) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .heightIn(max = 200.dp)
                    .fillMaxWidth()
            ) {
                items(tasks, key = { it.id }) { task ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = task.isDone,
                            onCheckedChange = {newValue -> onTaskUpdate(task.copy(isDone = newValue))},
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary, uncheckedColor = Color.Gray)
                        )
                        Text(
                            text = task.title ?: "Нет описания",
                            textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None,
                            color = if (task.isDone) Color.Gray else MaterialTheme.colorScheme.onSurface,
                            fontSize = 15.sp,
                            modifier = Modifier.weight(1f).padding(top = 4.dp)
                        )
                        Row {
                            IconButton(
                                onClick = {/* TODO: Редактировать задачу */ }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EditNote,
                                    contentDescription = "Редактировать",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            IconButton(
                                onClick = {onTaskUpdate(PlanCalendarTaskDomain())}
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Удалить",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth().padding(top = 5.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Button(
                onClick = { /* TODO: Добавить задачу */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.0f),
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text("Добавить задачу", fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun CalendarDialogTaskContentFooter(onSave: () -> Unit, onDelete: () -> Unit){
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextButton(
            shape = RoundedCornerShape(10.dp),
            onClick = { onDelete() },
            contentPadding = PaddingValues(vertical = 14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp,
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
        ) {
            Text(
                text = "ОЧИСТИТЬ ВСЁ",
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.shadowGlow(color = MaterialTheme.colorScheme.error.copy(alpha = 0.08f), offsetX = 0.dp, offsetY = 0.dp, blurRadius = 17.dp)
            )
        }
        Button(
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colorScheme.background,
                containerColor = MaterialTheme.colorScheme.primary
            ),
            contentPadding = PaddingValues(vertical = 14.dp),
            onClick = { onSave() },
            modifier = Modifier.fillMaxWidth().shadowGlow(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f), offsetX = 0.dp, offsetY = 0.dp, blurRadius = 17.dp)

        ) {
            Text(text = "СОХРАНИТЬ", fontWeight = FontWeight.Bold)
        }
    }
}


@Composable
fun CalendarDialogTaskContent(
    viewModel: PlanCalendarViewModel = hiltViewModel(),
    localState: CalendarDialogLocal,
    type: PlanCalendarType,
    dialogStateChange: (PlannerDialogType) -> Unit,
    dialogLocalStateChange: (CalendarDialogLocal.() -> CalendarDialogLocal) -> Unit
) {
    val loadingCoroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(vertical = 15.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        CalendarDialogEditContentHeader(
            entity = localState.entityLocal,
            type = type,
            onDismissClick = {
                dialogLocalStateChange{ copy(entityLocal = localState.entityOrigin, tasksLocal = localState.tasksOrigin) }
                dialogStateChange(PlannerDialogType.CalendarDetails(entity = localState.entityLocal, type = type, mode = CalendarDialogMode.VIEW))
            }
        )

        Box(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                CalendarDialogEditContentDescription(
                    entity = localState.entityLocal,
                    onTitleChange = { newTitle ->
                        dialogLocalStateChange { copy(entityLocal = localState.entityLocal.copy(title = newTitle)) }
                    }
                )
                CalendarDialogEditContentTasks(
                    tasks = localState.tasksLocal,
                    onTaskUpdate = { task: PlanCalendarTaskDomain ->
                        dialogLocalStateChange {
                            copy(tasksLocal = localState.tasksLocal.map { if (it.id == task.id) task else it })
                        }
                    },
                )
            }
        }

        CalendarDialogEditContentFooter(
            onSave = {
                viewModel.updateEntityAndTasks(
                    status = localState.savingStatus,
                    coroutineScope = loadingCoroutineScope,
                    type = type,
                    entity = localState.entityLocal,
                    newTasks = localState.tasksLocal,
                    sourceTasks = localState.tasksOrigin,
                    onStatusChange = { newStatus ->
                        dialogLocalStateChange{ copy(savingStatus = newStatus) }
                        if(newStatus == PlanCalendarLoadingStatus.DONE){
                            dialogStateChange(PlannerDialogType.CalendarDetails(entity = localState.entityLocal, type = type, mode = CalendarDialogMode.VIEW))
                        }
                    },
                )
            },
            onDelete = {
                viewModel.updateEntityAndTasks(
                    status = localState.deletingStatus,
                    coroutineScope = loadingCoroutineScope,
                    type = type,
                    entity = localState.entityLocal.copy(title = ""),
                    newTasks = emptyList(),
                    sourceTasks = localState.tasksOrigin,
                    onStatusChange = { newStatus ->
                        dialogLocalStateChange{ copy(deletingStatus = newStatus) }
                        if(newStatus == PlanCalendarLoadingStatus.DONE){
                            dialogLocalStateChange{ copy(entityLocal = PlanCalendarEntityDomain(date = localState.entityLocal.date), tasksLocal = emptyList()) }
                            dialogStateChange(PlannerDialogType.CalendarDetails(entity = localState.entityLocal, type = type, mode = CalendarDialogMode.VIEW))
                        }
                    },
                )
            },
        )
    }
}
