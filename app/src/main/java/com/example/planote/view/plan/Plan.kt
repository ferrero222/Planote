/*****************************************************************
 *  Plan view ui
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.view.plan

/*****************************************************************
 * Imported packages
 ****************************************************************/
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.example.planote.view.plan.component.HeaderBlock
import com.example.planote.view.plan.component.TodayBlock
import com.example.planote.view.plan.component.calendar.CalendarBlock
import com.example.planote.view.plan.component.calendar.CalendarDialog
import com.example.planote.view.plan.component.week.WeekBlock
import com.example.planote.view.plan.component.week.WeekDialog
import com.example.planote.viewModel.plan.PlanCalendarDialogMode
import com.example.planote.viewModel.plan.PlanWeekDialogMode

/*****************************************************************
 * Variables, data, enum
 ****************************************************************/
/*****************************************************************
 * Interfaces
 ****************************************************************/
/*****************************************************************
 * Top Level Functions
 ****************************************************************/
@Composable
fun PlannerPage() {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        item {
            HeaderBlock()
        }
        item {
            TodayBlock()
        }
        item {
            var weekDialogState by remember{ mutableStateOf(PlanWeekDialogMode.IDLE)}
            WeekBlock{state -> weekDialogState = state}
            WeekDialog(weekDialogState){state -> weekDialogState = state}
        }
        item {
            var calendarDialogState by remember{ mutableStateOf(PlanCalendarDialogMode.IDLE)}
            CalendarBlock{state -> calendarDialogState = state}
            CalendarDialog(calendarDialogState){state -> calendarDialogState = state}
        }
    }
}


@Composable
fun PlannerBlockCard(
    content: @Composable () -> Unit,
) {
    Card(
        shape = RectangleShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, shape = RectangleShape, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
    ) {
        content()
    }
}

/*****************************************************************
 * Classes
 ****************************************************************/
/*****************************************************************
 * Preview
 ****************************************************************/

