/*****************************************************************
 *  Package for main screen with circular pager
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.view.plan.component.calendar

/*****************************************************************
 * Imported packages
 ****************************************************************/
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = { onDismissClick() },
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 10.dp)
                .size(32.dp)
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
        Text(
            text = entity.title ?:  "Нет данных" ,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun CalendarDialogViewContentTasks(tasks: List<PlanCalendarTaskDomain>){
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
        Text(
            text = "ЗАДАЧИ",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )

        if(!tasks.isEmpty()) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .heightIn(max = 200.dp)
                    .fillMaxWidth()
            ) {
                items(tasks, key = { it.id }) { task ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = task.isDone,
                            onCheckedChange = {},
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary, uncheckedColor = Color.Gray)
                        )
                        Text(
                            text = task.title ?: "Нет описания",
                            textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None,
                            color = if (task.isDone) Color.Gray else MaterialTheme.colorScheme.onSurface,
                            fontSize = 15.sp,
                            modifier = Modifier
                                .weight(1f)
                                .padding(top = 4.dp)
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
            Box(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    CalendarDialogViewContentDescription(entity = localState.entityLocal)
                    CalendarDialogViewContentTasks(tasks = localState.tasksLocal,)
                }
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

