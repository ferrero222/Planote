/*****************************************************************
 *  Package for calendar view components
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.view.plan.component

/*****************************************************************
 * Imported packages
 ****************************************************************/
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.planote.view.plan.CustomBlock
import com.example.planote.viewModel.plan.CalendarViewType
import com.example.planote.viewModel.plan.PlanCalendarViewModel
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

/*****************************************************************
 * Top Level Functions
 ****************************************************************/
@Composable
fun CalendarBlock(viewModel: PlanCalendarViewModel = hiltViewModel()) {
    CustomBlock(
        title = "Календарь",
        settingsExist = true,
        onSettingsClick = {}
    ) {
        val dataState by viewModel.dataState.collectAsStateWithLifecycle()
        Column {
            Row {
                CalendarTypeSelector(
                    currentViewType = dataState.currentViewType,
                    onViewTypeChanged = { viewModel.setViewType(it) }
                )
            }
            DaysCalendarView(viewModel, dataState.currentViewType)
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
                selected = currentViewType == viewType
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
private fun CalendarItem(
    day: CalendarDay,
    currentDate: LocalDate,
    viewType: CalendarViewType
) {
    val isCurrentDay = day.date == currentDate
    val isCurrentMonth = true
    val itemText = when(viewType){
        CalendarViewType.DAYS -> {day.date.dayOfMonth.toString()}
        CalendarViewType.MONTHS ->  {day.date.monthValue.toString()}
        CalendarViewType.YEARS -> {day.date.year.toString()}
    }
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .background(
                color = when {
                    isCurrentDay -> Color.Blue
                    !isCurrentMonth -> Color.LightGray.copy(alpha = 0.3f)
                    else -> Color.Transparent
                },
                shape = CircleShape
            )
            .border(
                width = 1.dp,
                color = Color.Transparent,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = itemText,
            color = when {
                isCurrentDay -> Color.White
                !isCurrentMonth -> Color.Gray
                else -> Color.Black
            },
            fontSize = 14.sp
        )
    }
}

@Composable
private fun DaysCalendarView(viewModel: PlanCalendarViewModel, viewType: CalendarViewType) {
    val currentDate = LocalDate.now()
    val startMonth = YearMonth.now().minusMonths(100) // 100 месяцев назад
    val endMonth = YearMonth.now().plusMonths(100)    // 100 месяцев вперед
    val daysOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY)

    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = YearMonth.from(currentDate),
        firstDayOfWeek = daysOfWeek.first(),
    )

    HorizontalCalendar(
        state = calendarState,
        monthHeader = { month ->
            val header = when(viewType){
                CalendarViewType.DAYS -> "${month.yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${month.yearMonth.year}"
                CalendarViewType.MONTHS -> "${month.yearMonth.year}"
                CalendarViewType.YEARS -> ""
            }
            Text(
                text = header,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                textAlign = TextAlign.Center
            )
        },
        dayContent = { day ->
            CalendarItem(day, currentDate, viewType)
        }
    )
}