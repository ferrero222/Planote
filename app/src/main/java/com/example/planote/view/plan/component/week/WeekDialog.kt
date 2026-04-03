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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.planote.viewModel.plan.PlanWeekDialogMode
import com.example.planote.viewModel.plan.PlanWeekLoading

/*****************************************************************
 * Variables, data, enum
 ****************************************************************/
/*****************************************************************
 * Interfaces
 ****************************************************************/
/*****************************************************************
 * Private functions
 ****************************************************************/
/*****************************************************************
 * Public functions
 ****************************************************************/
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WeekDialog(dialogState : PlanWeekDialogMode, dialogStateChange: (PlanWeekDialogMode) -> Unit) {
    if(dialogState != PlanWeekDialogMode.IDLE) {
        Dialog(onDismissRequest = { dialogStateChange(PlanWeekDialogMode.IDLE) }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .width(340.dp)
                    .height(650.dp)
                    .border(width = 1.dp, shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.17f))
            ) {
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
}

@Composable
fun WeekDialogLoading(
    status: PlanWeekLoading,
    onContent: @Composable () -> Unit
) {
    val isLoading = when (status) {
        is PlanWeekLoading.Loading,
        is PlanWeekLoading.Saving,
        is PlanWeekLoading.Deleting -> true
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
                modifier = Modifier.fillMaxWidth().padding(top = 50.dp).height(100.dp),
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
