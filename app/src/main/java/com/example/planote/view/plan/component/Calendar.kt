/*****************************************************************
 *  Package for main screen with circular pager
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.view.plan.component

/*****************************************************************
 * Imported packages
 ****************************************************************/
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarViewDay
import androidx.compose.material.icons.filled.CalendarViewMonth
import androidx.compose.material.icons.filled.CalendarViewWeek
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.planote.view.plan.PlanColorOnSurface
import com.example.planote.viewModel.plan.CalendarViewType
import com.example.planote.viewModel.plan.PlanCalendarViewModel
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.daysOfWeek
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
 * Top level functions
 ****************************************************************/
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CalendarBlock(viewModel: PlanCalendarViewModel = hiltViewModel()) {
    Card(
         shape = RoundedCornerShape(20.dp),
         elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
         colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
         modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
    ) {
        val dataState by viewModel.dataState.collectAsStateWithLifecycle()

        Column(
            modifier = Modifier.padding(top = 25.dp).padding(horizontal = 25.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(color = PlanColorOnSurface)
            ) {
                CalendarTypeSelector(
                    currentViewType = dataState.currentViewType, onViewTypeChanged = { viewModel.setViewType(it) }
                )
            }
            Box(
                modifier = Modifier.padding(top = 15.dp).fillMaxWidth().requiredHeight(400.dp)
            ) {
                AnimatedContent(
                    targetState = dataState.currentViewType, label = "CalendarSwitchAnimation", transitionSpec = {
                        if (targetState.ordinal > initialState.ordinal) {
                            (slideInHorizontally { it } + fadeIn()).togetherWith(slideOutHorizontally { -it } + fadeOut())
                        } else {
                            (slideInHorizontally { -it } + fadeIn()).togetherWith(slideOutHorizontally { it } + fadeOut())
                        }
                    }
                ) { viewType ->
                        when (viewType) {
                            CalendarViewType.DAYS   -> DaysCalendar(viewModel)
                            CalendarViewType.MONTHS -> MonthsCalendar(viewModel)
                            CalendarViewType.YEARS  -> YearsCalendar(viewModel)
                        }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarTypeSelector(currentViewType: CalendarViewType, onViewTypeChanged: (CalendarViewType) -> Unit, modifier: Modifier = Modifier) {
    SingleChoiceSegmentedButtonRow(
        modifier = modifier
    ) {
        CalendarViewType.entries.forEach { viewType ->
            SegmentedButton(
                shape = RoundedCornerShape(5.dp),
                onClick = { onViewTypeChanged(viewType) },
                selected = currentViewType == viewType,
                colors = SegmentedButtonDefaults.colors(
                         activeContentColor = MaterialTheme.colorScheme.background,
                         activeContainerColor = MaterialTheme.colorScheme.primary,
                         inactiveContentColor = MaterialTheme.colorScheme.onSurface.copy(0.3f)
                ),
                border = BorderStroke(0.dp, Color.Transparent),
                icon = {},
                modifier = Modifier.padding(horizontal = 10.dp).padding(top = 5.dp).padding(bottom = 5.dp),
            ) {
                Text(text = when (viewType) {
                        CalendarViewType.DAYS -> "Дни"
                        CalendarViewType.MONTHS -> "Месяцы"
                        CalendarViewType.YEARS -> "Годы"
                    }
                )
            }
        }
    }
}

@Composable
private fun DaysCalendar(viewModel: PlanCalendarViewModel) {
    val currentDate = LocalDate.now()
    val startMonth = YearMonth.now().minusMonths(24)
    val endMonth = YearMonth.now().plusMonths(24)
    val daysOfWeek = remember { daysOfWeek() }
    val calendarState = rememberCalendarState(startMonth = startMonth,
                                              endMonth = endMonth,
                                              firstVisibleMonth = YearMonth.now(),
                                              firstDayOfWeek = DayOfWeek.MONDAY,
                                              outDateStyle = OutDateStyle.EndOfGrid)
    HorizontalCalendar(
        state = calendarState,
        monthHeader = { month ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
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
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.aspectRatio(1f)
                                   .padding(2.dp)
                                   .clip(CircleShape)
                                   .background(
                                       color = if (isCurrentDay) MaterialTheme.colorScheme.primary else Color.Transparent,
                                       shape = CircleShape
                                   )
                                   .clickable { /* TODO */ },
            ) {
                Text(
                    text = day.date.dayOfMonth.toString(),
                    color = if (day.position == DayPosition.MonthDate) {
                        if(isCurrentDay) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    },
                    fontSize = if (isCurrentDay) 18.sp else if (day.position == DayPosition.MonthDate) 15.sp else 14.sp
                )
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
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                )
                Icon(
                    imageVector = Icons.Filled.CalendarViewDay,
                    contentDescription = "иконка футера",
                    tint = MaterialTheme.colorScheme.onSurface.copy(0.2f),
                    modifier = Modifier.align(Alignment.TopEnd).padding(top = 9.dp, end = 18.dp).size(20.dp)
                )
            }
        }
    )
}

@Composable
private fun MonthsCalendar(viewModel: PlanCalendarViewModel) {
    val currentYear = LocalDate.now().year
    val currentMonth = LocalDate.now().month
    val months = Month.entries

    Column {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
        ) {
            items(months) { month ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .aspectRatio(1.6f)
                        .clip(RoundedCornerShape(15.dp))
                        .background(
                            color = if (month == currentMonth) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = RoundedCornerShape(15.dp)
                        )
                        .clickable { /* TODO */ }

                ) {
                    Text(
                        text = month.getDisplayName(TextStyle.FULL, Locale.getDefault()).replaceFirstChar { it.uppercaseChar() },
                        fontSize = if (month == currentMonth) 18.sp else 14.sp,
                        color = if (month == currentMonth) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

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
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
            )
            Icon(
                imageVector = Icons.Filled.CalendarViewWeek,
                contentDescription = "иконка футера",
                tint = MaterialTheme.colorScheme.onSurface.copy(0.2f),
                modifier = Modifier.align(Alignment.TopEnd).padding(top = 12.dp, end = 24.dp).size(20.dp)
            )
        }
    }
}

@Composable
private fun YearsCalendar(viewModel: PlanCalendarViewModel) {
    val currentYear = LocalDate.now().year
    val startYear = currentYear - 10
    val years = (startYear until currentYear + 10).toList()

    Column {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
        ) {
            items(years) { year ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .aspectRatio(1.6f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            color = if (year == currentYear) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { /* TODO */ },
                ) {
                    Text(
                        text = year.toString(),
                        color = if (year == currentYear) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onSurface,
                        fontSize = if (year == currentYear) 18.sp else 14.sp,
                        textAlign = TextAlign.Center
                    )
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
                modifier = Modifier.fillMaxWidth().padding(top = 15.dp),
            )
            Icon(
                imageVector = Icons.Filled.CalendarViewMonth,
                contentDescription = "иконка футера",
                tint = MaterialTheme.colorScheme.onSurface.copy(0.2f),
                modifier = Modifier.align(Alignment.TopEnd).padding(top = 18.dp, end = 22.dp).size(20.dp)
            )
        }
    }
}

/*****************************************************************
 * Classes
 ****************************************************************/
/*****************************************************************
 * Preview
 ****************************************************************/