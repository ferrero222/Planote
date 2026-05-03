/*****************************************************************
 *  Package for server view
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.view.plan.component

/*****************************************************************
 * Imported packages
 ****************************************************************/
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.planote.DarkColorScheme
import com.example.planote.R
import com.example.planote.view.plan.PlannerBlockCard
import com.example.planote.viewModel.plan.PlanTodayDataHolder
import com.example.planote.viewModel.plan.PlanTodaySource
import com.example.planote.viewModel.plan.PlanTodayTaskDomain
import com.example.planote.viewModel.plan.PlanTodayViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.Locale

/*****************************************************************
 * Constants
 ****************************************************************/
private val sectionAlpha = mapOf(
    PlanTodaySource.WEEK to 0.9f,
    PlanTodaySource.CALENDAR_DAY to 0.7f,
    PlanTodaySource.CALENDAR_MONTH to 0.5f,
    PlanTodaySource.CALENDAR_YEAR to 0.3f,
)

/*****************************************************************
 * Interfaces
 ****************************************************************/
/*****************************************************************
 * Top Level Functions
 ****************************************************************/
@Composable
fun TodayBlock(
    viewModel: PlanTodayViewModel = hiltViewModel(),
) {
    val dataState by viewModel.dataState.collectAsStateWithLifecycle()
    val today = LocalDate.now()
    val dayOfWeek = today.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()).replaceFirstChar { it.uppercaseChar() }
    val month = today.month.getDisplayName(TextStyle.FULL, Locale.getDefault()).replaceFirstChar { it.uppercaseChar() }

    PlannerBlockCard {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.today_active_schedule),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$month, $dayOfWeek, ${today.dayOfMonth}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(14.dp))

            TodaySection(
                label = stringResource(R.string.today_weekly_tasks),
                tasks = dataState.weekTasks,
                source = PlanTodaySource.WEEK,
                onDoneChanged = null,
            )
            TodaySection(
                label = stringResource(R.string.today_daily_tasks),
                tasks = dataState.dayTasks,
                source = PlanTodaySource.CALENDAR_DAY,
                onDoneChanged = { task, isDone -> viewModel.updateTaskDone(task, isDone) },
            )
            TodaySection(
                label = stringResource(R.string.today_monthly_tasks),
                tasks = dataState.monthTasks,
                source = PlanTodaySource.CALENDAR_MONTH,
                onDoneChanged = { task, isDone -> viewModel.updateTaskDone(task, isDone) },
            )
            TodaySection(
                label = stringResource(R.string.today_yearly_tasks),
                tasks = dataState.yearTasks,
                source = PlanTodaySource.CALENDAR_YEAR,
                onDoneChanged = { task, isDone -> viewModel.updateTaskDone(task, isDone) },
            )

            val hasAnyTask = dataState.weekTasks.isNotEmpty()
                || dataState.dayTasks.isNotEmpty()
                || dataState.monthTasks.isNotEmpty()
                || dataState.yearTasks.isNotEmpty()
            if (!hasAnyTask) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.today_no_tasks),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    )
                }
            }
        }
    }
}

@Composable
private fun TodaySection(
    label: String,
    tasks: List<PlanTodayTaskDomain>,
    source: PlanTodaySource,
    onDoneChanged: ((PlanTodayTaskDomain, Boolean) -> Unit)?,
) {
    if (tasks.isEmpty()) return

    val accentAlpha = sectionAlpha[source] ?: 0.6f

    Column(
        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(14.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = accentAlpha))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary.copy(alpha = accentAlpha),
            )
        }
        tasks.forEach { task ->
            TodayTaskItem(
                task = task,
                canCheck = onDoneChanged != null,
                onDoneChanged = if (onDoneChanged != null) { { isDone -> onDoneChanged(task, isDone) } } else null,
                accentAlpha = accentAlpha,
            )
        }
    }
}

