/*****************************************************************
 *  Package for server view
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.view.server

/*****************************************************************
 * Imported packages
 ****************************************************************/
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

/*****************************************************************
 * Top Level Functions
 ****************************************************************/
@Composable
fun ServerPage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🌐", fontSize = 48.sp)
            Text("Страница сервера", fontSize = 24.sp, color = MaterialTheme.colorScheme.onBackground)
            Text("Плавно проведите пальцем влево/вправо", fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
        }
    }
}