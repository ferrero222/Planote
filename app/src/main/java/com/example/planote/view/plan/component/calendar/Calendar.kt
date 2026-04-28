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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarViewMonth
import androidx.compose.material.icons.filled.CalendarViewWeek
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ReplayCircleFilled
import androidx.compose.material.icons.filled.Square
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.planote.DarkColorScheme
import com.example.planote.MyAppFont
import com.example.planote.PreviewContainer
import com.example.planote.isLandscape
import com.example.planote.view.plan.PlannerBlockCard
import com.example.planote.viewModel.plan.PlanCalendarDataHolder
import com.example.planote.viewModel.plan.PlanCalendarDialogMode
import com.example.planote.viewModel.plan.PlanCalendarEntityDomain
import com.example.planote.viewModel.plan.PlanCalendarLoading
import com.example.planote.viewModel.plan.PlanCalendarTaskDomain
import com.example.planote.viewModel.plan.PlanCalendarType
import com.example.planote.viewModel.plan.PlanCalendarViewModel
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.daysOfWeek
import kotlinx.coroutines.launch
import me.trishiraj.shadowglow.shadowGlow
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.WeekFields
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
@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun CalendarBlock(
    dataState: PlanCalendarDataHolder,
    onTypeChanged: (PlanCalendarType) -> Unit,
    onDayClick: (PlanCalendarEntityDomain, PlanCalendarType) -> Unit,
    onMonthClick: (PlanCalendarEntityDomain, PlanCalendarType) -> Unit,
    onYearClick: (PlanCalendarEntityDomain, PlanCalendarType) -> Unit,
    onEntityTasks: suspend (PlanCalendarEntityDomain, PlanCalendarType) -> List<PlanCalendarTaskDomain>,
) {
    PlannerBlockCard {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            CalendarBlockHeader(
                currentViewType = dataState.type,
                onViewTypeChanged = onTypeChanged
            )
            CalendarBlockContent(
                dataState = dataState,
                onDayClick = onDayClick,
                onMonthClick = onMonthClick,
                onYearClick = onYearClick,
                onEntityTasks = onEntityTasks
            )
        }
    }
}

