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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
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
import com.example.planote.viewModel.plan.PlanCalendarEntityDomain
import com.example.planote.viewModel.plan.PlanCalendarLoadingStatus
import com.example.planote.viewModel.plan.PlanCalendarTaskDomain
import com.example.planote.viewModel.plan.PlanCalendarType
import com.example.planote.viewModel.plan.PlanCalendarViewModel

/*****************************************************************
 * Variables, data, enum
 ****************************************************************/
data class CalendarDialogLocal(
    val entityOrigin: PlanCalendarEntityDomain = PlanCalendarEntityDomain(),
    val entityLocal: PlanCalendarEntityDomain = PlanCalendarEntityDomain(),
    val tasksOrigin: List<PlanCalendarTaskDomain> = emptyList(),
    val tasksLocal: List<PlanCalendarTaskDomain> = emptyList(),
    val taskEdit: PlanCalendarTaskDomain = PlanCalendarTaskDomain(),
    val loadingStatus: PlanCalendarLoadingStatus = PlanCalendarLoadingStatus.IDLE,
    val savingStatus: PlanCalendarLoadingStatus = PlanCalendarLoadingStatus.IDLE,
    val deletingStatus: PlanCalendarLoadingStatus = PlanCalendarLoadingStatus.IDLE
)

enum class CalendarDialogMode { VIEW, EDIT, TASK }

/*****************************************************************
 * Interfaces
 ****************************************************************/
/*****************************************************************
 * Private functions
 ****************************************************************/
/*****************************************************************
 * Public functions
 ****************************************************************/
/**
 *   When we get to dialog window at the first time we will get VIEW mode and local copy
 * of the entity and tasks which for this window was opened. So, dialog gets local and
 * original data but works only with local. This local data stores in localState and passes through
 * each mode back and forward with all changes. Local data resets to original data only when we
 * getting back to VIEW mode without saving them. In EDIT mode we have opportunity to save local
 * data to BD and back to VIEW mode. WE ONLY WORK WITH LOCAL DATA AS A CACHE. All processes of
 * working with BD executes in coroutines with variable status for waiting result.
 *   There is unique situation when we are saving empty entity which does`nt exist in BD yet.
 * After saving it, entity should get correct bd ID to be able to work with bd again if we don`t
 * close dialog after saving, so wee need somehow gets back to VIEW mode ang get new ID of this
 * entity from BD and save it in original and local data storages. So, for this we subscribed
 * to dataState which holds all new entity data from bd, when it changes we gets that and check if
 * entity in dataState is the same as entity of this function, if not, save new instance and reset
 * localState. Thus, we actually check if input original entity is correct and the same as in BD
 * and didn`t change while we works with that, if changed get new instance from _entity.
 **/
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CalendarDialog(viewModel: PlanCalendarViewModel = hiltViewModel(), entity: PlanCalendarEntityDomain, type: PlanCalendarType, mode: CalendarDialogMode, dialogStateChange: (PlannerDialogType) -> Unit) {
    val dataState by viewModel.dataState.collectAsStateWithLifecycle()
    val _entity = when(type){
        PlanCalendarType.YEARS  -> dataState.years.find  { it.date == entity.date } ?: PlanCalendarEntityDomain(date = entity.date)
        PlanCalendarType.DAYS   -> dataState.days.find   { it.date == entity.date } ?: PlanCalendarEntityDomain(date = entity.date)
        PlanCalendarType.MONTHS -> dataState.months.find { it.date == entity.date } ?: PlanCalendarEntityDomain(date = entity.date)
    }
    var localState by remember(_entity.date, _entity.id) { mutableStateOf(CalendarDialogLocal(entityOrigin = _entity, entityLocal = _entity)) }

    Dialog(onDismissRequest = { dialogStateChange(PlannerDialogType.None) }) {
        Card(
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .requiredWidth(340.dp)
                .requiredHeight(650.dp)
        ) {
            AnimatedContent(
                targetState = mode,
                label = "Dialog mode transition",
                transitionSpec = {
                    fadeIn(
                        animationSpec = tween(220, easing = LinearEasing)
                    ) togetherWith fadeOut(
                        animationSpec = tween(180, easing = LinearEasing)
                    )
                },
            ) {
                currentMode -> when(currentMode){
                    CalendarDialogMode.VIEW -> CalendarDialogViewContent(localState = localState, type = type, dialogStateChange = dialogStateChange){transform -> localState = localState.run(transform)}
                    CalendarDialogMode.EDIT -> CalendarDialogEditContent(localState = localState, type = type, dialogStateChange = dialogStateChange){transform -> localState = localState.run(transform)}
                    CalendarDialogMode.TASK -> Unit
                }
            }
        }
    }
}

@Composable
fun CalendarLoading(status: PlanCalendarLoadingStatus, onNextContent: @Composable () -> Unit){
    when(status){
        PlanCalendarLoadingStatus.IDLE,  PlanCalendarLoadingStatus.PROC -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
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
