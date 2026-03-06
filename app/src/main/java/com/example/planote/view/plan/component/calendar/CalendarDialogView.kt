/*****************************************************************
 *  Package for main screen with circular pager
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.view.plan.component.calendar

/*****************************************************************
 * Imported packages
 ****************************************************************/
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.planote.view.plan.PlannerDialogType
import com.example.planote.viewModel.plan.PlanCalendarEntityDomain
import com.example.planote.viewModel.plan.PlanCalendarLoadingStatus
import com.example.planote.viewModel.plan.PlanCalendarTaskDomain
import com.example.planote.viewModel.plan.PlanCalendarType
import com.example.planote.viewModel.plan.PlanCalendarViewModel
import me.trishiraj.shadowglow.shadowGlow
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
private fun CalendarDialogViewContentHeader(entity: PlanCalendarEntityDomain, type: PlanCalendarType, onDismissClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = { onDismissClick() },
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 10.dp).size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Закрыть",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
        }
        val month = entity.date.month.getDisplayName(TextStyle.FULL, Locale.getDefault()).replaceFirstChar { it.uppercaseChar() }
        val textHeader = when (type) {
            PlanCalendarType.DAYS -> "$month ${entity.date.dayOfMonth}"
            PlanCalendarType.MONTHS -> "$month ${entity.date.year}"
            PlanCalendarType.YEARS -> "${entity.date.year} year"
        }
        Text(
            text = textHeader,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.align(Alignment.Center)
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 5.dp)
                .background(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(15.dp)
                )
                .padding(horizontal = 12.dp, vertical = 5.dp)

        ) {
            Text(
                text = "VIEW",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun CalendarDialogViewContentDescription(entity: PlanCalendarEntityDomain){
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) { //Description
        Text(
            text = "ОПИСАНИЕ",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
        )
        Box() {
            Text(
                text = entity.title ?: "Нет данных",
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp).verticalScroll(rememberScrollState())
            )
        }
    }
}

@Composable
private fun CalendarDialogViewContentTasks(tasks: List<PlanCalendarTaskDomain>
){
    val listState = rememberLazyListState()
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
        Text(
            text = "ЗАДАЧИ",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )

        if(tasks.isNotEmpty()) {
            Box {
                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                ) {
                    items(tasks, key = { it.id }) { task ->
                        val hasDescription = !task.description.isNullOrBlank()
                        var expandedTaskState by remember { mutableStateOf(false) }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth().clickable(enabled = hasDescription) { expandedTaskState = !expandedTaskState }
                            ) {
                                Box(
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Checkbox(
                                        checked = task.isDone,
                                        onCheckedChange = {},
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                            uncheckedColor = MaterialTheme.colorScheme.primary,
                                            checkmarkColor = MaterialTheme.colorScheme.surface
                                        )
                                    )
                                }
                                Text(
                                    text = task.title ?: "Нет описания",
                                    color = if (task.isDone) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 15.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 8.dp)
                                )
                                if (hasDescription) {
                                    Icon(
                                        imageVector = if (expandedTaskState) Icons.Default.KeyboardArrowUp
                                        else Icons.Default.KeyboardArrowDown,
                                        contentDescription = if (expandedTaskState) "Свернуть" else "Развернуть",
                                        tint = if(task.isDone) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            AnimatedVisibility(
                                visible = expandedTaskState && hasDescription,
                                enter = expandVertically(
                                    expandFrom = Alignment.Top,
                                    animationSpec = tween(durationMillis = 150)
                                ),
                                exit = shrinkVertically(
                                    shrinkTowards = Alignment.Top,
                                    animationSpec = tween(durationMillis = 150)
                                )
                            ) {
                                Text(
                                    text = task.description ?: "Нет",
                                    color = if (task.isDone) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                    fontSize = 13.sp,
                                    lineHeight = 12.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 32.dp, end = 8.dp)
                                )
                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .animateContentSize()
                ) {

                    AnimatedVisibility(
                        visible = listState.canScrollForward,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        enter = fadeIn(animationSpec = tween(150)),
                        exit = fadeOut(animationSpec = tween(150))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                            MaterialTheme.colorScheme.surface
                                        )
                                    )
                                )
                        )
                    }
                }
            }
        }
        else {
            Text(
                "Нет данных",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun CalendarDialogViewContentFooter(onEdit: () -> Unit){
    Button(
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            contentColor = MaterialTheme.colorScheme.background,
            containerColor = MaterialTheme.colorScheme.primary
        ),
        contentPadding = PaddingValues(vertical = 15.dp),
        onClick = { onEdit() },
        modifier = Modifier
            .fillMaxWidth()
            .shadowGlow(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                offsetX = 0.dp,
                offsetY = 0.dp,
                blurRadius = 17.dp
            )
    ) {
        Text(text = "РЕДАКТИРОВАТЬ", fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CalendarDialogViewContent(
    viewModel: PlanCalendarViewModel = hiltViewModel(),
    localState: CalendarDialogLocal,
    type: PlanCalendarType,
    dialogStateChange: (PlannerDialogType) -> Unit,
    dialogLocalStateChange: (CalendarDialogLocal.() -> CalendarDialogLocal) -> Unit
) {
    val loadingCoroutineScope = rememberCoroutineScope()
    viewModel.getEntityTasks(
        status = localState.loadingStatus,
        coroutineScope = loadingCoroutineScope,
        type = type,
        entity = localState.entityLocal,
        onGetTasks = { newTasks -> dialogLocalStateChange{ copy(tasksLocal = newTasks, tasksOrigin = newTasks) }  },
        onStatusChange = { newStatus -> dialogLocalStateChange{ copy(loadingStatus = newStatus)} },
    )
    CalendarLoading(localState.loadingStatus) {
        Column(
            modifier = Modifier.fillMaxSize().padding(vertical = 15.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            CalendarDialogViewContentHeader(
                entity = localState.entityLocal,
                type = type,
                onDismissClick = {dialogStateChange(PlannerDialogType.None) }
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.weight(1f).fillMaxWidth().padding(bottom = 35.dp)
            ) {
                CalendarDialogViewContentDescription(entity = localState.entityLocal)
                CalendarDialogViewContentTasks(tasks = localState.tasksLocal,)
            }

            CalendarDialogViewContentFooter(
                onEdit = {
                    dialogLocalStateChange{ copy(savingStatus = PlanCalendarLoadingStatus.IDLE, deletingStatus = PlanCalendarLoadingStatus.IDLE) }
                    dialogStateChange(PlannerDialogType.CalendarDetails(entity = localState.entityLocal, type = type, mode = CalendarDialogMode.EDIT))
                }
            )
        }
    }
}