@Composable
private fun CalendarBlockHeader(
    currentViewType: PlanCalendarType,
    onViewTypeChanged: (PlanCalendarType) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(17.dp)
                    .weight(1f)
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

            Box(
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Square,
                        contentDescription = "иконка футера",
                        tint = if(currentViewType == PlanCalendarType.DAYS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(0.2f),
                        modifier = Modifier.size(13.dp)
                    )
                    Icon(
                        imageVector = Icons.Filled.Square,
                        contentDescription = "иконка футера",
                        tint = if(currentViewType == PlanCalendarType.MONTHS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(0.2f),
                        modifier = Modifier.size(13.dp)
                    )
                    Icon(
                        imageVector = Icons.Filled.Square,
                        contentDescription = "иконка футера",
                        tint = if(currentViewType == PlanCalendarType.YEARS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(0.2f),
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            modifier = Modifier.padding(top = 7.dp)
        )
        CalendarTypeSelector(
            currentViewType = currentViewType,
            onViewTypeChanged = { newType -> onViewTypeChanged(newType) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
                .height(40.dp)
        )
    }
}

@Composable
private fun CalendarBlockContent(
    dataState: PlanCalendarDataHolder,
    onDayClick: (PlanCalendarEntityDomain, PlanCalendarType) -> Unit,
    onMonthClick: (PlanCalendarEntityDomain, PlanCalendarType) -> Unit,
    onYearClick: (PlanCalendarEntityDomain, PlanCalendarType) -> Unit,
    onEntityTasks: suspend (PlanCalendarEntityDomain, PlanCalendarType) -> List<PlanCalendarTaskDomain>
) {
    Box(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
            .heightIn(
                min = if (isLandscape()) 650.dp else 300.dp,
                max = if (isLandscape()) 1000.dp else 300.dp
            )
    ) {
        AnimatedContent(
            targetState = dataState.type, label = "CalendarSwitchAnimation", transitionSpec = {
                if (targetState.ordinal > initialState.ordinal) (slideInHorizontally { it } + fadeIn()).togetherWith(slideOutHorizontally { -it } + fadeOut())
                else (slideInHorizontally { -it } + fadeIn()).togetherWith(slideOutHorizontally { it } + fadeOut())
            }
        ) { targetViewType -> when (targetViewType) {
                PlanCalendarType.DAYS   -> DaysCalendar(dataState, onDayClick, onEntityTasks)
                PlanCalendarType.MONTHS -> MonthsCalendar(dataState, onMonthClick)
                PlanCalendarType.YEARS  -> YearsCalendar(dataState, onYearClick)
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
                    inactiveContentColor = MaterialTheme.colorScheme.onSurface.copy(0.3f),
                    inactiveContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.4f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                icon = {},
                modifier = Modifier
                    .shadowGlow(
                        color = if (viewType == currentViewType) MaterialTheme.colorScheme.primary.copy(alpha = 0.22f) else Color.Transparent,
                        offsetX = 0.dp,
                        offsetY = 0.dp,
                        blurRadius = 17.dp
                    ),
            ) {
                Text(text = when (viewType) {
                        PlanCalendarType.DAYS   -> "ДНИ"
                        PlanCalendarType.MONTHS -> "МЕСЯЦЫ"
                        PlanCalendarType.YEARS  -> "ГОДЫ"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun CalendarCellBoxPortrait(
    modifier: Modifier = Modifier,
    label: String,
    ratio: Float = 1.3f,
    onClick: (() -> Unit)? = null,
    showDot: Boolean = false,
    dotColor: Color = MaterialTheme.colorScheme.primary,
    dotPadding: Dp = 0.dp,
    dotSize: Dp = 5.dp,
    boxHeight: TextUnit = 20.sp,
    boxLenght: Float = 1f,
    isOutEntity: Boolean = false,
    isCurEntity: Boolean = false,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
          .aspectRatio(ratio)
          .let { if (onClick != null) it.clickable { onClick() } else it }
    ) {
      Text(
          textAlign = TextAlign.Center,
          text = label,
          color = if (!isOutEntity) {
              if(isCurEntity) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onSurface
          } else {
              MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
          },
          fontSize = 15.sp,
          lineHeight = boxHeight,
          fontWeight = if (isCurEntity && !isOutEntity) FontWeight.Bold else FontWeight.Normal,
          modifier = modifier
              .fillMaxWidth(boxLenght)
              .background(
                  color = if(isCurEntity) MaterialTheme.colorScheme.primary else Color.Transparent
              )
      )
      if (showDot) {
            Box(
                modifier = Modifier
                    .padding(vertical = dotPadding)
                    .align(Alignment.TopCenter)
                    .size(dotSize)
                    .clip(RectangleShape)
                    .background(dotColor)
            )
        }
    }
}

@Composable
private fun CalendarCellBoxLandScape(
    modifier: Modifier = Modifier,
    label: String,
    entity: PlanCalendarEntityDomain,
    type: PlanCalendarType,
    onEntityTasks: suspend (PlanCalendarEntityDomain, PlanCalendarType) -> List<PlanCalendarTaskDomain>,
    ratio: Float = 1.3f,
    onClick: (() -> Unit)? = null,
    boxHeight: TextUnit = 20.sp,
    boxLenght: Float = 1f,
    isOutEntity: Boolean = false,
    isCurEntity: Boolean = false,
) {
    var loadingState by remember(entity.id, entity.date) { mutableStateOf<PlanCalendarLoading>(PlanCalendarLoading.Idle) }
    var tasks by remember(entity.id, entity.date) { mutableStateOf<List<PlanCalendarTaskDomain>>(                      listOf(
        PlanCalendarTaskDomain(id = 1, ownerId = entity.id, title = "Задача dasdasdsadsadas1"),
        PlanCalendarTaskDomain(id = 2, ownerId = entity.id, title = "Задача 2"),
        PlanCalendarTaskDomain(id = 3, ownerId = entity.id, title = "Задача 3"),
        PlanCalendarTaskDomain(id = 4, ownerId = entity.id, title = "Задача 4"),
        PlanCalendarTaskDomain(id = 5, ownerId = entity.id, title = "Задача 5"),
        PlanCalendarTaskDomain(id = 6, ownerId = entity.id, title = "Задача 6")
    )) }

    LaunchedEffect(entity.id, entity.date) {
        if (isOutEntity || entity.id == 0L) {
            tasks = emptyList()
            loadingState = PlanCalendarLoading.Idle
        } else {
            loadingState = PlanCalendarLoading.Loading
            tasks = onEntityTasks(entity, type)
            loadingState = PlanCalendarLoading.Idle
        }
    }

    Box(
        modifier = modifier
            .aspectRatio(ratio)
            .padding(bottom = 5.dp)
            .let { if (onClick != null) it.clickable { onClick() } else it }
    ) {
        Column {
            Text(
                textAlign = TextAlign.Center,
                text = label,
                color = if (!isOutEntity) {
                    if(isCurEntity) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                },
                fontSize = 15.sp,
                lineHeight = boxHeight,
                fontWeight = if (isCurEntity && !isOutEntity) FontWeight.Bold else FontWeight.Normal,
                modifier = modifier
                    .fillMaxWidth(boxLenght)
                    .background(
                        color = if(isCurEntity) MaterialTheme.colorScheme.primary else Color.Transparent
                    )
            )
            HorizontalDivider(
                thickness = 3.dp,
                color = if (!isOutEntity) {
                    if(isCurEntity) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                } else {
                    Color.Transparent
                },
                modifier = Modifier.padding(horizontal = if(!isCurEntity) 5.dp else 0.dp)
            )
            CalendarLoading(loadingState) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    if (!isOutEntity && tasks.isEmpty()) {
                        Text(
                            text = "Нет задач",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    } else if(!isOutEntity) {
                        tasks.forEachIndexed { index, task ->
                            Text(
                                text = if(index >= 3) "..." else  task.title ?: "Без названия",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DaysCalendar(
    dataState: PlanCalendarDataHolder,
    onDayClick: (PlanCalendarEntityDomain, PlanCalendarType) -> Unit,
    onEntityTasks: suspend (PlanCalendarEntityDomain, PlanCalendarType) -> List<PlanCalendarTaskDomain>
) {
    val currentDate = LocalDate.now()
    val nowMonth = YearMonth.now()
    val startMonth = nowMonth.minusMonths(24)
    val endMonth = nowMonth.plusMonths(24)
    val daysOfWeek = remember { daysOfWeek() }
    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = nowMonth,
        firstDayOfWeek = DayOfWeek.MONDAY,
        outDateStyle = OutDateStyle.EndOfGrid
    )
    val scope = rememberCoroutineScope()
    HorizontalCalendar(
        state = calendarState,
        monthHeader = { _ ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp)
                    .padding(bottom = if(isLandscape()) 15.dp else 0.dp)
            ) {
                daysOfWeek.forEach { dayOfWeek ->
                    Text(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                        fontSize = 12.sp,
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
            if(isLandscape()){
                CalendarCellBoxLandScape(
                    label = day.date.dayOfMonth.toString(),
                    entity = curEntity,
                    type = PlanCalendarType.DAYS,
                    onEntityTasks = onEntityTasks,
                    onClick = { if(day.position == DayPosition.MonthDate) { onDayClick(curEntity, PlanCalendarType.DAYS) } },
                    isCurEntity = isCurrentDay,
                    isOutEntity = (day.position != DayPosition.MonthDate)
                )
            } else {
                CalendarCellBoxPortrait(
                    label = day.date.dayOfMonth.toString(),
                    onClick = { if(day.position == DayPosition.MonthDate) { onDayClick(curEntity, PlanCalendarType.DAYS) } },
                    showDot = (curEntity.id.toInt() != 0 && day.position == DayPosition.MonthDate && !isCurrentDay),
                    dotColor = if(curLocalDate.isBefore(currentDate)) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primary,
                    isCurEntity = isCurrentDay,
                    isOutEntity = (day.position != DayPosition.MonthDate)
                )
            }
        },

        monthFooter = { month ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "${month.yearMonth.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase().replace(".", "")} #${month.yearMonth.year}",
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "W/${month.yearMonth.atStartOfMonth().get(WeekFields.ISO.weekOfWeekBasedYear())}/${
                            month.yearMonth.atEndOfMonth().get(WeekFields.ISO.weekOfWeekBasedYear())
                        }",
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    )
                    IconButton (
                        onClick = { scope.launch { calendarState.animateScrollToMonth(nowMonth) } },
                        modifier = Modifier.size(20.dp)
                    ){
                        Icon(
                            imageVector = Icons.Filled.ReplayCircleFilled,
                            contentDescription = "иконка футера",
                            tint = MaterialTheme.colorScheme.primary.copy(0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun MonthsCalendar(
    dataState: PlanCalendarDataHolder,
    onMonthClick: (PlanCalendarEntityDomain, PlanCalendarType) -> Unit
) {
    val currentYear = LocalDate.now().year
    val currentMonth = LocalDate.now().month
    val months = Month.entries

    Column {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(months) { month ->
                val curLocalDate = LocalDate.of(currentYear, month, 1)
                val curEntityIsNew = dataState.months.find{ it.date == curLocalDate }
                val curEntity = curEntityIsNew ?: PlanCalendarEntityDomain(date = curLocalDate)
                val dotColor = if (LocalDate.of(currentYear, month, 1).isBefore(LocalDate.of(currentYear, currentMonth, 1))) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                               else MaterialTheme.colorScheme.primary
                CalendarCellBoxPortrait(
                    label = month.getDisplayName(TextStyle.FULL, Locale.getDefault()).replaceFirstChar { it.uppercaseChar() },
                    ratio = 2f,
                    onClick = { onMonthClick(curEntity, PlanCalendarType.MONTHS) },
                    showDot = (curEntity.id.toInt() != 0 && month != currentMonth),
                    dotColor = dotColor,
                    dotSize = 7.dp,
                    dotPadding = 5.dp,
                    boxHeight = 35.sp,
                    boxLenght = 0.85f,
                    isCurEntity = month == currentMonth
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 25.dp)
                .padding(top = 10.dp)
        ) {
            Text(
                text = "$currentYear",
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Icon(
                imageVector = Icons.Filled.CalendarViewWeek,
                contentDescription = "иконка футера",
                tint = MaterialTheme.colorScheme.onSurface.copy(0.2f),
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
private fun YearsCalendar(
    dataState: PlanCalendarDataHolder,
    onYearClick: (PlanCalendarEntityDomain, PlanCalendarType) -> Unit
) {
    val currentYear = LocalDate.now().year
    val startYear = currentYear - 10
    val years = (startYear until currentYear + 10).toList()

    Column {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(years) { year ->
                val curLocalDate = LocalDate.of(year, 1, 1)
                val curEntityIsNew = dataState.years.find{ it.date == curLocalDate }
                val curEntity = curEntityIsNew ?: PlanCalendarEntityDomain(date = curLocalDate)
                val dotColor = if (LocalDate.of(year, 1, 1).isBefore(LocalDate.of(currentYear, 1, 1))) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                               else MaterialTheme.colorScheme.primary
                CalendarCellBoxPortrait(
                    label = year.toString(),
                    ratio = 2f,
                    onClick = { onYearClick(curEntity, PlanCalendarType.YEARS) },
                    showDot = (curEntity.id.toInt() != 0 && year != currentYear),
                    dotColor = dotColor,
                    dotSize = 7.dp,
                    dotPadding = 0.dp,
                    boxHeight = 30.sp,
                    boxLenght = 0.85f,
                    isCurEntity = year == currentYear
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp)
        ) {
            Text(
                text = "21 век",
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Icon(
                imageVector = Icons.Filled.CalendarViewMonth,
                contentDescription = "fotterIcon",
                tint = MaterialTheme.colorScheme.onSurface.copy(0.2f),
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.CenterEnd)
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

    CalendarBlock(
        dataState = dataState,
        onTypeChanged = { newType -> viewModel.changeType(newType) },
        onDayClick = { entity, type ->
            viewModel.loadEntityAndTasks(entity, type)
            dialogStateChange(if (entity.id == 0L) PlanCalendarDialogMode.EDIT else PlanCalendarDialogMode.VIEW)
        },
        onMonthClick = { entity, type ->
            viewModel.loadEntityAndTasks(entity, type)
            dialogStateChange(if (entity.id == 0L) PlanCalendarDialogMode.EDIT else PlanCalendarDialogMode.VIEW)
        },
        onYearClick = { entity, type ->
            viewModel.loadEntityAndTasks(entity, type)
            dialogStateChange(if (entity.id == 0L) PlanCalendarDialogMode.EDIT else PlanCalendarDialogMode.VIEW)
        },
        onEntityTasks = { entity, type -> viewModel.observeEntityTasks(entity, type) }
    )
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
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RectangleShape,
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
@Preview(showBackground = true, backgroundColor = 0xFF121212,
    device = "spec:width=750.4dp,height=750.5dp,dpi=440,orientation=landscape"
)
@Composable
fun CalendarBlockPreview(
    dataState: PlanCalendarDataHolder = PlanCalendarDataHolder(
        days = listOf(
            PlanCalendarEntityDomain(id = 1, title = "День", date = LocalDate.now()),
            PlanCalendarEntityDomain(id = 2, title = "Встреча", date = LocalDate.now().plusDays(1)),
            PlanCalendarEntityDomain(id = 3, title = "Задача", date = LocalDate.now().minusDays(3))
        ),
        months = listOf(
            PlanCalendarEntityDomain(id = 1, title = "План", date = LocalDate.of(LocalDate.now().year, 5, 1)),
            PlanCalendarEntityDomain(id = 1, title = "План", date = LocalDate.of(LocalDate.now().year, 2, 1))
        ),
        years = listOf(
            PlanCalendarEntityDomain(id = 1, title = "Год", date = LocalDate.of(LocalDate.now().year +2, 1, 1)),
            PlanCalendarEntityDomain(id = 1, title = "Год", date = LocalDate.of(LocalDate.now().year -2, 1, 1)),
            PlanCalendarEntityDomain(id = 1, title = "Год", date = LocalDate.of(LocalDate.now().year +5, 1, 1))
        ),
        type = PlanCalendarType.DAYS
    )
) {
    MaterialTheme(colorScheme = DarkColorScheme, typography = MyAppFont) {
        PreviewContainer {
            CalendarBlock(
                dataState = dataState,
                onTypeChanged = {},
                onDayClick = { _, _ -> },
                onMonthClick = { _, _ -> },
                onYearClick = { _, _ -> },
                onEntityTasks = { entity, type ->
                    if (type == PlanCalendarType.DAYS && entity.id == 3L) {
                        listOf(
                            PlanCalendarTaskDomain(id = 1, ownerId = entity.id, title = "Задача 1"),
                            PlanCalendarTaskDomain(id = 2, ownerId = entity.id, title = "Задача 2"),
                            PlanCalendarTaskDomain(id = 3, ownerId = entity.id, title = "Задача 3"),
                            PlanCalendarTaskDomain(id = 4, ownerId = entity.id, title = "Задача 4"),
                            PlanCalendarTaskDomain(id = 5, ownerId = entity.id, title = "Задача 5"),
                            PlanCalendarTaskDomain(id = 6, ownerId = entity.id, title = "Задача 6")
                        )
                    } else {
                        emptyList()
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun CalendarAlertPreview(){
    PreviewContainer {
        CalendarAlert(
            title = "Удалить задачу?",
            description = "Это действие нельзя отменить",
            onConfirm = { },
            onDismiss = { }
        )
    }
}
