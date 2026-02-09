/*****************************************************************
 *  Plan view ui
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.view.plan

/*****************************************************************
 * Imported packages
 ****************************************************************/
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.planote.BackgroundDark
import com.example.planote.view.plan.component.HeaderBlock
import com.example.planote.view.plan.component.TodayBlock
import com.example.planote.view.plan.component.WeekBlock
import com.example.planote.view.plan.component.calendar.CalendarBlock
import com.example.planote.view.plan.component.calendar.CalendarDialog
import com.example.planote.viewModel.plan.PlanCalendarEntityDomain
import com.example.planote.viewModel.plan.PlanCalendarType
import java.time.LocalDate

/*****************************************************************
 * Variables, data, enum
 ****************************************************************/
val PlanColorOnSurface = BackgroundDark.copy(alpha = 0.6f)

sealed class PlannerDialogType{

    data object None : PlannerDialogType()

    data class WeekDetails(
        val entity: PlanCalendarEntityDomain,
    ) : PlannerDialogType()

    data class CalendarDetails(
        val entity: PlanCalendarEntityDomain,
        val type: PlanCalendarType,
        val mode: CalendarDialogMode
    ) : PlannerDialogType()

    data class TaskDetails(
        val date: LocalDate,
    ) : PlannerDialogType()
}

enum class CalendarDialogMode { VIEW, EDIT }

/*****************************************************************
 * Interfaces
 ****************************************************************/
/*****************************************************************
 * Top Level Functions
 ****************************************************************/
@Composable
fun PlannerPage() {
    var dialogState by remember {mutableStateOf<PlannerDialogType>(PlannerDialogType.None)}
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        item { HeaderBlock() }
        item { TodayBlock() }
        item { WeekBlock() }
        item { CalendarBlock{state -> dialogState = state} }
    }
    when (val curState = dialogState) {
        is PlannerDialogType.None -> Unit
        is PlannerDialogType.WeekDetails -> Unit
        is PlannerDialogType.CalendarDetails -> CalendarDialog(entity = curState.entity, type = curState.type, mode = curState.mode){state -> dialogState = state}
        is PlannerDialogType.TaskDetails -> Unit
    }
}

@Preview
@Composable
fun previewPlannerPage(){
    PlannerPage()
}


/*****************************************************************
 * Classes
 ****************************************************************/


/*****************************************************************
 * Preview
 ****************************************************************/

