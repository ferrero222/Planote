/*****************************************************************
 *  Package for main screen with circular pager
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.view.plan.component.calendar

/*****************************************************************
 * Imported packages
 ****************************************************************/
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.planote.view.plan.CalendarDialogMode
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
private fun CalendarDialogContentHeader(entity: PlanCalendarEntityDomain, type: PlanCalendarType, mode: CalendarDialogMode, onDismissClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = { onDismissClick() },
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 10.dp)
                .size(32.dp)
        ) {
            Icon(
                imageVector = if (mode == CalendarDialogMode.VIEW) Icons.Filled.Close else Icons.Filled.ArrowBackIosNew,
                contentDescription = if (mode == CalendarDialogMode.VIEW) "Закрыть" else "Назад",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
        }

        val month = entity.date.month.getDisplayName(TextStyle.FULL, Locale.getDefault()).replaceFirstChar { it.uppercaseChar() } // Ваш формат с заглавной буквы

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
                    color = if (mode == CalendarDialogMode.VIEW) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f) else MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(15.dp)
                )
                .padding(horizontal = 12.dp, vertical = 5.dp)
                .shadowGlow(
                    color = if(mode == CalendarDialogMode.VIEW) MaterialTheme.colorScheme.primary.copy(alpha = 0.0f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    offsetX = 0.dp,
                    offsetY = 0.dp,
                    blurRadius = 20.dp)

        ) {
            Text(
                text = if (mode == CalendarDialogMode.VIEW) "VIEW" else "EDIT",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (mode == CalendarDialogMode.VIEW) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background
            )
        }
    }
}

@Composable
private fun CalendarDialogContentDescription(entity: PlanCalendarEntityDomain, mode: CalendarDialogMode, onTitleChange: (String) -> Unit){
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) { //Description
        Text(
            text = "ОПИСАНИЕ",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
        )
        if (mode == CalendarDialogMode.VIEW) {
            Text(
                text = entity.title ?:  "Нет данных" ,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp)
            )
        } else {
            OutlinedTextField(
                value = entity.title ?: "",
                placeholder = { Text(text = "Нет данных", color = MaterialTheme.colorScheme.onSurface) },
                onValueChange = onTitleChange,
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )
        }
    }
}

@Composable
private fun CalendarDialogContentTasks(tasks: List<PlanCalendarTaskDomain>, mode: CalendarDialogMode, onTaskUpdate: (PlanCalendarTaskDomain) -> Unit){
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

                        if (mode == CalendarDialogMode.EDIT) {
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
        } else {
            Text("Нет данных", color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp, modifier = Modifier.padding(top = 4.dp))
        }
        if (mode == CalendarDialogMode.EDIT) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
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
}

@Composable
private fun CalendarDialogContentFooter(mode: CalendarDialogMode, onEdit: () -> Unit, onSave: () -> Unit, onDelete: () -> Unit
){
    if (mode == CalendarDialogMode.VIEW) { //Footer
        Button(
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colorScheme.background,
                containerColor = MaterialTheme.colorScheme.primary
            ),
            contentPadding = PaddingValues(vertical = 15.dp),
            onClick = { onEdit() },
            modifier = Modifier
                .fillMaxWidth()
                .shadowGlow(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), offsetX = 0.dp, offsetY = 0.dp, blurRadius = 15.dp)
        ) {
            Text(text = "РЕДАКТИРОВАТЬ", fontWeight = FontWeight.Bold)
        }
    } else {
        Column {
            Button(
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.background,
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                contentPadding = PaddingValues(vertical = 15.dp),
                onClick = { onSave() },
                modifier = Modifier.fillMaxWidth().shadowGlow(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), offsetX = 0.dp, offsetY = 0.dp, blurRadius = 20.dp)

            ) {
                Text(text = "СОХРАНИТЬ", fontWeight = FontWeight.Bold)
            }
            TextButton(
                shape = RoundedCornerShape(10.dp),
                onClick = { onDelete() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .border(width = 1.dp,
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
            ) {
                Text(
                    text = "ОЧИСТИТЬ ВСЁ",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.shadowGlow(color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f), offsetX = 0.dp, offsetY = 0.dp, blurRadius = 20.dp)
                )
            }
        }
    }
}

@Composable
private fun CalendarLoading(status: PlanCalendarLoadingStatus, onNextContent: @Composable () -> Unit){
    when(status){
        PlanCalendarLoadingStatus.IDLE,  PlanCalendarLoadingStatus.PROC -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        PlanCalendarLoadingStatus.DONE -> onNextContent()
    }
}