@Composable
private fun TodayTaskItem(
    task: PlanTodayTaskDomain,
    canCheck: Boolean,
    onDoneChanged: ((Boolean) -> Unit)?,
    accentAlpha: Float,
) {
    val hasDescription = !task.description.isNullOrBlank()
    var expanded by remember { mutableStateOf(false) }
    val bgColor = if (task.isDone)
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f)
    else
        MaterialTheme.colorScheme.primary.copy(alpha = 0.04f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = bgColor, shape = RoundedCornerShape(6.dp))
            .padding(start = 10.dp, end = 10.dp, top = 8.dp, bottom = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = hasDescription) { expanded = !expanded }
        ) {
            if (canCheck) {
                Checkbox(
                    checked = task.isDone,
                    onCheckedChange = onDoneChanged,
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        uncheckedColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        checkmarkColor = MaterialTheme.colorScheme.surface,
                    ),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            if (task.time != null && task.time != LocalTime.MIDNIGHT) {
                Text(
                    text = "${task.time.hour.toString().padStart(2, '0')}:${task.time.minute.toString().padStart(2, '0')}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = accentAlpha),
                    modifier = Modifier.width(40.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = task.title ?: stringResource(R.string.today_no_title),
                fontSize = 14.sp,
                color = if (task.isDone) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    else MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (hasDescription) {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) stringResource(R.string.today_collapse) else stringResource(R.string.today_expand),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        AnimatedVisibility(
            visible = expanded && hasDescription,
            enter = expandVertically(expandFrom = Alignment.Top, animationSpec = tween(200)),
            exit = shrinkVertically(shrinkTowards = Alignment.Top, animationSpec = tween(200)),
        ) {
            Text(
                text = task.description ?: "",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                maxLines = 6,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, start = if (canCheck) 24.dp else 0.dp)
            )
        }
    }
    Spacer(modifier = Modifier.height(4.dp))
}

/*****************************************************************
 * Preview
 ****************************************************************/
@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun TodayBlockPreview() {
    val sampleWeekTasks = listOf(
        PlanTodayTaskDomain(id = 1, title = "Позавтракать", time = LocalTime.of(9, 0), isDone = true, source = PlanTodaySource.WEEK),
        PlanTodayTaskDomain(id = 2, title = "Встреча с командой", time = LocalTime.of(14, 0), description = "Подготовить отчет", source = PlanTodaySource.WEEK),
        PlanTodayTaskDomain(id = 3, title = "Спортзал", source = PlanTodaySource.WEEK),
    )
    val sampleDayTasks = listOf(
        PlanTodayTaskDomain(id = 4, title = "Купить продукты", description = "Молоко, хлеб, яйца", source = PlanTodaySource.CALENDAR_DAY),
        PlanTodayTaskDomain(id = 5, title = "Позвонить врачу", isDone = true, source = PlanTodaySource.CALENDAR_DAY),
    )
    val sampleMonthTasks = listOf(
        PlanTodayTaskDomain(id = 6, title = "Сдать отчет", source = PlanTodaySource.CALENDAR_MONTH),
    )
    val sampleYearTasks = listOf(
        PlanTodayTaskDomain(id = 7, title = "Подать декларацию", source = PlanTodaySource.CALENDAR_YEAR),
    )
    MaterialTheme(colorScheme = DarkColorScheme) {
        TodayBlockPreviewContent(
            dataState = PlanTodayDataHolder(
                weekTasks = sampleWeekTasks,
                dayTasks = sampleDayTasks,
                monthTasks = sampleMonthTasks,
                yearTasks = sampleYearTasks,
            )
        )
    }
}

@Composable
private fun TodayBlockPreviewContent(
    dataState: PlanTodayDataHolder,
) {
    val today = LocalDate.now()
    val dayOfWeek = today.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()).replaceFirstChar { it.uppercaseChar() }
    val month = today.month.getDisplayName(TextStyle.FULL, Locale.getDefault()).replaceFirstChar { it.uppercaseChar() }
    PlannerBlockCard {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.today_active_schedule),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$month, $dayOfWeek, ${today.dayOfMonth}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(14.dp))
            TodaySection(label = stringResource(R.string.today_weekly_tasks), tasks = dataState.weekTasks, source = PlanTodaySource.WEEK, onDoneChanged = null)
            TodaySection(label = stringResource(R.string.today_daily_tasks), tasks = dataState.dayTasks, source = PlanTodaySource.CALENDAR_DAY, onDoneChanged = null)
            TodaySection(label = stringResource(R.string.today_monthly_tasks), tasks = dataState.monthTasks, source = PlanTodaySource.CALENDAR_MONTH, onDoneChanged = null)
            TodaySection(label = stringResource(R.string.today_yearly_tasks), tasks = dataState.yearTasks, source = PlanTodaySource.CALENDAR_YEAR, onDoneChanged = null)
            val hasAny = dataState.weekTasks.isNotEmpty() || dataState.dayTasks.isNotEmpty() || dataState.monthTasks.isNotEmpty() || dataState.yearTasks.isNotEmpty()
            if (!hasAny) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.today_no_tasks),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    )
                }
            }
        }
    }
}
