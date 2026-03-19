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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.planote.view.plan.PlannerDialogType
import com.example.planote.viewModel.plan.PlanCalendarLoadingStatus
import com.example.planote.viewModel.plan.PlanWeekDayEntityDomain
import com.example.planote.viewModel.plan.PlanWeekDayTaskEntityDomain
import com.example.planote.viewModel.plan.PlanWeekEntityDomain
import com.example.planote.viewModel.plan.PlanWeekLoadingStatus
import com.example.planote.viewModel.plan.PlanWeekViewModel

/*****************************************************************
 * Variables, data, enum
 ****************************************************************/
data class WeekDialogDayLocal(
    val dayOrigin: PlanWeekDayEntityDomain = PlanWeekDayEntityDomain(),
    val dayLocal: PlanWeekDayEntityDomain = PlanWeekDayEntityDomain(),

    val tasksOrigin: List<PlanWeekDayTaskEntityDomain> = emptyList(),
    val tasksLocal: List<PlanWeekDayTaskEntityDomain> = emptyList(),

    val taskEditOrigin: PlanWeekDayTaskEntityDomain = PlanWeekDayTaskEntityDomain(),
    val taskEditLocal: PlanWeekDayTaskEntityDomain = PlanWeekDayTaskEntityDomain(),

    val loadingStatus: PlanWeekLoadingStatus = PlanWeekLoadingStatus.IDLE,
    val savingStatus: PlanWeekLoadingStatus = PlanWeekLoadingStatus.IDLE,
    val deletingStatus: PlanWeekLoadingStatus = PlanWeekLoadingStatus.IDLE
)

enum class WeekDialogDayMode { VIEW, EDIT, TASK }

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
fun WeekDialogDay(viewModel: PlanWeekViewModel = hiltViewModel(), day: PlanWeekDayEntityDomain, mode: WeekDialogDayMode, dialogStateChange: (PlannerDialogType) -> Unit) {
    val dataState by viewModel.dataState.collectAsStateWithLifecycle()
    val _day = dataState.data.find{ it.isToggle }?.days?.find { it.num == day.num } ?: PlanWeekDayEntityDomain(num = day.num)

    var localState by remember(_day) { mutableStateOf(WeekDialogDayLocal(dayOrigin = _day, dayLocal = _day)) }

    Dialog(onDismissRequest = { dialogStateChange(PlannerDialogType.None) }) {
        Card(
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.width(340.dp).height(650.dp)
        ) {
            AnimatedContent(
                targetState = mode,
                label = "Dialog mode transition",
                transitionSpec = {
                    fadeIn(animationSpec = tween(220, easing = LinearEasing)) togetherWith
                    fadeOut(animationSpec = tween(180, easing = LinearEasing))
                },
            ) {
                currentMode -> when(currentMode){
                    WeekDialogDayMode.VIEW -> WeekDialogViewContent(localState = localState, dialogStateChange = dialogStateChange){transform -> localState = localState.run(transform)}
                    WeekDialogDayMode.EDIT -> WeekDialogEditContent(localState = localState, dialogStateChange = dialogStateChange){transform -> localState = localState.run(transform)}
                    WeekDialogDayMode.TASK -> WeekDialogTaskContent(localState = localState, dialogStateChange = dialogStateChange){transform -> localState = localState.run(transform)}
                }
            }
        }
    }
}

@Composable
fun WeekDialogDayLoading(status: PlanCalendarLoadingStatus, onNextContent: @Composable () -> Unit){
    when(status){
        PlanCalendarLoadingStatus.IDLE,  PlanCalendarLoadingStatus.PROC -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp)
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        PlanCalendarLoadingStatus.DONE -> onNextContent()
    }
}
