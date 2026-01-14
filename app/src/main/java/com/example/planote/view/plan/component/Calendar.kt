/*****************************************************************
 *  Package for calendar view components
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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
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
 * Top Level Functions
 ****************************************************************/
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CalendarBlock(viewModel: PlanCalendarViewModel = hiltViewModel()) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        val dataState by viewModel.dataState.collectAsStateWithLifecycle()
        Column{
            Row(modifier = Modifier.padding(top = 12.dp).padding(horizontal = 20.dp)) {
                CalendarTypeSelector(
                    currentViewType = dataState.currentViewType,
                    onViewTypeChanged = { viewModel.setViewType(it) }
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(440.dp)
            ) {
                AnimatedContent(
                    targetState = dataState.currentViewType,
                    label = "CalendarSwitchAnimation",
                    transitionSpec = {
                        if (targetState.ordinal > initialState.ordinal) {
                            (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> -width } + fadeOut())
                        } else {
                            (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> width } + fadeOut())
                        }
                    }
                ) { viewType ->
                    when (viewType) {
                        CalendarViewType.DAYS -> DaysCalendar(viewModel)
                        CalendarViewType.MONTHS -> MonthsCalendar(viewModel)
                        CalendarViewType.YEARS -> YearsCalendar(viewModel)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarTypeSelector(currentViewType: CalendarViewType, onViewTypeChanged: (CalendarViewType) -> Unit) {
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        CalendarViewType.entries.forEach { viewType ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = viewType.ordinal, count = CalendarViewType.entries.size),
                onClick = { onViewTypeChanged(viewType) },
                selected = currentViewType == viewType,
                colors = SegmentedButtonDefaults.colors(activeContentColor = colorScheme.onSecondary, activeContainerColor = colorScheme.primary),
                icon = {}
            ) {
                Text(
                    text = when (viewType) {
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
    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = YearMonth.now(),
        firstDayOfWeek = DayOfWeek.MONDAY,
        outDateStyle = OutDateStyle.EndOfGrid
    )
    HorizontalCalendar(
        state = calendarState,
        monthHeader = { month ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                for (dayOfWeek in daysOfWeek) {
                    Text(
                        modifier = Modifier.weight(1f),
                        color = colorScheme.onSecondary.copy(alpha = 0.3f),
                        textAlign = TextAlign.Center,
                        text = dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.getDefault()),
                    )
                }
            }
        },
        dayContent = { day ->
            val isCurrentDay = day.date == currentDate
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(2.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(
                        color = if (isCurrentDay) colorScheme.primary else Color.Transparent,
                        shape = CircleShape
                    )
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day.date.dayOfMonth.toString(),
                    color = if(day.position == DayPosition.MonthDate) colorScheme.onSecondary else colorScheme.onSecondary.copy(alpha = 0.3f),
                    fontSize = if(isCurrentDay) 18.sp else if(day.position == DayPosition.MonthDate) 15.sp else 14.sp
                )
            }
        },
        monthFooter = {month ->
            Text(
                text = "${month.yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${month.yearMonth.year}",
                fontSize = 15.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                textAlign = TextAlign.Center,
                color = colorScheme.onSecondary.copy(alpha = 0.3f),
            )
        }

    )
}

@Composable
private fun MonthsCalendar(viewModel: PlanCalendarViewModel) {
    val currentYear = LocalDate.now().year
    val currentMonth = LocalDate.now().month
    val months = Month.values().toList()

    Column {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
        ) {
            items(months) { month ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1.6f)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(
                            color = if (month == currentMonth) colorScheme.primary else Color.Transparent,
                            shape = RoundedCornerShape(50.dp)
                        )
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                            .replaceFirstChar { it.uppercase() },
                        fontSize = if (month == currentMonth) 18.sp else 14.sp,
                        color = colorScheme.onSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        Spacer(modifier = Modifier.padding(8.dp))
        Text(
            text = "$currentYear",
            fontSize = 18.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            textAlign = TextAlign.Center,
            color = colorScheme.onSecondary.copy(alpha = 0.3f),
        )
    }
}

@Composable
private fun YearsCalendar(viewModel: PlanCalendarViewModel) {
    val currentYear = LocalDate.now().year
    val startYear = (currentYear - 10)
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
                    modifier = Modifier
                        .aspectRatio(1.6f)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(
                            color = if (year == currentYear) colorScheme.primary else Color.Transparent,
                            shape = RoundedCornerShape(50.dp)
                        )
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = year.toString(),
                        color = colorScheme.onSecondary,
                        fontSize = if (year == currentYear) 18.sp else 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        Spacer(modifier = Modifier.padding(8.dp))
        Text(
            text = "21 век",
            fontSize = 18.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            textAlign = TextAlign.Center,
            color = colorScheme.onSecondary.copy(alpha = 0.3f),
        )
    }
}
