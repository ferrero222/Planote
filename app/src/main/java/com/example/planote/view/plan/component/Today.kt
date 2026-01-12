/*****************************************************************
 *  Package for server view
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.view.plan.component

/*****************************************************************
 * Imported packages
 ****************************************************************/
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.planote.view.plan.CustomBlock

/*****************************************************************
 * Top Level Functions
 ****************************************************************/
@Composable
fun TodayBlock() {
        CustomBlock(
            title = "Сегодня",
            settingsExist = false,
            onSettingsClick = { }
        ) {
            Text(
                text = "• Задача 1\n• Задача 2\n• Задача 3",
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
}