@Composable
private fun CalendarDialogContent(
    viewModel: PlanCalendarViewModel = hiltViewModel(),
    entity: PlanCalendarEntityDomain,
    type: PlanCalendarType,
    mode: CalendarDialogMode,
    dialogStateChange: (PlannerDialogType) -> Unit
) {
    var localEntity by remember(entity.date) { mutableStateOf(entity) }
    var localTasks: List<PlanCalendarTaskDomain> by remember(entity) { mutableStateOf(emptyList()) }

    var getDataStatus by remember(entity.date) { mutableStateOf(PlanCalendarLoadingStatus.IDLE) }
    var saveDataStatus by remember(entity.date) { mutableStateOf(PlanCalendarLoadingStatus.IDLE) }
    var deleteDataStatus by remember(entity.date) { mutableStateOf(PlanCalendarLoadingStatus.IDLE) }

    val loadingCoroutineScope = rememberCoroutineScope()

    viewModel.getEntityTasks(
        status = getDataStatus,
        coroutineScope = loadingCoroutineScope,
        type = type,
        entity = localEntity,
        onGetTasks = { newTasks -> localTasks = newTasks },
        onStatusChange = { newStatus -> getDataStatus = newStatus },
    )

    CalendarLoading(getDataStatus) {
        Column(
            modifier = Modifier.fillMaxSize().padding(vertical = 15.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            CalendarDialogContentHeader(
                entity = entity,
                type = type,
                mode = mode,
                onDismissClick = {
                    when (mode) {
                        CalendarDialogMode.VIEW -> dialogStateChange(PlannerDialogType.None)
                        CalendarDialogMode.EDIT -> dialogStateChange(PlannerDialogType.CalendarDetails(entity = localEntity, type = type, mode = CalendarDialogMode.VIEW))
                    }
                }
            )

            Box(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    CalendarDialogContentDescription(
                        entity = localEntity,
                        mode = mode,
                        onTitleChange = { newTitle -> localEntity = localEntity.copy(title = newTitle) }
                    )
                    CalendarDialogContentTasks(
                        tasks = localTasks,
                        mode = mode,
                        onTaskUpdate = { task: PlanCalendarTaskDomain ->
                            localTasks = localTasks.map { if (it.id == task.id) task else it }
                        },
                    )
                }
            }

            CalendarDialogContentFooter(
                mode = mode,
                onSave = {
                    viewModel.updateEntityAndTasks(
                        status = saveDataStatus,
                        coroutineScope = loadingCoroutineScope,
                        type = type,
                        entity = localEntity,
                        newTasks = localTasks,
                        sourceTasks = localTasks,
                        onEntityId = { newId -> localEntity = localEntity.copy(id = newId) },
                        onStatusChange = { newStatus ->
                            saveDataStatus = newStatus
                            if(saveDataStatus == PlanCalendarLoadingStatus.DONE)
                                dialogStateChange(PlannerDialogType.CalendarDetails(entity = localEntity, type = type, mode = CalendarDialogMode.VIEW))
                        },
                    )
                },
                onDelete = {
                    viewModel.updateEntity(
                        status = deleteDataStatus,
                        coroutineScope = loadingCoroutineScope,
                        type = type,
                        entity = localEntity.copy(title = ""),
                        tasks = emptyList(),
                        onEntityId = { },
                        onStatusChange = { newStatus ->
                            deleteDataStatus = newStatus
                            if(deleteDataStatus == PlanCalendarLoadingStatus.DONE)
                                dialogStateChange(PlannerDialogType.CalendarDetails(entity = PlanCalendarEntityDomain(date = entity.date), type = type, mode = CalendarDialogMode.VIEW))
                        },
                    )
                },
                onEdit = {
                    saveDataStatus = PlanCalendarLoadingStatus.IDLE
                    deleteDataStatus = PlanCalendarLoadingStatus.IDLE
                    dialogStateChange(PlannerDialogType.CalendarDetails(entity = localEntity, type = type, mode = CalendarDialogMode.EDIT))
                }
            )
        }
    }
}

/*****************************************************************
 * Public functions
 ****************************************************************/
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CalendarDialog(entity: PlanCalendarEntityDomain, type: PlanCalendarType, mode: CalendarDialogMode, dialogStateChange: (PlannerDialogType) -> Unit) {
    Dialog(onDismissRequest = { dialogStateChange(PlannerDialogType.None) }) {
        Card(
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .requiredWidth(340.dp)
                .requiredHeight(650.dp)
        ) {
            AnimatedContent(
                targetState = mode,
                label = "Dialog mode transition",
                transitionSpec = {
                    fadeIn(
                        animationSpec = tween(220, easing = LinearEasing)
                    ) togetherWith fadeOut(
                        animationSpec = tween(180, easing = LinearEasing)
                    )
                },
            ) {
                currentMode -> CalendarDialogContent(entity = entity, type = type, mode = currentMode, dialogStateChange = dialogStateChange)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCalendarDialogContentHeader(){
    val typetemp = CalendarDialogMode.EDIT
    Column(
        modifier = Modifier.fillMaxSize().padding(vertical = 15.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        CalendarDialogContentHeader(
            entity = PlanCalendarEntityDomain(),
            type = PlanCalendarType.DAYS,
            mode = typetemp,
            onDismissClick = {}
        )

        Box(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                CalendarDialogContentDescription(
                    entity = PlanCalendarEntityDomain(),
                    mode = typetemp,
                    onTitleChange = { }
                )
                CalendarDialogContentTasks(
                    tasks = emptyList<PlanCalendarTaskDomain>(),
                    mode = typetemp,
                    onTaskUpdate = { task: PlanCalendarTaskDomain -> },
                )
            }
        }

        CalendarDialogContentFooter(
            mode = typetemp,
            onSave = {},
            onDelete = {},
            onEdit = {}
        )
    }
}

