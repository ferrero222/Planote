/*****************************************************************
 *  Main Activity
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote

/*****************************************************************
 * Imported packages
 ****************************************************************/
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.planote.view.MainScreen
import dagger.hilt.android.AndroidEntryPoint

/*****************************************************************
 * Variables, data, enum
 ****************************************************************/
val LightColorScheme = lightColorScheme(
    primary = Color(0xFF27C4B9),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFE0E0E0),
    onSurface = Color(0xFF000000),
    onError = Color(0xE4FF6D6D)
)

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF2CD5DA),
    background = Color(0xFF000000),
    surface = Color(0xF01D2121),
    onSurface = Color(0xFFFFFFFF),
    onError = Color(0xE4FF6D6D)
)

val AldrichFont = FontFamily(Font(R.font.aldrich))

val MyAppFont = Typography(
    displayLarge = TextStyle(fontFamily = AldrichFont),
    displayMedium = TextStyle(fontFamily = AldrichFont),
    displaySmall = TextStyle(fontFamily = AldrichFont),
    headlineLarge = TextStyle(fontFamily = AldrichFont),
    headlineMedium = TextStyle(fontFamily = AldrichFont),
    headlineSmall = TextStyle(fontFamily = AldrichFont),
    titleLarge = TextStyle(fontFamily = AldrichFont),
    titleMedium = TextStyle(fontFamily = AldrichFont),
    titleSmall = TextStyle(fontFamily = AldrichFont),
    bodyLarge = TextStyle(fontFamily = AldrichFont),
    bodyMedium = TextStyle(fontFamily = AldrichFont),
    bodySmall = TextStyle(fontFamily = AldrichFont),
    labelLarge = TextStyle(fontFamily = AldrichFont),
    labelMedium = TextStyle(fontFamily = AldrichFont),
    labelSmall = TextStyle(fontFamily = AldrichFont),
)

/*****************************************************************
 * Interfaces
 ****************************************************************/
/*****************************************************************
 * Top Level Functions
 ****************************************************************/
@Composable
fun isLandscape(): Boolean = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

@Composable
fun MyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = MyAppFont,
        content = content
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
@Composable
fun PreviewContainer(
    padding: Dp = 24.dp,
    content: @Composable BoxScope.() -> Unit
) {
    MaterialTheme(colorScheme = DarkColorScheme, typography = MyAppFont) {
        Box(content = content)
    }
}