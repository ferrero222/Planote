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
import com.example.planote.viewModel.plan.PlanCalendarDialogMode

/*****************************************************************
 * Variables, data, enum
 ****************************************************************/
/*****************************************************************
 * Interfaces
 ****************************************************************/
sealed class CalendarDialogAlert {
    object None : CalendarDialogAlert()
    object DismissChanges : CalendarDialogAlert()
}

/*****************************************************************
 * Private functions
 ****************************************************************/
@Composable
private fun CalendarDialogAlertHandler(
    calendarDialogAlert: CalendarDialogAlert,
    dialogStateChange: (PlanCalendarDialogMode) -> Unit,
    onDismiss: () -> Unit
) {
    when(calendarDialogAlert) {
        is CalendarDialogAlert.DismissChanges -> {
            CalendarAlert(
                title = stringResource(R.string.calendar_dialog_close_title),
                description = stringResource(R.string.dialog_unsaved_changes),
                confirmText = stringResource(R.string.calendar_dialog_close_confirm),
                dismissText = stringResource(R.string.dialog_dismiss),
                onConfirm = {
                    onDismiss()
                    dialogStateChange(PlanCalendarDialogMode.IDLE)
                },
                onDismiss = {
                    onDismiss()
                }
            )
        }
        is CalendarDialogAlert.None -> { }
    }
}

/*****************************************************************
 * Public functions
 ****************************************************************/
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CalendarDialog(
    dialogState : PlanCalendarDialogMode,
    dialogStateChange: (PlanCalendarDialogMode) -> Unit
) {
    var calendarDialogAlert by remember { mutableStateOf<CalendarDialogAlert>(CalendarDialogAlert.None) }
    if(dialogState != PlanCalendarDialogMode.IDLE) {
        Dialog(onDismissRequest = {
                when(dialogState){
                    PlanCalendarDialogMode.VIEW -> dialogStateChange(PlanCalendarDialogMode.IDLE)
                    PlanCalendarDialogMode.EDIT -> calendarDialogAlert = CalendarDialogAlert.DismissChanges
                    PlanCalendarDialogMode.TASK -> calendarDialogAlert = CalendarDialogAlert.DismissChanges
                    PlanCalendarDialogMode.IDLE -> calendarDialogAlert = CalendarDialogAlert.None
                }
            }
        ) {
            CalendarDialogCard {
                AnimatedContent(
                    targetState = dialogState,
                    label = "Dialog mode transition",
                    transitionSpec = {
                        fadeIn(animationSpec = tween(220, easing = LinearEasing)) togetherWith
                        fadeOut(animationSpec = tween(180, easing = LinearEasing))
                    },
                ) { currentMode ->
                    when (currentMode) {
                        PlanCalendarDialogMode.VIEW -> CalendarDialogViewContent(dialogStateChange = dialogStateChange)
                        PlanCalendarDialogMode.EDIT -> CalendarDialogEditContent(dialogStateChange = dialogStateChange)
                        PlanCalendarDialogMode.TASK -> CalendarDialogTaskContent(dialogStateChange = dialogStateChange)
                        PlanCalendarDialogMode.IDLE -> return@AnimatedContent
                    }
                }
            }
        }
    }

    CalendarDialogAlertHandler(
        calendarDialogAlert = calendarDialogAlert,
        dialogStateChange = dialogStateChange,
        onDismiss = { calendarDialogAlert = CalendarDialogAlert.None }
    )
}

@Composable
fun CalendarDialogCard(
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