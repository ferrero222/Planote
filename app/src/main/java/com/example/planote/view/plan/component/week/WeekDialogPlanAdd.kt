/*****************************************************************
 *  Package for main screen with circular pager
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.view.plan.component.week

/*****************************************************************
 * Imported packages
 ****************************************************************/
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.planote.DarkColorScheme
import com.example.planote.MyAppFont
import com.example.planote.viewModel.plan.PlanWeekDialogMode
import com.example.planote.viewModel.plan.PlanWeekDomain
import com.example.planote.viewModel.plan.PlanWeekLoading
import com.example.planote.viewModel.plan.PlanWeekViewModel
import me.trishiraj.shadowglow.shadowGlow

/*****************************************************************
 * Variables, data, enum
 ****************************************************************/
sealed class WeekDialogPlanAddAlert {
    object None : WeekDialogPlanAddAlert()
    object DismissChanges : WeekDialogPlanAddAlert()
    object DiscardChanges : WeekDialogPlanAddAlert()
}

/*****************************************************************
 * Interfaces
 ****************************************************************/
/*****************************************************************
 * Private functions
 ****************************************************************/
@Composable
private fun WeekDialogPlanAddContentHeader(
    onDismissClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = { onDismissClick() },
            modifier = Modifier.align(Alignment.CenterStart).size(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBackIosNew,
                contentDescription = "Назад",
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
        Text(
            text = "РЕДАКТИРОВАНИЕ",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.align(Alignment.Center)
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .background(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(10.dp)
                )

        ) {
            Text(
                text = "EDIT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
        }
    }
}

@Composable
private fun WeekDialogPlanAddContentTitle(
    week: PlanWeekDomain,
    onTitleChange: (String) -> Unit
){
    Column(modifier = Modifier.fillMaxWidth()) { //Description
        Text(
            text = "ЗАГОЛОВОК",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = week.title ?: "",
            placeholder = { Text(text = "Введите заголовок", color = MaterialTheme.colorScheme.onSurface) },
            onValueChange = onTitleChange,
            textStyle = TextStyle(fontSize = 15.sp),
            shape = RoundedCornerShape(5.dp),
            maxLines = 5,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                unfocusedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
            ),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Редактировать",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(15.dp)
                )
            }
        )
    }
}

@Composable
private fun WeekDialogPlanAddContentDescription(
    week: PlanWeekDomain,
    onDescChange: (String) -> Unit
){
    Column(modifier = Modifier.fillMaxWidth()) { //Description
        Text(
            text = "ОПИСАНИЕ",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = week.description ?: "",
            placeholder = { Text(text = "Введите описание", color = MaterialTheme.colorScheme.onSurface) },
            onValueChange = onDescChange,
            textStyle = TextStyle(fontSize = 15.sp),
            shape = RoundedCornerShape(5.dp),
            maxLines = 5,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                unfocusedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
            ),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Редактировать",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(15.dp)
                )
            }
        )
    }
}

