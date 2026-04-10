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
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.SsidChart
import androidx.compose.material.icons.filled.WebStories
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.planote.DarkColorScheme
import com.example.planote.MyAppFont
import com.example.planote.viewModel.plan.PlanWeekDayDomain
import com.example.planote.viewModel.plan.PlanWeekDayTaskDomain
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
            containerColor = if(isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(25.dp),
        modifier = Modifier
            .width(140.dp)
            .height(240.dp)
            .padding(10.dp)
            .shadowGlow(
                color = if(isToday) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent,
                offsetX = 0.dp,
                offsetY = 0.dp,
                blurRadius = 15.dp
            )
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(3.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(15.dp),
            ) {
            Text(
                text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).replace(".", ""),
                color = if(isToday) MaterialTheme.colorScheme.background.copy(0.5f) else MaterialTheme.colorScheme.onSurface.copy(0.5f),
                textAlign = TextAlign.Center,
            )
            Text(
                text = "${date.dayOfMonth}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = if(isToday) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                fontSize = 25.sp,
            )

            if(!dayTasks.isEmpty()){
                val listState = rememberLazyListState()
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxWidth().padding(start = 7.dp, end = 7.dp, bottom = 10.dp).weight(1f),
                ) {
                    items(dayTasks){ task ->
                        val curText = if(task.time != LocalTime.MIDNIGHT) task.time else ""
                        Text(
                            text = "$curText - ${task.title ?: ""}",
                            fontSize = 10.sp,
                            lineHeight = 15.sp,
                            color = if(isToday) MaterialTheme.colorScheme.background.copy(0.7f) else MaterialTheme.colorScheme.onSurface.copy(0.6f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Icon(
                    imageVector = icon,
                    contentDescription = "Иконка",
                    tint = if(isToday) MaterialTheme.colorScheme.background else if(isDay) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.size(27.dp)
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 30.dp).weight(1f)
                ){
                    Icon(
                        imageVector = Icons.Default.EventBusy,
                        contentDescription = "Нет задач",
                        tint = if(isToday) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onSurface.copy(0.3f),
                        modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = "Нет задач",
                        fontSize = 13.sp,
                        color = if(isToday) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onSurface.copy(0.3f),
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
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.background.copy(alpha = 0.6f)),
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp, bottom = 20.dp)
            .height(48.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.CalendarViewWeek,
                contentDescription = "ПОМЕНЯТЬ ПЛАН",
                modifier = Modifier.padding(end = 5.dp).size(18.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "ПОМЕНЯТЬ ПЛАН",
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

    val isWeek = dataState.weeks.find { it.isToggle }
    val weekDays = (0..6).map { LocalDate.now().with(DayOfWeek.MONDAY).plusDays(it.toLong()) }
    val todayIndex = weekDays.indexOfFirst { it == LocalDate.now() }

    val lazyListState = rememberLazyListState()
    LaunchedEffect(todayIndex) { lazyListState.animateScrollToItem(index = todayIndex) }

    var weekAlert by remember { mutableStateOf<WeekBlockAlert>(WeekBlockAlert.None) }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp).clip(RoundedCornerShape(20.dp)),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 25.dp)
        ) {
            Text(
                text = "НЕДЕЛЬНЫЙ ПЛАН",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 20.dp, bottom = 5.dp)
            )
            LazyRow(
                state = lazyListState,
                modifier = Modifier.padding(top = 5.dp, bottom = 15.dp),
            ) {
                itemsIndexed(weekDays) { index, date ->
                    val isToday = date == LocalDate.now()
                    val day = dataState.days.find{it.num == index} ?: PlanWeekDayDomain(num = index)
                    val isDay = dataState.days.contains(day)

                    val dayTasks by produceState(
                        initialValue = emptyList(),
                        key1 = index
                    ) {
                        viewModel.observeDayTasksForDayIndex(index).collect { tasks -> value = tasks }
                    }

                    WeekBlockCard(
                        date = date,
                        isToday = isToday,
                        isDay = isDay,
                        day = day,
                        dayTasks = dayTasks,
                        onDayClick = { clickedDay ->
                            if(isWeek != null){
                                viewModel.loadDayAndTasks(clickedDay)
                                if(isDay) dialogStateChange(PlanWeekDialogMode.DAYVIEW) else dialogStateChange(PlanWeekDialogMode.DAYEDIT)
                            } else {
                                weekAlert = WeekBlockAlert.NoPlan
                            }
                        }
                    )
                }
            }
            WeekBlockButton(
                onClick = {
                    viewModel.loadWeeks()
                    dialogStateChange(PlanWeekDialogMode.PLANCHANGE)
                }
            )
        }
    }
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
        Card(
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .width(300.dp)
                .border(width = 1.dp, shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.17f))
        ) {
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
) {
    val weekDays = (0..6).map { LocalDate.now().with(DayOfWeek.MONDAY).plusDays(it.toLong()) }
    val lazyListState = rememberLazyListState()
    val todayIndex = weekDays.indexOfFirst { it == LocalDate.now() }
    LaunchedEffect(todayIndex) { lazyListState.animateScrollToItem(index = todayIndex) }
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = MyAppFont,
    ) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp).clip(RoundedCornerShape(20.dp)),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 25.dp)
            ) {
                Text(
                    text = "НЕДЕЛЬНЫЙ ПЛАН",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 20.dp, bottom = 5.dp)
                )
                LazyRow(
                    state = lazyListState,
                    modifier = Modifier.padding(top = 5.dp, bottom = 15.dp),
                ) {
                    itemsIndexed(weekDays) { index, date ->
                        WeekBlockCard(
                            date = date,
                            isToday = date == LocalDate.now(),
                            isDay = true,
                            day = PlanWeekDayDomain(num = index, title = "день"),
                            dayTasks = emptyList(),
//                                listOf(
//                                PlanWeekDayTaskDomain(id = 1, title = "Задача 1", description = "Основной план", time = LocalTime.NOON),
//                                PlanWeekDayTaskDomain(id = 5, title = "Задача 1", description = "Основной план"),
//                                PlanWeekDayTaskDomain(id = 4, title = "Задача 1", time = LocalTime.NOON),
//                                PlanWeekDayTaskDomain(id = 2, title = "Задача 2", description = "Запасной варианfffdsfsfsdfdsfsdfdsтdsadsadsadsadad", time = LocalTime.NOON),
//                                PlanWeekDayTaskDomain(id = 3, title = "Задача 3", description = ""),
//                                PlanWeekDayTaskDomain(id = 7, title = "Задача 3", description = ""),
//                                PlanWeekDayTaskDomain(id = 8, title = "Задача 3", description = "")),
                            onDayClick = {}
                        )
                    }
                }
                WeekBlockButton(
                    onClick = {
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun WeekAlertPreview(){
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = MyAppFont,
    ) {
        WeekAlert(
            title = "Удалить задачу?",
            description = "Это действие нельзя отменить",
            onConfirm = { },
            onDismiss = {  }
        )
    }
}