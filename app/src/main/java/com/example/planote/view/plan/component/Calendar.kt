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
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CalendarViewDay
import androidx.compose.material.icons.filled.CalendarViewMonth
import androidx.compose.material.icons.filled.CalendarViewWeek
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.planote.view.plan.CalendarDialogMode
import com.example.planote.view.plan.PlanColorOnSurface
import com.example.planote.view.plan.PlannerDialogType
import com.example.planote.viewModel.plan.CalendarType
import com.example.planote.viewModel.plan.PlanCalendarEntityDomain
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
fun CalendarBlock(viewModel: PlanCalendarViewModel = hiltViewModel(), dialogStateChange: (PlannerDialogType) -> Unit) {
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(color = PlanColorOnSurface)
            ) {
                CalendarTypeSelector(
                    currentViewType = dataState.type, onViewTypeChanged = { viewModel.setType(it) }
                )
            }
            Box(
                modifier = Modifier
                    .padding(top = 15.dp)
                    .fillMaxWidth()
                    .requiredHeight(400.dp)
            ) {
                AnimatedContent(
                    targetState = dataState.type, label = "CalendarSwitchAnimation", transitionSpec = {
                        if (targetState.ordinal > initialState.ordinal) {
                            (slideInHorizontally { it } + fadeIn()).togetherWith(slideOutHorizontally { -it } + fadeOut())
                        } else {
                            (slideInHorizontally { -it } + fadeIn()).togetherWith(slideOutHorizontally { it } + fadeOut())
                        }
                    }
                ) { viewType -> when (viewType) {
                        CalendarType.DAYS   -> DaysCalendar(viewModel, dialogStateChange)
                        CalendarType.MONTHS -> MonthsCalendar(viewModel, dialogStateChange)
                        CalendarType.YEARS  -> YearsCalendar(viewModel, dialogStateChange)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CalendarDetailsDialog(date: LocalDate, type: CalendarType, mode: CalendarDialogMode, dialogStateChange: (PlannerDialogType) -> Unit) {
    Dialog(onDismissRequest = { dialogStateChange(PlannerDialogType.None) }) {
        Card(
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().padding(vertical = 120.dp)
        ) {
            AnimatedContent(
                targetState = mode,
                label = "Dialog mode transition",
                transitionSpec = { (fadeIn(animationSpec = tween(200)) + scaleIn(initialScale = 0.95f, animationSpec = tween(200))).togetherWith(fadeOut(animationSpec = tween(150))) }
            ) {
                currentMode -> CalendarDetailsDialogContent(date = date, type = type, mode = currentMode, dialogStateChange = dialogStateChange)
            }
        }
    }
}

@Composable
fun CalendarDetailsDialogContent(viewModel: PlanCalendarViewModel = hiltViewModel(), date: LocalDate, type: CalendarType, mode: CalendarDialogMode, dialogStateChange: (PlannerDialogType) -> Unit
) {
    var curEntity = viewModel.getEntity(type).find { it.date == date }



    var curTasks = viewModel.getTasks()
    viewModel.getTasks(type, curEntity ?: PlanCalendarEntityDomain())
    val taskState by viewModel.taskState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize().padding(vertical = 15.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, //Header
                modifier = Modifier.fillMaxWidth().padding(start = 10.dp, end = 20.dp)
            ) {
                IconButton(
                    onClick = {
                        when (mode) {
                            CalendarDialogMode.VIEW -> dialogStateChange(PlannerDialogType.None)
                            CalendarDialogMode.EDIT -> dialogStateChange(PlannerDialogType.CalendarDetails(date = date, type = type, mode = CalendarDialogMode.VIEW))
                        }
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (mode == CalendarDialogMode.VIEW) Icons.Filled.Close else Icons.Filled.ArrowBackIosNew,
                        contentDescription = if (mode == CalendarDialogMode.VIEW) "Закрыть" else "Назад",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }

                val month = date.month.getDisplayName(TextStyle.FULL, Locale.getDefault()).replaceFirstChar { it.uppercaseChar() }
                val textHeader = when (type) {
                    CalendarType.DAYS -> "$month ${date.dayOfMonth}"
                    CalendarType.MONTHS -> "$month ${date.year}"
                    CalendarType.YEARS -> "${date.year} year"
                }
                Text(
                    text = textHeader,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f).padding(top = 5.dp)
                )

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .background(
                            color = if(mode == CalendarDialogMode.VIEW) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f) else MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(15.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = if (mode == CalendarDialogMode.VIEW) "VIEW" else "EDIT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if(mode == CalendarDialogMode.VIEW) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background
                    )
                }
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) { //Description
                Text(
                    text = "ОПИСАНИЕ",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                )
                if (mode == CalendarDialogMode.VIEW) {
                    Text(
                        text = descriptionText.ifEmpty { "Нет данных" },
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                } else {
                    OutlinedTextField(
                        value = descriptionText,
                        onValueChange = { descriptionText = it },
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    )
                }
            }

            /* ===== TASKS ===== */
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                Text(
                    text = "ЗАДАЧИ",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )

                if (curEntity != null && !taskState.tasks.isEmpty()) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.heightIn(max = 200.dp).fillMaxWidth()
                    ) {
                        items(taskState.tasks, key = { it.id }) { task ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Checkbox(
                                    checked = task.isDone,
                                    onCheckedChange = { completed ->
                                        viewModel.updateTask(type, curEntity, task.copy(isDone = completed))
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MaterialTheme.colorScheme.primary,
                                        uncheckedColor = Color.Gray
                                    )
                                )
                                Text(
                                    text = task.title ?: "Нет описания",
                                    textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None,
                                    color = if (task.isDone) Color.Gray else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 15.sp,
                                    modifier = Modifier.weight(1f)
                                )

                                if (mode == CalendarDialogMode.EDIT) {
                                    Row {
                                        IconButton(
                                            onClick = { viewModel.getTasks(type, curEntity)}
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.EditNote,
                                                contentDescription = "Редактировать",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                viewModel.deleteTask(type, curEntity, task)
                                                viewModel.getTasks(type, curEntity)
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Удалить",
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Text("Нет данных", color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp)
                }

                if (mode == CalendarDialogMode.EDIT) {
                    Button(
                        onClick = { /* TODO: Добавить задачу */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.0f),
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp).align(Alignment.Start)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Добавить задачу", fontSize = 15.sp)
                    }
                }
            }
        }

        if (mode == CalendarDialogMode.VIEW) { //Footer
            Button(
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.background,
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                contentPadding = PaddingValues(vertical = 15.dp),
                onClick = { dialogStateChange(PlannerDialogType.CalendarDetails(date = date, type = type, mode = CalendarDialogMode.EDIT)) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 15.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "РЕДАКТИРОВАТЬ", fontWeight = FontWeight.Bold)
            }
        } else {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 15.dp)
                    .fillMaxWidth()
            ) {
                Button(
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colorScheme.background,
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    contentPadding = PaddingValues(vertical = 15.dp),
                    onClick = {
                        viewModel.updateEntity(type, curEntity?.copy(title = descriptionText) ?: PlanCalendarEntityDomain(title = descriptionText, id = 0, date = date))
                        dialogStateChange(PlannerDialogType.CalendarDetails(date = date, type = type, mode = CalendarDialogMode.VIEW))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "СОХРАНИТЬ", fontWeight = FontWeight.Bold)
                }
                TextButton(
                    onClick = { if (curEntity != null) viewModel.deleteEntity(type, curEntity) },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Text(
                        text = "ОЧИСТИТЬ ВСЁ",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/*****************************************************************
 * Private functions
 ****************************************************************/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarTypeSelector(currentViewType: CalendarType, onViewTypeChanged: (CalendarType) -> Unit, modifier: Modifier = Modifier) {
    SingleChoiceSegmentedButtonRow(
        modifier = modifier
    ) {
        CalendarType.entries.forEach { viewType ->
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
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(top = 5.dp)
                    .padding(bottom = 5.dp),
            ) {
                Text(text = when (viewType) {
                        CalendarType.DAYS   -> "Дни"
                        CalendarType.MONTHS -> "Месяцы"
                        CalendarType.YEARS  -> "Годы"
                    }
                )
            }
        }
    }
}

@Composable
private fun DaysCalendar(viewModel: PlanCalendarViewModel, dialogStateChange: (PlannerDialogType) -> Unit) {
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
                    .clickable { dialogStateChange(PlannerDialogType.CalendarDetails(date = curLocalDate, type = CalendarType.DAYS, mode = CalendarDialogMode.VIEW)) }
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
private fun MonthsCalendar(viewModel: PlanCalendarViewModel, dialogStateChange: (PlannerDialogType) -> Unit) {
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
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .aspectRatio(1.6f)
                        .clip(RoundedCornerShape(15.dp))
                        .background(
                            color = if (month == currentMonth) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = RoundedCornerShape(15.dp)
                        )
                        .clickable { dialogStateChange(PlannerDialogType.CalendarDetails(date = curLocalDate, type = CalendarType.MONTHS, mode = CalendarDialogMode.VIEW)) }
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
private fun YearsCalendar(viewModel: PlanCalendarViewModel, dialogStateChange: (PlannerDialogType) -> Unit) {
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
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .aspectRatio(1.6f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            color = if (year == currentYear) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { dialogStateChange(PlannerDialogType.CalendarDetails(date = curLocalDate, type = CalendarType.YEARS, mode = CalendarDialogMode.VIEW)) }
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
 * Classes
 ****************************************************************/
/*****************************************************************
 * Preview
 ****************************************************************/