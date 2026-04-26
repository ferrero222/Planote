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
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.example.planote.PreviewContainer
import com.example.planote.viewModel.plan.PlanCalendarDialogDataHolder
import com.example.planote.viewModel.plan.PlanCalendarDialogMode
import com.example.planote.viewModel.plan.PlanCalendarEntityDomain
import com.example.planote.viewModel.plan.PlanCalendarLoading
import com.example.planote.viewModel.plan.PlanCalendarTaskDomain
import com.example.planote.viewModel.plan.PlanCalendarType
import com.example.planote.viewModel.plan.PlanCalendarViewModel
import me.trishiraj.shadowglow.shadowGlow
import java.time.LocalDate
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
private fun CalendarDialogViewContent(
    dialogState: PlanCalendarDialogDataHolder,
    type: PlanCalendarType,
    loading: PlanCalendarLoading,
    onDismissClick: () -> Unit,
    onEdit: () -> Unit
) {
    CalendarLoading(loading) {
        Column(
            verticalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier.fillMaxSize().padding(25.dp),
        ) {
            CalendarDialogViewContentHeader(
                entity = dialogState.entity,
                type = type,
                onDismissClick = onDismissClick,
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(15.dp),
                modifier = Modifier.weight(1f).fillMaxWidth(),
            ) {
                CalendarDialogViewContentDescription(entity = dialogState.entity)
                CalendarDialogViewContentTasks(tasks = dialogState.tasks)
            }
            CalendarDialogViewContentFooter(
                onEdit = onEdit,
            )
        }
    }
}

@Composable
private fun CalendarDialogViewContentHeader(
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
            modifier = Modifier.align(Alignment.CenterStart).size(20.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Закрыть",
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
                text = "VIEW",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
        }
    }
}

@Composable
private fun CalendarDialogViewContentDescription(
    entity: PlanCalendarEntityDomain
){
    if(entity.title.isNullOrEmpty()) return
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "ОПИСАНИЕ",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), shape = RoundedCornerShape(5.dp))
        ){
            Text(
                text = entity.title,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.verticalScroll(rememberScrollState()).padding(10.dp)
            )
        }
    }
}

@Composable
private fun CalendarDialogViewContentTasksItem(
    task: PlanCalendarTaskDomain
){
    val hasDescription = !task.description.isNullOrBlank()
    var expandedTaskState by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), shape = RoundedCornerShape(5.dp))
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = hasDescription) { expandedTaskState = !expandedTaskState }
                .padding(10.dp)
        ) {
            Text(
                text = task.title ?: "Нет описания",
                color = if (task.isDone) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (hasDescription) {
                Icon(
                    imageVector = if (expandedTaskState) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expandedTaskState) "Свернуть" else "Развернуть",
                    tint = if(task.isDone) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }
            Box(
                modifier = Modifier.size(20.dp)
            ) {
                Checkbox(
                    checked = task.isDone,
                    onCheckedChange = {},
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        uncheckedColor = MaterialTheme.colorScheme.onSurface,
                        checkmarkColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        }
        AnimatedVisibility(
            visible = expandedTaskState && hasDescription,
            enter = expandVertically(
                expandFrom = Alignment.Top,
                animationSpec = tween(durationMillis = 300)
            ),
            exit = shrinkVertically(
                shrinkTowards = Alignment.Top,
                animationSpec = tween(durationMillis = 300)
            )
        ) {
            Text(
                text = task.description ?: "Нет",
                color = if (task.isDone) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                fontSize = 13.sp,
                maxLines = 6,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
            )
        }
    }
}

@Composable
private fun CalendarDialogViewContentTasks(
    tasks: List<PlanCalendarTaskDomain>
){
    if(tasks.isEmpty()) return
    val listState = rememberLazyListState()
    Column {
        Box(modifier = Modifier.fillMaxWidth()){
            Text(
                text = "ЗАДАЧИ",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Text(
                text = "${tasks.size} АКТИВНЫХ",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
        Box {
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(tasks, key = { it.id }) { task ->
                    CalendarDialogViewContentTasksItem(task = task)
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
    }
}

@Composable
private fun CalendarDialogViewContentFooter(
    onEdit: () -> Unit
){
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
            .shadowGlow(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                offsetX = 0.dp,
                offsetY = 0.dp,
                blurRadius = 17.dp
            )
    ) {
        Text(text = "РЕДАКТИРОВАТЬ", fontWeight = FontWeight.Bold)
    }
}

/*****************************************************************
 * Public functions
 ****************************************************************/
@Composable
fun CalendarDialogViewContent(
    viewModel: PlanCalendarViewModel = hiltViewModel(),
    dialogStateChange: (PlanCalendarDialogMode) -> Unit
) {
    val dialogState by viewModel.dialogState.collectAsStateWithLifecycle()
    val dataType by viewModel.dataState.collectAsStateWithLifecycle()

    CalendarDialogViewContent(
        dialogState = dialogState,
        type = dataType.type,
        loading = dialogState.loading,
        onDismissClick = { dialogStateChange(PlanCalendarDialogMode.IDLE) },
        onEdit = { dialogStateChange(PlanCalendarDialogMode.EDIT) },
    )
}

/*****************************************************************
 * Preview functions
 ****************************************************************/
@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun CalendarDialogViewContentPreview(
    entity: PlanCalendarEntityDomain = PlanCalendarEntityDomain(
        id = 1,
        title = "План на день",
        date = LocalDate.now()
    ),
    tasks: List<PlanCalendarTaskDomain> = listOf(
        PlanCalendarTaskDomain(id = 1, title = "Задача 1", description = "Основной план", isDone = true),
        PlanCalendarTaskDomain(id = 2, title = "Задача 2", description = "Запасной вариант", isDone = false),
        PlanCalendarTaskDomain(id = 3, title = "Задача 3", description = "", isDone = false)
    ),
    type: PlanCalendarType = PlanCalendarType.DAYS,
    loading: PlanCalendarLoading = PlanCalendarLoading.Idle
) {
    PreviewContainer {
        CalendarDialogCard {
            CalendarDialogViewContent(
                dialogState = PlanCalendarDialogDataHolder(entity = entity, tasks = tasks),
                type = type,
                loading = loading,
                onDismissClick = {},
                onEdit = {},
            )
        }
    }
}