@Composable
private fun WeekDialogPlanAddContentFooter(
    onSave: () -> Unit,
    onCancel: () -> Unit
){
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextButton(
            shape = RoundedCornerShape(10.dp),
            onClick = { onCancel() },
            contentPadding = PaddingValues(vertical = 15.dp),
            modifier = Modifier.fillMaxWidth()
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
            contentPadding = PaddingValues(vertical = 15.dp),
            onClick = { onSave() },
            modifier = Modifier.fillMaxWidth().shadowGlow(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f), offsetX = 0.dp, offsetY = 0.dp, blurRadius = 17.dp)

        ) {
            Text(text = "ДОБАВИТЬ", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun WeekDialogPlanAddAlertHandler(
    planDialogAlert: WeekDialogPlanAddAlert,
    viewModel: PlanWeekViewModel,
    dialogStateChange: (PlanWeekDialogMode) -> Unit,
    planDialogState: com.example.planote.viewModel.plan.PlanWeekDialogPlanDataHolder,
    onDismiss: () -> Unit
) {
    when(planDialogAlert) {
        is WeekDialogPlanAddAlert.DismissChanges -> {
            WeekAlert(
                title = "Вернуться назад?",
                description = "Несохранённые изменения будут потеряны",
                confirmText = "Вернуться",
                dismissText = "Отменить",
                onConfirm = {
                    onDismiss()
                    viewModel.discardEditWeek()
                    if(planDialogState.weeks.isEmpty()) dialogStateChange(PlanWeekDialogMode.IDLE) else dialogStateChange(PlanWeekDialogMode.PLANCHANGE)
                },
                onDismiss = {
                    onDismiss()
                }
            )
        }
        is WeekDialogPlanAddAlert.DiscardChanges -> {
            WeekAlert(
                title = "Отменить изменения?",
                description = "Несохранённые изменения будут потеряны",
                confirmText = "Отменить",
                dismissText = "Вернуться",
                onConfirm = {
                    onDismiss()
                    viewModel.discardEditWeek()
                    if(planDialogState.weeks.isEmpty()) dialogStateChange(PlanWeekDialogMode.IDLE) else dialogStateChange(PlanWeekDialogMode.PLANCHANGE)
                },
                onDismiss = {
                    onDismiss()
                }
            )
        }
        is WeekDialogPlanAddAlert.None -> { }
    }
}

/*****************************************************************
 * Public functions
 ****************************************************************/
@Composable
fun WeekDialogPlanAddContent(
    viewModel: PlanWeekViewModel = hiltViewModel(),
    dialogStateChange: (PlanWeekDialogMode) -> Unit
) {
    val planDialogState by viewModel.dialogPlanState.collectAsStateWithLifecycle()
    var planDialogAlert by remember { mutableStateOf<WeekDialogPlanAddAlert>(WeekDialogPlanAddAlert.None) }
    WeekLoading(planDialogState.loading) {
        Column(
            verticalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier.fillMaxSize().padding(25.dp),
        ) {
            WeekDialogPlanAddContentHeader(
                onDismissClick = {
                    planDialogAlert = WeekDialogPlanAddAlert.DismissChanges
                }
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(15.dp),
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) {
                WeekDialogPlanAddContentTitle(
                    week = planDialogState.editWeek,
                    onTitleChange = { newTitle ->
                        viewModel.updateEditWeek(planDialogState.editWeek.copy(title = newTitle))
                    }
                )
                WeekDialogPlanAddContentDescription(
                    week = planDialogState.editWeek,
                    onDescChange = { newDesc ->
                        viewModel.updateEditWeek(planDialogState.editWeek.copy(description = newDesc))
                    }
                )
            }

            WeekDialogPlanAddContentFooter(
                onSave = {
                    viewModel.saveEditWeek()
                    dialogStateChange(PlanWeekDialogMode.PLANCHANGE)
                },
                onCancel = {
                    planDialogAlert = WeekDialogPlanAddAlert.DiscardChanges
                },
            )
        }

        WeekDialogPlanAddAlertHandler(
            planDialogAlert = planDialogAlert,
            viewModel = viewModel,
            dialogStateChange = dialogStateChange,
            planDialogState = planDialogState,
            onDismiss = { planDialogAlert = WeekDialogPlanAddAlert.None }
        )
    }
}

/*****************************************************************
 * Preview functions
 ****************************************************************/
@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun WeekDialogPlanAddContentPreview() {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = MyAppFont,
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(35.dp)){
            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .width(340.dp)
                    .height(650.dp)
                    .border(
                        width = 1.dp,
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.17f)
                    )
            ) {
                WeekLoading(PlanWeekLoading.Idle) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(15.dp),
                        modifier = Modifier.fillMaxSize().padding(25.dp),
                    ) {
                        WeekDialogPlanAddContentHeader(
                            onDismissClick = {}
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(15.dp),
                            modifier = Modifier.weight(1f).fillMaxWidth()
                        ) {
                            WeekDialogPlanAddContentTitle(
                                week = PlanWeekDomain(id = 1, title = "Неделя", description = "Описание да"),
                                onTitleChange = {}
                            )
                            WeekDialogPlanAddContentDescription(
                                week = PlanWeekDomain(id = 1, title = "Неделя", description = "Описание да"),
                                onDescChange = {}
                            )
                        }

                        WeekDialogPlanAddContentFooter(
                            onSave = {},
                            onCancel = {},
                        )
                    }
                }
            }
        }
    }
}
