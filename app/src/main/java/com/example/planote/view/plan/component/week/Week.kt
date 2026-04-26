/*****************************************************************
 *  Package for server view
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.view.plan.component.week

/*****************************************************************
 * Imported packages
 ****************************************************************/
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.CalendarViewWeek
import androidx.compose.material.icons.filled.ChangeHistory
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SsidChart
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WebStories
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.planote.PreviewContainer
import com.example.planote.view.plan.PlannerBlockCard
import com.example.planote.viewModel.plan.PlanWeekDataHolder
import com.example.planote.viewModel.plan.PlanWeekDayDomain
import com.example.planote.viewModel.plan.PlanWeekDayTaskDomain
import com.example.planote.viewModel.plan.PlanWeekDialogMode
import com.example.planote.viewModel.plan.PlanWeekDomain
import com.example.planote.viewModel.plan.PlanWeekLoading
import com.example.planote.viewModel.plan.PlanWeekViewModel
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.Locale

/*****************************************************************
 * Variables, data, enum
 ****************************************************************/
sealed class WeekBlockAlert {
    object None : WeekBlockAlert()
    object NoPlan : WeekBlockAlert()
}

/*****************************************************************
 * Interfaces
 ****************************************************************/
/*****************************************************************
 * Private functions
 ****************************************************************/
private fun getWeekDayIcon(dayOfWeek: Int) = when (dayOfWeek) {
    1 -> Icons.Default.BarChart           //Mon
    2 -> Icons.Default.BlurOn             //Tue
    3 -> Icons.Default.FormatAlignCenter  //Wen
    4 -> Icons.Default.ChangeHistory      //Thu
    5 -> Icons.Default.SsidChart          //Fri
    6 -> Icons.Default.WebStories         //Sat
    7 -> Icons.Default.GraphicEq          //Sun
    else -> Icons.Default.CalendarViewWeek
}

@Composable
private fun WeekBlock(
    dataState: PlanWeekDataHolder,
    onDayClick: (PlanWeekDayDomain) -> Unit,
    onChangePlanClick: () -> Unit,
    observeDayTasks: (Int) -> Flow<List<PlanWeekDayTaskDomain>>,
) {
    val weekDays = (0..6).map { LocalDate.now().with(DayOfWeek.MONDAY).plusDays(it.toLong()) }
    val todayIndex = weekDays.indexOfFirst { it == LocalDate.now() }
    val lazyListState = rememberLazyListState()
    LaunchedEffect(todayIndex) { lazyListState.animateScrollToItem(index = todayIndex) }

    PlannerBlockCard {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier.align(Alignment.CenterVertically).size(17.dp).weight(1f)
                ){
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(7.dp)
                    ){
                        Text(
                            text = ">>   КАЛЕНДАРЬ_ЗАДАЧ",
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 3.dp)
                        )
                        IconButton(
                            onClick = {},
                            modifier = Modifier.size(15.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = "информация",
                                tint = MaterialTheme.colorScheme.primary.copy(0.5f),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                modifier = Modifier.padding(top = 7.dp)
            )
            LazyRow(
                state = lazyListState,
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                modifier = Modifier.padding(vertical = 10.dp),
            ) {
                itemsIndexed(weekDays, key = { _, date -> date }) { index, date ->
                    val isToday = date == LocalDate.now()
                    val day = dataState.days.find { it.num == index } ?: PlanWeekDayDomain(num = index)
                    val isDay = dataState.days.contains(day)
                    val dayTasks = observeDayTasks(index).collectAsStateWithLifecycle(initialValue = emptyList())

                    WeekBlockCard(
                        date = date,
                        isToday = isToday,
                        isDay = isDay,
                        day = day,
                        dayTasks = dayTasks.value,
                        onDayClick = onDayClick
                    )
                }
            }
            WeekBlockButton(
                onClick = onChangePlanClick
            )
        }
    }
}

