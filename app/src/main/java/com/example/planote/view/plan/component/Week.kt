/*****************************************************************
 *  Package for server view
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.view.plan.component

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
import androidx.compose.foundation.lazy.items
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
import com.example.planote.view.plan.PlanColorOnSurface
import com.example.planote.viewModel.plan.PlanCalendarViewModel
import me.trishiraj.shadowglow.shadowGlow
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

/*****************************************************************
 * Top Level Functions
 ****************************************************************/
@Composable
fun WeekBlock(viewModel: PlanCalendarViewModel = hiltViewModel()) {
    val today = LocalDate.now()
    val startOfWeek = today.with(DayOfWeek.MONDAY)
    val weekDays = (0..6).map { startOfWeek.plusDays(it.toLong()) }
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
                items(weekDays) { date ->
                    val isToday = date == today
                    val containerColor = if (isToday) MaterialTheme.colorScheme.primary else PlanColorOnSurface
                    val textColor = if (isToday) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onSurface
                    val dayOfWeekShort = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).replace(".", "")
                    val dayOfMonth = date.dayOfMonth
                    val iconColor = lerp(
                        start = if(isToday) MaterialTheme.colorScheme.background.copy(0.2f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f),
                        stop = if(isToday) MaterialTheme.colorScheme.background.copy(0.8f) else MaterialTheme.colorScheme.primary.copy(0.8f),
                        fraction = 0.0f
                    )
                    val dayOfWeek = date.dayOfWeek.value
                    val icon = when (dayOfWeek) {
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
                        onClick = {/* TODO: обработка клика */},
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
                                text = dayOfWeekShort,
                                color = textColor.copy(0.5f),
                                textAlign = TextAlign.Center,
                            )
                            Text(
                                text = "$dayOfMonth",
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
                onClick = { /* TODO: действие кнопки */ },
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
