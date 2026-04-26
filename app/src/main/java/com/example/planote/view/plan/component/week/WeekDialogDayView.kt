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
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
/*****************************************************************
 * Interfaces
 ****************************************************************/
/*****************************************************************
 * Private functions
 ****************************************************************/
@Composable
private fun WeekDialogDayViewContent(
    dialogState: PlanWeekDialogDayDataHolder,
    loading: PlanWeekLoading,
    onDismissClick: () -> Unit,
    onEdit: () -> Unit,
) {
    WeekLoading(loading) {
        Column(
            verticalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier.fillMaxSize().padding(25.dp),
        ) {
            WeekDialogDayViewContentHeader(
                day = dialogState.day,
                onDismissClick = onDismissClick,
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(15.dp),
                modifier = Modifier.weight(1f).fillMaxWidth(),
            ) {
                WeekDialogDayViewContentDescription(day = dialogState.day)
                WeekDialogDayViewContentTasks(tasks = dialogState.tasks)
            }
            WeekDialogDayViewContentFooter(
                onEdit = onEdit,
            )
        }
    }
}

@Composable
private fun WeekDialogDayViewContentHeader(
    day: PlanWeekDayDomain,
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
                    shape = RoundedCornerShape(15.dp)
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
private fun WeekDialogDayViewContentDescription(
    day: PlanWeekDayDomain
){
    if(day.title.isNullOrEmpty()) return
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
                text = day.title,
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
private fun WeekDialogDayViewContentTasksItem(
    task: PlanWeekDayTaskDomain
){
    val hasDescription = !task.description.isNullOrBlank()
    var expandedTaskState by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), shape = RoundedCornerShape(5.dp))
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = hasDescription) { expandedTaskState = !expandedTaskState }
                .padding(10.dp)
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
                ){
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = task.time.format(DateTimeFormatter.ofPattern("HH:mm")),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            }
            if(hasDescription) {
                Icon(
                    imageVector = if (expandedTaskState) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expandedTaskState) "Свернуть" else "Развернуть",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }
            else{
                Spacer(modifier = Modifier.size(20.dp))
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
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
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
private fun WeekDialogDayViewContentTasks(
    tasks: List<PlanWeekDayTaskDomain>
){
    if(tasks.isEmpty()) return
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
                    WeekDialogDayViewContentTasksItem(task = task)
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
private fun WeekDialogDayViewContentFooter(
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
fun WeekDialogDayViewContent(
    viewModel: PlanWeekViewModel = hiltViewModel(),
    dialogStateChange: (PlanWeekDialogMode) -> Unit
) {
    val dialogState by viewModel.dialogDayState.collectAsStateWithLifecycle()

    WeekDialogDayViewContent(
        dialogState = dialogState,
        loading = dialogState.loading,
        onDismissClick = { dialogStateChange(PlanWeekDialogMode.IDLE) },
        onEdit = { dialogStateChange(PlanWeekDialogMode.DAYEDIT) },
    )
}

/*****************************************************************
 * Preview functions
 ****************************************************************/
@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun WeekDialogDayViewContentPreview(
    day: PlanWeekDayDomain = PlanWeekDayDomain(
        id = 1,
        num = 0,
        title = "План на понедельник"
    ),
    tasks: List<PlanWeekDayTaskDomain> = listOf(
        PlanWeekDayTaskDomain(id = 1, title = "Задача 1", description = "Основной план", time = LocalTime.NOON),
        PlanWeekDayTaskDomain(id = 2, title = "Задача 2", description = "Запасной вариант", isDone = false),
        PlanWeekDayTaskDomain(id = 3, title = "Задача 3", description = "")
    ),
    loading: PlanWeekLoading = PlanWeekLoading.Idle
) {
    PreviewContainer{
        WeekDialogCard{
            WeekDialogDayViewContent(
                dialogState = PlanWeekDialogDayDataHolder(day = day, tasks = tasks),
                loading = loading,
                onDismissClick = {},
                onEdit = {},
            )
        }
    }
}
