/*****************************************************************
 *  Package for main screen with circular pager
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.view

/*****************************************************************
 * Imported packages
 ****************************************************************/
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.example.planote.view.note.NotesPage
import com.example.planote.view.plan.PlannerPage
import com.example.planote.view.server.ServerPage
import com.example.planote.view.settings.SettingsPage
import kotlin.math.abs

/*****************************************************************
 * Enums
 ****************************************************************/
enum class Page {
    Planner, Notes, Settings, Server
}

/*****************************************************************
 * Constants
 ****************************************************************/
private const val MULTIPLIER = 100
private const val BUFFER_ZONE = 5

/*****************************************************************
 * Top level functions
 ****************************************************************/
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen() {
    val pages = remember { Page.entries }
    val pagesAmount = pages.size * MULTIPLIER //5*100 = 500
    val initialPage = (pagesAmount / 2) // 500 /2 = 250 (in the middle)

    val pagerState = rememberPagerState(
        pageCount = { pagesAmount },
        initialPage = initialPage
    )

    LaunchedEffect(pagerState.currentPage) {
        val currentPage = pagerState.currentPage //current from 0 to 500
        val pagesSizeof = pages.size  //size of enum

        when {
            currentPage < pagesSizeof * BUFFER_ZONE -> { // if X belows 25, we so close to low border
                val targetPage = currentPage + pagesSizeof * (MULTIPLIER / 2) //get back to middle with offset
                pagerState.scrollToPage(targetPage) //move to page
            }
            currentPage > pagesAmount - pagesSizeof * BUFFER_ZONE -> { //if X upper 475, we so close to high border
                val targetPage = currentPage - pagesSizeof * (MULTIPLIER / 2) //get back to middle with offset
                pagerState.scrollToPage(targetPage) //move to page
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val actualScreen = pages[page % pages.size] //num of ENUM

            PageContent(
                screen = actualScreen,
                modifier = Modifier.fillMaxSize()
            )
        }

        CenteredPageIndicator(
            pageSizeof = pages.size,
            currentPage = pagerState.currentPage % pages.size,
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}

@Composable
private fun PageContent(screen: Page, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        when (screen) {
            Page.Planner -> PlannerPage()
            Page.Notes -> NotesPage()
            Page.Settings -> SettingsPage()
            Page.Server -> ServerPage()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CenteredPageIndicator(
    pagerState: PagerState,
    pageSizeof: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageSizeof) { index ->
            val curPageOffset = pagerState.currentPageOffsetFraction
            val targetPage = currentPage + curPageOffset
            val distance = calculateCyclicDistance(
                currentIndex = index.toFloat(),
                targetIndex = targetPage,
                totalPages = pageSizeof
            )

            // Убираем анимацию и вычисляем значения напрямую
            val dotSize = when {
                distance < 0.1f -> 12.dp
                distance <= 1.0f -> {
                    val progress = 1f - distance
                    lerp(6.dp, 12.dp, progress)
                }
                else -> 6.dp
            }

            val dotColor = when {
                distance < 0.1f -> MaterialTheme.colorScheme.primary
                distance <= 1.0f -> {
                    val progress = 1f - distance
                    val primaryColor = MaterialTheme.colorScheme.primary
                    val inactiveColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                    lerp(inactiveColor, primaryColor, progress)
                }
                else -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
            }

            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(dotSize)
                    .clip(CircleShape)
                    .background(dotColor)
            )
        }
    }
}

private fun calculateCyclicDistance(
    currentIndex: Float,
    targetIndex: Float,
    totalPages: Int
): Float {
    val directDistance = abs(currentIndex - targetIndex)
    val cyclicDistance = totalPages - directDistance
    return minOf(directDistance, cyclicDistance)
}