@Composable
private fun WeekBlockCard(
    date: LocalDate,
    isToday: Boolean,
    isDay: Boolean,
    day: PlanWeekDayDomain,
    dayTasks: List<PlanWeekDayTaskDomain>,
    onDayClick: (PlanWeekDayDomain) -> Unit
) {
    val icon = getWeekDayIcon(date.dayOfWeek.value)

    Card(
        onClick = { onDayClick(day) },
        colors = CardDefaults.cardColors(
            containerColor = if(isToday) MaterialTheme.colorScheme.primary.copy(0.1f) else MaterialTheme.colorScheme.background.copy(alpha = 0.4f)
        ),
        shape = RectangleShape,
        modifier = Modifier
            .width(170.dp)
            .height(225.dp)
            .padding(vertical = 10.dp)
            .border(
                width = if(isToday) 1.2.dp else 1.dp,
                color = if(isToday) MaterialTheme.colorScheme.primary else  MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            ),
        ) {
        Column(
                verticalArrangement = Arrangement.spacedBy(7.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize().padding(15.dp),
            ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase().replace(".", ""),
                    color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(0.6f),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Text(
                    text = "${date.dayOfMonth}.${date.month.value}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(0.9f),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }

            HorizontalDivider(
                thickness = 1.2.dp,
                color = if(isToday) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            )

            if(!dayTasks.isEmpty()){
                val listState = rememberLazyListState()
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp).weight(1f),
                ) {
                    items(dayTasks){ task ->
                        Row {
                            Text(
                                text = task.title ?: "",
                                fontSize = 12.sp,
                                lineHeight = 15.sp,
                                color = if(isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(0.6f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.weight(1f)
                            )
                            if(task.time != LocalTime.MIDNIGHT){
                                Text(
                                    text = " / ",
                                    fontSize = 12.sp,
                                    lineHeight = 15.sp,
                                    color = if(isToday) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface.copy(0.4f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.width(12.dp)
                                )
                                Text(
                                    text = "${task.time}",
                                    fontSize = 12.sp,
                                    lineHeight = 15.sp,
                                    color = if(isToday) MaterialTheme.colorScheme.primary.copy(0.7f) else MaterialTheme.colorScheme.onSurface.copy(0.4f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.width(40.dp)
                                )

                            }
                        }
                    }
                }
                Icon(
                    imageVector = icon,
                    contentDescription = "Иконка",
                    tint = if(isToday) MaterialTheme.colorScheme.primary else if(isDay) MaterialTheme.colorScheme.onSurface.copy(0.5f) else Color.Transparent,
                    modifier = Modifier.size(25.dp)
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize().padding(top = 30.dp)
                ){
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Нет задач",
                        tint = if(isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(0.5f),
                        modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = "ERR_НЕТ_ДАННЫХ",
                        fontSize = 12.sp,
                        color = if(isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(0.3f),
                        maxLines = 1,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }
            }


        }
    }
}

@Composable
private fun WeekBlockButton(
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ),
        border = BorderStroke(
            width = 1.dp,
            brush = SolidColor(MaterialTheme.colorScheme.primary.copy(0.2f))
        ),
        shape = RectangleShape,
        modifier = Modifier.fillMaxWidth().height(45.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.SwapHoriz,
                contentDescription = "ПОМЕНЯТЬ ПЛАН",
                modifier = Modifier.padding(end = 5.dp).size(22.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "ПОМЕНЯТЬ_ПЛАН",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun WeekBlockAlertHandler(
    weekAlert: WeekBlockAlert,
    viewModel: PlanWeekViewModel,
    dialogStateChange: (PlanWeekDialogMode) -> Unit,
    onDismiss: () -> Unit
) {
    when(weekAlert) {
        is WeekBlockAlert.NoPlan -> {
            WeekAlert(
                title = "Информация",
                description = "Сначала нужно создать план перед тем как редактировать дни",
                confirmText = "Создать",
                dismissText = "Отмена",
                onConfirm = {
                    onDismiss()
                    viewModel.loadWeeks()
                    dialogStateChange(PlanWeekDialogMode.PLANCHANGE)
                },
                onDismiss = {
                    onDismiss()
                }
            )
        }
        is WeekBlockAlert.None -> { }
    }
}

/*****************************************************************
 * Public functions
 ****************************************************************/
@Composable
fun WeekBlock(
    viewModel: PlanWeekViewModel = hiltViewModel(),
    dialogStateChange: (PlanWeekDialogMode) -> Unit
) {
    val dataState by viewModel.dataState.collectAsStateWithLifecycle()
    var weekAlert by remember { mutableStateOf<WeekBlockAlert>(WeekBlockAlert.None) }

    WeekBlock(
        dataState = dataState,
        onDayClick = { clickedDay ->
            val isWeek = dataState.weeks.find { it.isToggle }
            val isDay = dataState.days.contains(clickedDay)
            if (isWeek != null) {
                viewModel.loadDayAndTasks(clickedDay)
                dialogStateChange(if (isDay) PlanWeekDialogMode.DAYVIEW else PlanWeekDialogMode.DAYEDIT)
            } else {
                weekAlert = WeekBlockAlert.NoPlan
            }
        },
        onChangePlanClick = {
            viewModel.loadWeeks()
            dialogStateChange(PlanWeekDialogMode.PLANCHANGE)
        },
        observeDayTasks = { index -> viewModel.observeDayTasksForDayIndex(index) }
    )

    WeekBlockAlertHandler(
        weekAlert = weekAlert,
        viewModel = viewModel,
        dialogStateChange = dialogStateChange,
        onDismiss = { weekAlert = WeekBlockAlert.None }
    )
}

@Composable
fun WeekAlert(
    title: String = "Предупреждение",
    description: String? = "Несохранённые изменения будут потеряны",
    content: @Composable (() -> Unit)? = null,
    confirmText: String = "Подтвердить",
    dismissText: String = "Отменить",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        PlannerBlockCard {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = if (description != null || content != null) 12.dp else 0.dp)
                )

                if (description != null) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = if (content != null) 12.dp else 16.dp)
                    )
                }

                if (content != null) {
                    content()
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.height(44.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = dismissText,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = confirmText,
                            color = MaterialTheme.colorScheme.background,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WeekLoading(
    status: PlanWeekLoading,
    onContent: @Composable () -> Unit
) {
    val isLoading = when (status) {
        is PlanWeekLoading.Loading,
        is PlanWeekLoading.Saving,
        is PlanWeekLoading.Deleting -> true
        else -> false
    }
    AnimatedContent(
        targetState = isLoading,
        label = "Loading transition",
        transitionSpec = {
            fadeIn(animationSpec = tween(600)) togetherWith
                    fadeOut(animationSpec = tween(600))
        }
    ) { targetIsLoading ->
        if (targetIsLoading) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = 50.dp).height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(35.dp)
                )
            }
        } else {
            onContent()
        }
    }
}

/*****************************************************************
 * Preview
 ****************************************************************/
@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun WeekBlockPreview(
    dataState: PlanWeekDataHolder = PlanWeekDataHolder(
        days = listOf(
            PlanWeekDayDomain(num = 0, title = "Понедельник"),
            PlanWeekDayDomain(num = 1, title = "Вторник"),
            PlanWeekDayDomain(num = 2, title = "Среда"),
            PlanWeekDayDomain(num = 3, title = "Четверг"),
            PlanWeekDayDomain(num = 4, title = "Пятница"),
            PlanWeekDayDomain(num = 5, title = "Суббота"),
            PlanWeekDayDomain(num = 6, title = "Воскресенье")
        ),
        weeks = listOf(PlanWeekDomain(isToggle = true))
    )
) {
    val sampleTasks1 = listOf(
        PlanWeekDayTaskDomain(id = 1, title = "Позавтракать", time = LocalTime.of(9, 0), isDone = true),
        PlanWeekDayTaskDomain(id = 2, title = "Встреча с командой", time = LocalTime.of(14, 0), isDone = false),
        PlanWeekDayTaskDomain(id = 3, title = "Спортзал", time = LocalTime.MIDNIGHT, isDone = false)
    )
    val sampleTasks2 = listOf(
        PlanWeekDayTaskDomain(id = 4, title = "Доработать проект", time = LocalTime.of(10, 0), isDone = false),
        PlanWeekDayTaskDomain(id = 5, title = "Созвониться с клиентом", time = LocalTime.of(15, 30), isDone = false),
        PlanWeekDayTaskDomain(id = 6, title = "Созвониться с клиентом", time = LocalTime.MIDNIGHT, isDone = false)
    )
    val sampleTasksDefault = emptyList<PlanWeekDayTaskDomain>()
    PreviewContainer {
        WeekBlock(
            dataState = dataState,
            onDayClick = {},
            onChangePlanClick = {},
            observeDayTasks = { dayIndex ->
                kotlinx.coroutines.flow.flowOf(
                    when (dayIndex) {
                        1 -> sampleTasks1
                        2 -> sampleTasks2
                        else -> sampleTasksDefault
                    }
                )
            }
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun WeekAlertPreview(){
    PreviewContainer {
        WeekAlert(
            title = "Удалить задачу?",
            description = "Это действие нельзя отменить",
            onConfirm = { },
            onDismiss = { }
        )
    }
}