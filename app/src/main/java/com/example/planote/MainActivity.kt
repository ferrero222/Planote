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
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
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
    primary = Color(0xFF27C4B9),
    background = Color(0xFF000000),
    surface = Color(0xFF2F3B3B),
    onSurface = Color(0xFFFFFFFF),
    onError = Color(0xE4FF6D6D)
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
        colorScheme = colorScheme,
        typography = MyAppFont,
        content = content
    )
}

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
    bodyLarge = TextStyle(fontFamily = AldrichFont, fontSize = 16.sp, fontWeight = FontWeight.Normal),
    bodyMedium = TextStyle(fontFamily = AldrichFont),
    bodySmall = TextStyle(fontFamily = AldrichFont),
    labelLarge = TextStyle(fontFamily = AldrichFont),
    labelMedium = TextStyle(fontFamily = AldrichFont),
    labelSmall = TextStyle(fontFamily = AldrichFont),
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
 * Preview
 ****************************************************************/
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyAppTheme {
        MainScreen()
    }
}