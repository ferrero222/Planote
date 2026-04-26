/*****************************************************************
 *  Package for main screen with bottom navigation
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.view

/*****************************************************************
 * Imported packages
 ****************************************************************/
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bottombar.AnimatedBottomBar
import com.example.bottombar.components.BottomBarItem
import com.example.bottombar.model.IndicatorStyle
import com.example.bottombar.model.ItemStyle
import com.example.planote.DarkColorScheme
import com.example.planote.isLandscape
import com.example.planote.view.note.NotesPage
import com.example.planote.view.plan.PlannerPage
import com.example.planote.view.server.ServerPage
import com.example.planote.view.settings.SettingsPage

/*****************************************************************
 * Variables, data, enum
 ****************************************************************/
sealed class Screen(
    val title: String,
    val icon: ImageVector
) {
    object Planner : Screen("Планирование", Icons.Default.DateRange)
    object Notes : Screen("Заметки", Icons.AutoMirrored.Filled.Note)
    object Server : Screen("Сервер", Icons.Default.Cloud)
    object Settings : Screen("Настройки", Icons.Default.Settings)

    companion object {
        val items = listOf(Planner, Notes, Server, Settings)
    }
}

/*****************************************************************
 * Public functions
 ****************************************************************/
@Composable
fun MainScreen() {
    var selectedScreenIndex by remember { mutableIntStateOf(0) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomNavigationBar(
                selectedIndex = selectedScreenIndex,
                onScreenSelected = { index -> selectedScreenIndex = index }
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            val plannerAlpha  by animateFloatAsState(targetValue = if (selectedScreenIndex == 0) 1f else 0f, animationSpec = tween(durationMillis = 300), label = "PlannerAlpha")
            val notesAlpha    by animateFloatAsState(targetValue = if (selectedScreenIndex == 1) 1f else 0f, animationSpec = tween(durationMillis = 300), label = "NotesAlpha")
            val serverAlpha   by animateFloatAsState(targetValue = if (selectedScreenIndex == 2) 1f else 0f, animationSpec = tween(durationMillis = 300), label = "ServerAlpha")
            val settingsAlpha by animateFloatAsState(targetValue = if (selectedScreenIndex == 3) 1f else 0f, animationSpec = tween(durationMillis = 300), label = "SettingsAlpha")
            Box(modifier = Modifier.fillMaxSize().graphicsLayer { alpha = plannerAlpha })  { PlannerPage()  }
            Box(modifier = Modifier.fillMaxSize().graphicsLayer { alpha = notesAlpha })    { NotesPage()    }
            Box(modifier = Modifier.fillMaxSize().graphicsLayer { alpha = serverAlpha })   { ServerPage()   }
            Box(modifier = Modifier.fillMaxSize().graphicsLayer { alpha = settingsAlpha }) { SettingsPage() }
        }
    }
}

/*****************************************************************
 * Private functions
 ****************************************************************/
@Composable
private fun BottomNavigationBar(
    selectedIndex: Int,
    onScreenSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surface)
    ) {
        val orientation = LocalConfiguration.current.orientation
        AnimatedBottomBar(
            bottomBarHeight = if(isLandscape()) 40.dp else 60.dp,
            selectedItem = selectedIndex,
            itemSize = Screen.items.size,
            containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.6f),
            indicatorStyle = IndicatorStyle.LINE,
            indicatorColor = MaterialTheme.colorScheme.primary,
        ) {
            Screen.items.forEachIndexed { index, screen ->
                BottomBarItem(
                    selected = index == selectedIndex,
                    onClick = { onScreenSelected(index) },
                    imageVector = screen.icon,
                    label = screen.title,
                    containerColor = Color.Transparent,
                    contentColor = if(index == selectedIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                    iconColor = if(index == selectedIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                    textColor = if(index == selectedIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                    itemStyle = ItemStyle.STYLE4
                )
            }
        }
    }
}

/*****************************************************************
 * Preview
 ****************************************************************/
@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun MainScreenPreview() {
    MaterialTheme(
        colorScheme = DarkColorScheme
    ) {
        var selectedScreenIndex by remember { mutableIntStateOf(0) }
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                BottomNavigationBar(
                    selectedIndex = selectedScreenIndex,
                    onScreenSelected = { index -> selectedScreenIndex = index }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier.fillMaxSize().padding(padding)
            )
        }
    }
}
