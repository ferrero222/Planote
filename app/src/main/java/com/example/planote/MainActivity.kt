/*****************************************************************
 *  Main Activity
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote

/*****************************************************************
 * Imported packages
 ****************************************************************/
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.planote.view.MainScreen
import dagger.hilt.android.AndroidEntryPoint

/*****************************************************************
 * Variables, data, enum
 ****************************************************************/
val Primary = Color(0xFF27C4B9)
val BackgroundLight = Color(0xFFF5F5F5)
val BackgroundDark = Color(0xFF0A0A0A)
val Surface = Color(0xFF1D2828)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    background = BackgroundLight,
    surface = Surface,
    onSurface = Color.White,
)


private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    background = BackgroundDark,
    surface = Surface,
    onSurface = Color.White,
)

/*****************************************************************
 * Interfaces
 ****************************************************************/
/*****************************************************************
 * Top Level Functions
 ****************************************************************/
@Composable
fun MyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme, content = content
    )
}

/*****************************************************************
 * Classes
 ****************************************************************/
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAppTheme {
                MainScreen()
            }
        }
    }
}

/*****************************************************************
 * Preview
 ****************************************************************/
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyAppTheme {
        MainScreen()
    }
}