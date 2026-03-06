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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.planote.view.plan.PlannerDialogType
import com.example.planote.viewModel.plan.PlanCalendarEntityDomain
import com.example.planote.viewModel.plan.PlanCalendarTaskDomain
import com.example.planote.viewModel.plan.PlanCalendarType
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
private fun CalendarDialogTaskContentHeader(entity: PlanCalendarEntityDomain, type: PlanCalendarType, onDismissClick: () -> Unit
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
                imageVector = Icons.Filled.ArrowBackIosNew,
                contentDescription = "Назад",
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
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(15.dp)
                )
                .padding(horizontal = 12.dp, vertical = 5.dp)

        ) {
            Text(
                text = "TASK",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.background
            )
        }
    }
}

@Composable
private fun CalendarDialogTaskContentTitle(task: PlanCalendarTaskDomain, onTitleChange: (String) -> Unit){
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) { //Description
        Text(
            text = "ЗАГОЛОВОК",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        OutlinedTextField(
            value = task.title ?: "",
            placeholder = { Text(text = "", color = MaterialTheme.colorScheme.onSurface) },
            onValueChange = onTitleChange,
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
            )
        )
    }
}

@Composable
private fun CalendarDialogTaskContentDescription(task: PlanCalendarTaskDomain, onDescChange: (String) -> Unit){
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) { //Description
        Text(
            text = "ОПИСАНИЕ",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        OutlinedTextField(
            value = task.description ?: "",
            placeholder = { Text(text = "", color = MaterialTheme.colorScheme.onSurface) },
            onValueChange = onDescChange,
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
            )
        )
    }
}

@Composable
private fun CalendarDialogTaskContentCheckbox(task: PlanCalendarTaskDomain, onCheckChange: (Boolean) -> Unit){
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) { //Description
        Text(
            text = "ВЫПОЛНЕНИЕ",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 5.dp)

        )
        Box(
            modifier = Modifier.size(24.dp)
        ) {
        Checkbox(
            checked = task.isDone,
            onCheckedChange = { newValue -> onCheckChange(newValue) },
            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary, uncheckedColor = Color.Gray)
        )
            }
    }
}

@Composable
private fun CalendarDialogTaskContentFooter(onSave: () -> Unit, onCancel: () -> Unit){
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextButton(
            shape = RoundedCornerShape(10.dp),
            onClick = { onCancel() },
            contentPadding = PaddingValues(vertical = 14.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "ОТМЕНИТЬ",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                fontWeight = FontWeight.Medium,
            )
        }
        Button(
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colorScheme.background,
                containerColor = MaterialTheme.colorScheme.primary
            ),
            contentPadding = PaddingValues(vertical = 14.dp),
            onClick = { onSave() },
            modifier = Modifier.fillMaxWidth().shadowGlow(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f), offsetX = 0.dp, offsetY = 0.dp, blurRadius = 17.dp)

        ) {
            Text(text = "СОХРАНИТЬ", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CalendarDialogTaskContent(
    localState: CalendarDialogLocal,
    type: PlanCalendarType,
    dialogStateChange: (PlannerDialogType) -> Unit,
    dialogLocalStateChange: (CalendarDialogLocal.() -> CalendarDialogLocal) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(vertical = 15.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        CalendarDialogTaskContentHeader(
            entity = localState.entityLocal,
            type = type,
            onDismissClick = {
                dialogStateChange(PlannerDialogType.CalendarDetails(entity = localState.entityLocal, type = type, mode = CalendarDialogMode.EDIT))
            }
        )

        Box(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                CalendarDialogTaskContentTitle(
                    task = localState.taskEditLocal,
                    onTitleChange = { newTitle ->
                        dialogLocalStateChange { copy(taskEditLocal = localState.taskEditLocal.copy(title = newTitle)) }
                    }
                )
                CalendarDialogTaskContentDescription(
                    task = localState.taskEditLocal,
                    onDescChange = { newDesc ->
                        dialogLocalStateChange {  copy(taskEditLocal = localState.taskEditLocal.copy(description = newDesc)) }
                    }
                )
                CalendarDialogTaskContentCheckbox(
                    task = localState.taskEditLocal,
                    onCheckChange = { newValue ->
                        dialogLocalStateChange { copy(taskEditLocal = localState.taskEditLocal.copy(isDone = newValue)) }
                    }
                )
            }
        }

        CalendarDialogTaskContentFooter(
            onSave = {
                if(localState.taskEditOrigin.id.toInt() == 0 && localState.taskEditOrigin.description.isNullOrBlank() && localState.taskEditOrigin.title.isNullOrBlank()){
                    dialogLocalStateChange{
                        copy( tasksLocal = tasksLocal + localState.taskEditLocal)
                    }
                }
                else dialogLocalStateChange{
                    copy(tasksLocal = tasksLocal.map{ if (it.id == localState.taskEditLocal.id) localState.taskEditLocal else it })
                }
                dialogStateChange(PlannerDialogType.CalendarDetails(entity = localState.entityLocal, type = type, mode = CalendarDialogMode.EDIT))
            },
            onCancel = {
                dialogStateChange(PlannerDialogType.CalendarDetails(entity = localState.entityLocal, type = type, mode = CalendarDialogMode.EDIT))
            },
        )
    }
}
