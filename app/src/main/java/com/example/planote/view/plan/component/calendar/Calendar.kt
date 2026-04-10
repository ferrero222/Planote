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
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarViewDay
import androidx.compose.material.icons.filled.CalendarViewMonth
import androidx.compose.material.icons.filled.CalendarViewWeek
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.planote.DarkColorScheme
import com.example.planote.MyAppFont
import com.example.planote.viewModel.plan.PlanCalendarDialogMode
import com.example.planote.viewModel.plan.PlanCalendarEntityDomain
import com.example.planote.viewModel.plan.PlanCalendarLoading
import com.example.planote.viewModel.plan.PlanCalendarType
import com.example.planote.viewModel.plan.PlanCalendarViewModel
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.daysOfWeek
import me.trishiraj.shadowglow.shadowGlow
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
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
private fun CalendarBlockHeader(
    currentViewType: PlanCalendarType,
    onViewTypeChanged: (PlanCalendarType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background.copy(alpha = 0.6f))
    ) {
        CalendarTypeSelector(currentViewType = currentViewType, onViewTypeChanged = { newType -> onViewTypeChanged(newType) })
    }
}

@Composable
private fun CalendarBlockContent(
    viewModel: PlanCalendarViewModel,
    viewType: PlanCalendarType,
    dialogStateChange: (PlanCalendarDialogMode) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(top = 15.dp)
            .fillMaxWidth()
            .requiredHeight(400.dp)
    ) {
        AnimatedContent(
            targetState = viewType, label = "CalendarSwitchAnimation", transitionSpec = {
                if (targetState.ordinal > initialState.ordinal) (slideInHorizontally { it } + fadeIn()).togetherWith(slideOutHorizontally { -it } + fadeOut())
                else (slideInHorizontally { -it } + fadeIn()).togetherWith(slideOutHorizontally { it } + fadeOut())
            }
        ) { targetViewType -> when (targetViewType) {
                PlanCalendarType.DAYS   -> DaysCalendar(viewModel, dialogStateChange)
                PlanCalendarType.MONTHS -> MonthsCalendar(viewModel, dialogStateChange)
                PlanCalendarType.YEARS  -> YearsCalendar(viewModel, dialogStateChange)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarTypeSelector(
    currentViewType: PlanCalendarType,
    onViewTypeChanged: (PlanCalendarType) -> Unit,
    modifier: Modifier = Modifier
) {
    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        PlanCalendarType.entries.forEach { viewType ->
            SegmentedButton(
                shape = RectangleShape,
                onClick = { onViewTypeChanged(viewType) },
                selected = currentViewType == viewType,
                colors = SegmentedButtonDefaults.colors(
                    activeContentColor = MaterialTheme.colorScheme.background,
                    activeContainerColor = MaterialTheme.colorScheme.primary,
                    inactiveContentColor = MaterialTheme.colorScheme.onSurface.copy(0.3f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.background.copy(alpha = 0.2f)),
                icon = {},
                modifier = Modifier
                    .shadowGlow(
                        color = if (viewType == currentViewType) MaterialTheme.colorScheme.primary.copy(
                            alpha = 0.22f
                        ) else Color.Transparent,
                        offsetX = 0.dp,
                        offsetY = 0.dp,
                        blurRadius = 17.dp
                    ),
            ) {
                Text(text = when (viewType) {
                        PlanCalendarType.DAYS   -> "Дни"
                        PlanCalendarType.MONTHS -> "Месяцы"
                        PlanCalendarType.YEARS  -> "Годы"
                    }
                )
            }
        }
    }
}

@Composable
private fun DaysCalendar(
    viewModel: PlanCalendarViewModel,
    dialogStateChange: (PlanCalendarDialogMode) -> Unit
) {
    val dataState by viewModel.dataState.collectAsStateWithLifecycle()
    val currentDate = LocalDate.now()
    val startMonth = YearMonth.now().minusMonths(24)
    val endMonth = YearMonth.now().plusMonths(24)
    val daysOfWeek = remember { daysOfWeek() }
    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = YearMonth.now(),
        firstDayOfWeek = DayOfWeek.MONDAY,
        outDateStyle = OutDateStyle.EndOfGrid
    )
    HorizontalCalendar(
        state = calendarState,
        monthHeader = { _ ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                daysOfWeek.forEach { dayOfWeek ->
                    Text(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        textAlign = TextAlign.Center,
                        text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        dayContent = { day ->
            val isCurrentDay = day.date == currentDate
            val curLocalDate = day.date
            val curEntityIsNew = dataState.days.find{ it.date == curLocalDate }
            val curEntity = curEntityIsNew ?: PlanCalendarEntityDomain(date = curLocalDate)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(
                        color = if (isCurrentDay && day.position == DayPosition.MonthDate) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = CircleShape
                    )
                    .clickable {
                        if (day.position == DayPosition.MonthDate) {
                            viewModel.loadEntityAndTasks(curEntity, PlanCalendarType.DAYS)
                            if (curEntityIsNew == null) dialogStateChange(PlanCalendarDialogMode.EDIT) else dialogStateChange(
                                PlanCalendarDialogMode.VIEW
                            )
                        }
                    }
            ) {
                Text(
                    text = day.date.dayOfMonth.toString(),
                    color = if (day.position == DayPosition.MonthDate) {
                        if(isCurrentDay) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    },
                    fontSize = if (isCurrentDay && day.position == DayPosition.MonthDate) 18.sp else if (day.position == DayPosition.MonthDate) 15.sp else 14.sp
                )
                if (curEntity.id.toInt() != 0 && day.position == DayPosition.MonthDate && !isCurrentDay) {
                    val dotColor =
                        if(curLocalDate.isBefore(currentDate)) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                        else MaterialTheme.colorScheme.primary
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(dotColor)
                    )
                }
            }
        },
        monthFooter = { month ->
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${month.yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${month.yearMonth.year}",
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                )
                Icon(
                    imageVector = Icons.Filled.CalendarViewDay,
                    contentDescription = "иконка футера",
                    tint = MaterialTheme.colorScheme.onSurface.copy(0.2f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 9.dp, end = 18.dp)
                        .size(20.dp)
                )
            }
        }
    )
}

@Composable
private fun MonthsCalendar(
    viewModel: PlanCalendarViewModel,
    dialogStateChange: (PlanCalendarDialogMode) -> Unit
) {
    val dataState by viewModel.dataState.collectAsStateWithLifecycle()
    val currentYear = LocalDate.now().year
    val currentMonth = LocalDate.now().month
    val months = Month.entries

    Column {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            items(months) { month ->
                val curLocalDate = LocalDate.of(currentYear, month, 1)
                val curEntityIsNew = dataState.months.find{ it.date == curLocalDate }
                val curEntity = curEntityIsNew ?: PlanCalendarEntityDomain(date = curLocalDate)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .aspectRatio(1.6f)
                        .clip(RoundedCornerShape(15.dp))
                        .background(
                            color = if (month == currentMonth) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = RoundedCornerShape(15.dp)
                        )
                        .clickable {
                            viewModel.loadEntityAndTasks(curEntity, PlanCalendarType.MONTHS)
                            if (curEntityIsNew == null) dialogStateChange(PlanCalendarDialogMode.EDIT) else dialogStateChange(
                                PlanCalendarDialogMode.VIEW
                            )
                        }
                ) {
                    Text(
                        text = month.getDisplayName(TextStyle.FULL, Locale.getDefault()).replaceFirstChar { it.uppercaseChar() },
                        fontSize = if (month == currentMonth) 18.sp else 14.sp,
                        color = if (month == currentMonth) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    if (curEntity.id.toInt() != 0 && month != currentMonth) {
                        val dotColor =
                            if (LocalDate.of(currentYear, month, 1).isBefore(LocalDate.of(currentYear, currentMonth, 1))) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.primary
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = 10.dp)
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(dotColor)
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "$currentYear",
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
            )
            Icon(
                imageVector = Icons.Filled.CalendarViewWeek,
                contentDescription = "иконка футера",
                tint = MaterialTheme.colorScheme.onSurface.copy(0.2f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 12.dp, end = 24.dp)
                    .size(20.dp)
            )
        }
    }
}

@Composable
private fun YearsCalendar(
    viewModel: PlanCalendarViewModel,
    dialogStateChange: (PlanCalendarDialogMode) -> Unit
) {
    val dataState by viewModel.dataState.collectAsStateWithLifecycle()
    val currentYear = LocalDate.now().year
    val startYear = currentYear - 10
    val years = (startYear until currentYear + 10).toList()

    Column {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            items(years) { year ->
                val curLocalDate = LocalDate.of(year, 1, 1)
                val curEntityIsNew = dataState.years.find{ it.date == curLocalDate }
                val curEntity = curEntityIsNew ?: PlanCalendarEntityDomain(date = curLocalDate)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .aspectRatio(1.6f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            color = if (year == currentYear) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable {
                            viewModel.loadEntityAndTasks(curEntity, PlanCalendarType.YEARS)
                            if (curEntityIsNew == null) dialogStateChange(PlanCalendarDialogMode.EDIT) else dialogStateChange(
                                PlanCalendarDialogMode.VIEW
                            )
                        }
                ) {
                    Text(
                        text = year.toString(),
                        color = if (year == currentYear) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onSurface,
                        fontSize = if (year == currentYear) 18.sp else 14.sp,
                        textAlign = TextAlign.Center
                    )
                    if (curEntity.id.toInt() != 0 && year != currentYear) {
                        val dotColor =
                            if (LocalDate.of(year, 1, 1).isBefore(LocalDate.of(currentYear, 1, 1))) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.primary
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = 2.dp)
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(dotColor)
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "21 век",
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp),
            )
            Icon(
                imageVector = Icons.Filled.CalendarViewMonth,
                contentDescription = "fotterIcon",
                tint = MaterialTheme.colorScheme.onSurface.copy(0.2f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 18.dp, end = 22.dp)
                    .size(20.dp)
            )
        }
    }
}


/*****************************************************************
 * Public functions
 ****************************************************************/
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CalendarBlock(
    viewModel: PlanCalendarViewModel = hiltViewModel(),
    dialogStateChange: (PlanCalendarDialogMode) -> Unit
) {
    val dataState by viewModel.dataState.collectAsStateWithLifecycle()
    Card(
         shape = RoundedCornerShape(20.dp),
         elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
         colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
         modifier = Modifier
             .fillMaxWidth()
             .padding(vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(top = 25.dp)
                .padding(horizontal = 25.dp)
        ) {
            CalendarBlockHeader(
                currentViewType = dataState.type,
                onViewTypeChanged = { newType -> viewModel.changeType(newType) }
            )
            CalendarBlockContent(
                viewModel = viewModel,
                viewType = dataState.type,
                dialogStateChange = dialogStateChange
            )
        }
    }
}

@Composable
fun CalendarLoading(
    status: PlanCalendarLoading,
    onContent: @Composable () -> Unit
) {
    val isLoading = when (status) {
        is PlanCalendarLoading.Loading,
        is PlanCalendarLoading.Saving,
        is PlanCalendarLoading.Deleting -> true
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp)
                    .height(100.dp),
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

@Composable
fun CalendarAlert(
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
                .border(
                    width = 1.dp,
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.17f)
                )
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
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
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

/*****************************************************************
 * Classes
 ****************************************************************/
/*****************************************************************
 * Preview
 ****************************************************************/
@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun CalendarAlertPreview(){
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = MyAppFont,
    ){
        CalendarAlert(
            title = "Удалить задачу?",
            description = "Это действие нельзя отменить",
            onConfirm = { },
            onDismiss = {  }
        )
    }
}