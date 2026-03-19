/*****************************************************************
 *  Package for server view
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.view.plan.component.week

/*****************************************************************
 * Imported packages
 ****************************************************************/
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.CalendarViewWeek
import androidx.compose.material.icons.filled.ChangeHistory
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.SsidChart
import androidx.compose.material.icons.filled.WebStories
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.planote.view.plan.PlanColorOnSurface
import com.example.planote.view.plan.PlannerDialogType
import com.example.planote.viewModel.plan.PlanWeekDayEntityDomain
import com.example.planote.viewModel.plan.PlanWeekViewModel
import me.trishiraj.shadowglow.shadowGlow
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

/*****************************************************************
 * Top Level Functions
 ****************************************************************/
@Composable
fun WeekBlock(viewModel: PlanWeekViewModel = hiltViewModel(), dialogStateChange: (PlannerDialogType) -> Unit) {
    val today = LocalDate.now()
    val weekDays = (0..6).map { today.with(DayOfWeek.MONDAY).plusDays(it.toLong()) }

    val dataState by viewModel.dataState.collectAsStateWithLifecycle()
    val weekBd = dataState.data.find{it.isToggle}

    val todayIndex = weekDays.indexOfFirst { it == today }
    val lazyListState = rememberLazyListState()
    LaunchedEffect(todayIndex) { lazyListState.animateScrollToItem(index = todayIndex) }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp).clip(RoundedCornerShape(20.dp)),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 25.dp)
        ) {
            Text(
                text = "НЕДЕЛЬНЫЙ ПЛАН",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 20.dp, bottom = 5.dp)
            )
            LazyRow(
                state = lazyListState,
                modifier = Modifier.padding(top = 5.dp, bottom = 15.dp),
            ) {
                itemsIndexed(weekDays) { index, date ->
                    val isToday = date == today

                    val containerColor = if (isToday) MaterialTheme.colorScheme.primary else PlanColorOnSurface
                    val textColor = if (isToday) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onSurface

                    val dayBd = weekBd?.days?.find{it.num == index}
                    val dayTasks = dayBd?.tasks ?: emptyList()
                    val dayTasksProgress = if(dayTasks.isNotEmpty()) (dayTasks.count{it.isDone}.toDouble() / dayTasks.size.toDouble()) else 0

                    val iconColor = lerp(
                        start = if(isToday) MaterialTheme.colorScheme.background.copy(0.2f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f),
                        stop = if(isToday) MaterialTheme.colorScheme.background.copy(0.8f) else MaterialTheme.colorScheme.primary.copy(0.8f),
                        fraction = dayTasksProgress.toFloat()
                    )
                    val icon = when (date.dayOfWeek.value) {
                        1 -> Icons.Default.BarChart           //Mon
                        2 -> Icons.Default.BlurOn             //Tue
                        3 -> Icons.Default.FormatAlignCenter  //Wen
                        4 -> Icons.Default.ChangeHistory      //Thu
                        5 -> Icons.Default.SsidChart          //Fri
                        6 -> Icons.Default.WebStories         //Sat
                        7 -> Icons.Default.GraphicEq          //Sun
                        else -> {}
                    }

                    Card(
                        onClick = {
                            if(weekBd != null) dialogStateChange(PlannerDialogType.WeekDayDetails(day = dayBd ?: PlanWeekDayEntityDomain(num = index), mode = WeekDialogDayMode.VIEW))
                            else dialogStateChange(PlannerDialogType.WeekPlanDetails(mode = WeekDialogPlanMode.CHANGE))
                        },
                        colors = CardDefaults.cardColors(containerColor = containerColor),
                        shape = RoundedCornerShape(25.dp),
                        modifier = Modifier
                            .width(100.dp)
                            .height(160.dp)
                            .padding(10.dp)
                            .shadowGlow(
                                color = if(isToday) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent,
                                offsetX = 0.dp,
                                offsetY = 0.dp,
                                blurRadius = 15.dp
                            )
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize().padding(20.dp),
                            ) {
                            Text(
                                text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).replace(".", ""),
                                color = textColor.copy(0.5f),
                                textAlign = TextAlign.Center,
                            )
                            Text(
                                text = "$date.dayOfMonth",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = textColor,
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp
                            )
                            Icon(
                                imageVector = icon as ImageVector,
                                contentDescription = "Прогресс за ${date.dayOfMonth}",
                                tint = iconColor,
                                modifier = Modifier.size(35.dp).padding(top = 10.dp)
                            )
                        }
                    }
                }
            }
            Button(
                onClick = { dialogStateChange(PlannerDialogType.WeekPlanDetails(mode = WeekDialogPlanMode.CHANGE)) },
                colors = ButtonDefaults.buttonColors(PlanColorOnSurface),
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp, bottom = 20.dp)
                    .height(48.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.CalendarViewWeek,
                        contentDescription = "ПОМЕНЯТЬ ПЛАН",
                        modifier = Modifier
                            .padding(end = 5.dp).size(18.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = "ПОМЕНЯТЬ ПЛАН",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}
