/*****************************************************************
 *  Package for MVVM plan data repository
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

/************************************************************
 * Global variables
 ************************************************************/
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF00BCD4),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF00364A), // необязательно, но полезно для Material 3
    onPrimaryContainer = Color(0xFF97F0FF),
    secondary = Color(0xFF03DAC6),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF00514A),
    onSecondaryContainer = Color(0xFF8CFFE9),
    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0), // чуть мягче, чем чисто белый
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0), // тоже мягкий белый для лучшей читаемости
    surfaceVariant = Color(0xFF303030), // если используете Material 3
    onSurfaceVariant = Color(0xFFB0B0B0),
    outline = Color(0xFF707070)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE),
    secondary = Color(0xFF03DAC6),
    background = Color.White,
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
)

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
 * Top Level Functions
 ****************************************************************/
@Composable
fun MyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

/*****************************************************************
 * Previews
 ****************************************************************/
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyAppTheme {
        MainScreen()
    }
}