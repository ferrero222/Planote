/*****************************************************************
 *  Package for main screen with circular pager
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.view.plan.component.week

/*****************************************************************
 * Imported packages
 ****************************************************************/
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.planote.PreviewContainer
import com.example.planote.R
import com.example.planote.isLandscape
import com.example.planote.viewModel.plan.PlanWeekDialogMode
import com.example.planote.viewModel.plan.PlanWeekDialogPlanDataHolder
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
private fun WeekDialogPlanAddContent(
    dialogState: PlanWeekDialogPlanDataHolder,
    loading: PlanWeekLoading,
    onDismissClick: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
) {
    WeekLoading(loading) {
        Column(
            verticalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier.fillMaxSize().padding(if(!isLandscape()) 17.dp else 10.dp),
        ) {
            WeekDialogPlanAddContentHeader(
                onDismissClick = onDismissClick,
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(15.dp),
                modifier = Modifier.weight(1f).fillMaxWidth(),
            ) {
                WeekDialogPlanAddContentTitle(
                    week = dialogState.editWeek,
                    onTitleChange = onTitleChange,
                )
                WeekDialogPlanAddContentDescription(
                    week = dialogState.editWeek,
                    onDescChange = onDescChange,
                )
            }
            WeekDialogPlanAddContentFooter(
                onSave = onSave,
                onCancel = onCancel,
            )
        }
    }
}

@Composable
private fun WeekDialogPlanAddContentHeader(
    onDismissClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ){
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = { onDismissClick() },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = stringResource(R.string.dialog_back),
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(17.dp).padding(bottom = 4.dp)
                )
            }
            Text(
                text = stringResource(R.string.week_plan_add_editing),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                modifier = Modifier.align(Alignment.Center)
            )
            Text(
                text = "// EDIT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
private fun WeekDialogPlanAddContentTitle(
    week: PlanWeekDomain,
    onTitleChange: (String) -> Unit
){
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.week_plan_add_title),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = week.title ?: "",
            placeholder = { Text(text = stringResource(R.string.week_plan_add_title_hint), color = MaterialTheme.colorScheme.onSurface) },
            onValueChange = onTitleChange,
            textStyle = TextStyle(fontSize = 15.sp),
            shape = RectangleShape,
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
                    contentDescription = stringResource(R.string.dialog_edit),
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
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.week_plan_add_description),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = week.description ?: "",
            placeholder = { Text(text = stringResource(R.string.week_plan_add_description_hint), color = MaterialTheme.colorScheme.onSurface) },
            onValueChange = onDescChange,
            textStyle = TextStyle(fontSize = 15.sp),
            shape = RectangleShape,
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
                    contentDescription = stringResource(R.string.dialog_edit),
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
            shape = RectangleShape,
            onClick = { onCancel() },
            contentPadding = PaddingValues(vertical = 15.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.week_plan_add_cancel),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                fontWeight = FontWeight.Medium,
            )
        }
        Button(
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colorScheme.background,
                containerColor = MaterialTheme.colorScheme.primary
            ),
            contentPadding = PaddingValues(vertical = 15.dp),
            onClick = { onSave() },
            modifier = Modifier.fillMaxWidth().shadowGlow(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f), offsetX = 0.dp, offsetY = 0.dp, blurRadius = 17.dp)

        ) {
            Text(text = stringResource(R.string.week_plan_add_save), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun WeekDialogPlanAddAlertHandler(
    planDialogAlert: WeekDialogPlanAddAlert,
    viewModel: PlanWeekViewModel,
    dialogStateChange: (PlanWeekDialogMode) -> Unit,
    planDialogState: PlanWeekDialogPlanDataHolder,
    onDismiss: () -> Unit
) {
    when(planDialogAlert) {
        is WeekDialogPlanAddAlert.DismissChanges -> {
            WeekAlert(
                title = stringResource(R.string.week_plan_add_back_title),
                description = stringResource(R.string.dialog_unsaved_changes),
                confirmText = stringResource(R.string.week_plan_add_back_confirm),
                dismissText = stringResource(R.string.dialog_cancel),
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
                title = stringResource(R.string.week_plan_add_discard_title),
                description = stringResource(R.string.dialog_unsaved_changes),
                confirmText = stringResource(R.string.week_plan_add_discard_confirm),
                dismissText = stringResource(R.string.week_plan_add_return),
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
    val dialogState by viewModel.dialogPlanState.collectAsStateWithLifecycle()
    var planDialogAlert by remember { mutableStateOf<WeekDialogPlanAddAlert>(WeekDialogPlanAddAlert.None) }

    WeekDialogPlanAddContent(
        dialogState = dialogState,
        loading = dialogState.loading,
        onDismissClick = { planDialogAlert = WeekDialogPlanAddAlert.DismissChanges },
        onTitleChange = { newTitle -> viewModel.updateEditWeek(dialogState.editWeek.copy(title = newTitle)) },
        onDescChange = { newDesc -> viewModel.updateEditWeek(dialogState.editWeek.copy(description = newDesc)) },
        onSave = {
            viewModel.saveEditWeek()
            dialogStateChange(PlanWeekDialogMode.PLANCHANGE)
        },
        onCancel = { planDialogAlert = WeekDialogPlanAddAlert.DiscardChanges },
    )

    WeekDialogPlanAddAlertHandler(
        planDialogAlert = planDialogAlert,
        viewModel = viewModel,
        dialogStateChange = dialogStateChange,
        planDialogState = dialogState,
        onDismiss = { planDialogAlert = WeekDialogPlanAddAlert.None }
    )
}

/*****************************************************************
 * Preview functions
 ****************************************************************/
@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun WeekDialogPlanAddContentPreview(
    editWeek: PlanWeekDomain = PlanWeekDomain(
        id = 1,
        title = "План на неделю",
        description = "Описание плана на неделю"
    ),
    loading: PlanWeekLoading = PlanWeekLoading.Idle
) {
    PreviewContainer{
        WeekDialogCard {
            WeekDialogPlanAddContent(
                dialogState = PlanWeekDialogPlanDataHolder(editWeek = editWeek),
                loading = loading,
                onDismissClick = {},
                onTitleChange = {},
                onDescChange = {},
                onSave = {},
                onCancel = {},
            )
        }
    }
}
