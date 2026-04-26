/*****************************************************************
 *  Package for server view
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.view.plan.component

/*****************************************************************
 * Imported packages
 ****************************************************************/
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.planote.DarkColorScheme
import com.example.planote.view.plan.PlannerBlockCard
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

/*****************************************************************
 * Interfaces
 ****************************************************************/
/*****************************************************************
 * Top Level Functions
 ****************************************************************/
@Composable
fun TodayBlock() {
    val today = LocalDate.now()
    val dayOfWeek = today.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()).replaceFirstChar {it.uppercaseChar()}
    val month = today.month.getDisplayName(TextStyle.FULL, Locale.getDefault()).replaceFirstChar {it.uppercaseChar()}
    PlannerBlockCard {
        Column(
            modifier = Modifier.padding(horizontal = 30.dp)
        ) {
            Text(
                text = "АКТИВНОЕ РАСПИСАНИЕ",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 20.dp, bottom = 5.dp)
            )
            Text(
                text = "$month, $dayOfWeek, ${today.dayOfMonth}",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )
            Column {
                Text(
                    text = "Задача 1",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 20.dp, bottom = 5.dp),
                )
                Text(
                    text = "Задача 1",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 20.dp, bottom = 5.dp),
                )
                Text(
                    text = "Задача 1",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 20.dp, bottom = 20.dp),
                )
            }
        }
    }
}

/*****************************************************************
 * Classes
 ****************************************************************/
/*****************************************************************
 * Preview
 ****************************************************************/
@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun TodayBlockPreview(){
    MaterialTheme( colorScheme = DarkColorScheme) {
        Box(modifier = Modifier.padding(15.dp)){
            TodayBlock()
        }
    }
}