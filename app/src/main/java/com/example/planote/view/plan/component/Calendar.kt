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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
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
            CalendarContentView(viewModel, dataState.currentViewType)
        }
    }
}

@Composable
private fun CalendarContentView(viewModel: PlanCalendarViewModel, viewType: CalendarViewType) {
    when (viewType) {
        CalendarViewType.DAYS -> DaysCalendar(viewModel)
        CalendarViewType.MONTHS -> MonthsCalendar(viewModel)
        CalendarViewType.YEARS -> YearsCalendar(viewModel)
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
private fun DaysCalendar(viewModel: PlanCalendarViewModel) {
    val currentDate = LocalDate.now()
    val startMonth = YearMonth.now().minusMonths(100)
    val endMonth = YearMonth.now().plusMonths(100)
    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = YearMonth.now(),
        firstDayOfWeek = DayOfWeek.MONDAY,
    )

    HorizontalCalendar(
        state = calendarState,
        monthHeader = { month ->
            Text(
                text = "${month.yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${month.yearMonth.year}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                textAlign = TextAlign.Center
            )
        },
        dayContent = { day ->
            CalendarDayItem(day, currentDate)
        }
    )
}

@Composable
private fun CalendarDayItem(day: CalendarDay, currentDate: LocalDate) {
    val isCurrentDay = day.date == currentDate
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .background(
                color = if (isCurrentDay) Color.Blue else Color.Transparent,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            color = if (isCurrentDay) Color.White else Color.Black,
            fontSize = 14.sp
        )
    }
}



@Composable
private fun MonthsCalendar(viewModel: PlanCalendarViewModel) {
    val currentYear = LocalDate.now().year
    val months = Month.values().toList() // JANUARY to DECEMBER

    LazyVerticalGrid(
        columns = GridCells.Fixed(3), // 3 колонки → 4 строки
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(months) { month ->
            MonthItem(
                month = month,
                year = currentYear,
                onClick = {
                    // Здесь можно вызвать viewModel.selectMonth(year, month)
                    // Или переключиться обратно в DAYS с выбранным месяцем
                }
            )
        }
    }
}

@Composable
private fun MonthItem(month: Month, year: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1.5f)
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = month.getDisplayName(TextStyle.SHORT, Locale.getDefault()).replaceFirstChar { it.uppercase() },
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun YearsCalendar(viewModel: PlanCalendarViewModel) {
    val currentYear = LocalDate.now().year
    val startYear = ((currentYear - 6) / 12) * 12
    val years = (startYear until startYear + 12).toList()
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(years) { year ->
            YearItem(
                year = year,
                isSelected = (year == currentYear),
                onClick = {
                    // viewModel.selectYear(year)
                }
            )
        }
    }
}

@Composable
private fun YearItem(year: Int, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp)
            .background(
                color = if (isSelected) Color.Blue else Color.Transparent,
                shape = CircleShape
            )
            .border(1.dp, if (isSelected) Color.Blue else Color.Gray, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = year.toString(),
            color = if (isSelected) Color.White else Color.Black,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}
