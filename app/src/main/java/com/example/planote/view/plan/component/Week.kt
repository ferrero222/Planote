/*****************************************************************
 *  Package for server view
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.view.plan.component

/*****************************************************************
 * Imported packages
 ****************************************************************/
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.planote.viewModel.plan.PlanCalendarViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import kotlin.random.Random

/*****************************************************************
 * Top Level Functions
 ****************************************************************/
@Composable
fun WeekBlock(viewModel: PlanCalendarViewModel = hiltViewModel()) {
    val today = LocalDate.now()
    val startOfWeek = today.with(DayOfWeek.MONDAY)
    val weekDays = (0..6).map { startOfWeek.plusDays(it.toLong()) }

    Column() {
        Text(
            modifier = Modifier.padding(12.dp),
            text = "Еженедельно",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .clip(RoundedCornerShape(12.dp)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            LazyRow(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(weekDays) { date ->
                    val isToday = date == today
                    val containerColor =
                        if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                    val textColor =
                        if (isToday) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    val dayOfWeekShort =
                        date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                            .replace(".", "")
                    val dayOfMonth = date.dayOfMonth
                    val monthName = date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                        .replaceFirstChar { it.uppercase() }.replace(".", "")
                    Card(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .width(100.dp)
                            .height(160.dp)
                            .clickable { /* TODO: обработка клика */ },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = containerColor)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = dayOfWeekShort,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = textColor,
                                textAlign = TextAlign.Center,
                            )
                            Text(
                                text = "$dayOfMonth $monthName",
                                style = MaterialTheme.typography.bodyMedium,
                                color = textColor,
                                textAlign = TextAlign.Center,
                            )
                            Column() {
                                Text(
                                    text = "Задача ${Random.nextInt(1, 10)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = textColor.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = "Задача ${Random.nextInt(1, 10)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = textColor.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = "Задача ${Random.nextInt(1, 10)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = textColor.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
