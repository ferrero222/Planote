/*****************************************************************
 *  Package for main screen with circular pager
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.view.plan.component.week

/*****************************************************************
 * Imported packages
 ****************************************************************/
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.planote.R
import com.example.planote.viewModel.plan.PlanWeekDialogMode

/*****************************************************************
 * Variables, data, enum
 ****************************************************************/
/*****************************************************************
 * Interfaces
 ****************************************************************/
sealed class WeekDialogAlert {
    object None : WeekDialogAlert()
    object DismissChanges : WeekDialogAlert()
}

/*****************************************************************
 * Private functions
 ****************************************************************/
@Composable
private fun WeekDialogAlertHandler(
    weekDialogAlert: WeekDialogAlert,
    dialogStateChange: (PlanWeekDialogMode) -> Unit,
    onDismiss: () -> Unit
) {
    when(weekDialogAlert) {
        is WeekDialogAlert.DismissChanges -> {
            WeekAlert(
                title = stringResource(R.string.week_dialog_close_title),
                description = stringResource(R.string.dialog_unsaved_changes),
                confirmText = stringResource(R.string.week_dialog_close_confirm),
                dismissText = stringResource(R.string.dialog_dismiss),
                onConfirm = {
                    onDismiss()
                    dialogStateChange(PlanWeekDialogMode.IDLE)
                },
                onDismiss = {
                    onDismiss()
                }
            )
        }
        is WeekDialogAlert.None -> { }
    }
}

/*****************************************************************
 * Public functions
 ****************************************************************/
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WeekDialog(
    dialogState : PlanWeekDialogMode,
    dialogStateChange: (PlanWeekDialogMode) -> Unit
) {
    var weekDialogAlert by remember { mutableStateOf<WeekDialogAlert>(WeekDialogAlert.None) }
    if(dialogState != PlanWeekDialogMode.IDLE) {
        Dialog(onDismissRequest = {
            when(dialogState){
                PlanWeekDialogMode.DAYVIEW -> dialogStateChange(PlanWeekDialogMode.IDLE)
                PlanWeekDialogMode.DAYEDIT -> weekDialogAlert = WeekDialogAlert.DismissChanges
                PlanWeekDialogMode.DAYTASK -> weekDialogAlert = WeekDialogAlert.DismissChanges
                PlanWeekDialogMode.PLANCHANGE -> dialogStateChange(PlanWeekDialogMode.IDLE)
                PlanWeekDialogMode.PLANADD -> weekDialogAlert = WeekDialogAlert.DismissChanges
                PlanWeekDialogMode.IDLE -> weekDialogAlert = WeekDialogAlert.None
            }
        }) {
            WeekDialogCard {
                AnimatedContent(
                    targetState = dialogState,
                    label = "Dialog mode transition",
                    transitionSpec = {
                        fadeIn(animationSpec = tween(220, easing = LinearEasing)) togetherWith
                        fadeOut(animationSpec = tween(180, easing = LinearEasing))
                    },
                ) { currentMode ->
                    when (currentMode) {
                        PlanWeekDialogMode.DAYVIEW ->    { WeekDialogDayViewContent(dialogStateChange = dialogStateChange) }
                        PlanWeekDialogMode.DAYEDIT ->    { WeekDialogDayEditContent(dialogStateChange = dialogStateChange) }
                        PlanWeekDialogMode.DAYTASK ->    { WeekDialogDayTaskContent(dialogStateChange = dialogStateChange) }
                        PlanWeekDialogMode.PLANCHANGE -> { WeekDialogPlanChangeContent(dialogStateChange = dialogStateChange) }
                        PlanWeekDialogMode.PLANADD ->    { WeekDialogPlanAddContent(dialogStateChange = dialogStateChange)}
                        PlanWeekDialogMode.IDLE ->       return@AnimatedContent
                    }
                }
            }
        }
    }

    WeekDialogAlertHandler(
        weekDialogAlert = weekDialogAlert,
        dialogStateChange = dialogStateChange,
        onDismiss = { weekDialogAlert = WeekDialogAlert.None }
    )
}

@Composable
fun WeekDialogCard(
    content: @Composable () -> Unit,
) {
    Card(
        shape = RectangleShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .width(350.dp)
            .height(650.dp)
            .border(width = 1.dp, shape = RectangleShape, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.17f))
    ) {
        content()
    }
}
