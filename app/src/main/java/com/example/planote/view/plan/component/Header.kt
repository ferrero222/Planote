/*****************************************************************
 *  Package for main screen with circular pager
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.view.plan.component

/*****************************************************************
 * Imported packages
 ****************************************************************/
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.planote.DarkColorScheme
import me.trishiraj.shadowglow.shadowGlow

/*****************************************************************
 * Variables, data, enum
 ****************************************************************/
/*****************************************************************
 * Interfaces
 ****************************************************************/
/*****************************************************************
 * Top level functions
 ****************************************************************/
@Composable
fun HeaderBlock() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(top = 35.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Dashboard,
            contentDescription = "headerIcon",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(top = 9.dp, end = 25.dp)
                .size(25.dp)
                .shadowGlow(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    offsetX = 0.dp,
                    offsetY = 0.dp,
                    blurRadius = 25.dp
                ),
        )
        Text(
            text = "ПАНЕЛЬ.РАСПИСАНИЯ",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth().padding(start = 35.dp, top = 10.dp)
        )
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
fun HeaderBlockPreview(){
    MaterialTheme( colorScheme = DarkColorScheme) {
        Box(modifier = Modifier.padding(15.dp)){
            HeaderBlock()
        }
    }
}