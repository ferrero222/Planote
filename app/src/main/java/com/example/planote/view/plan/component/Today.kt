/*****************************************************************
 *  Package for server view
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.view.plan.component

/*****************************************************************
 * Imported packages
 ****************************************************************/
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.planote.viewModel.plan.PlanCalendarViewModel

/*****************************************************************
 * Top Level Functions
 ****************************************************************/
@Composable
fun TodayBlock(viewModel: PlanCalendarViewModel = hiltViewModel()) {
    // Размер "лужи" — примерно 120x80 dp
    val blobWidth = 120.dp
    val blobHeight = 80.dp
    val cornerRadius = 24.dp // для плавности

    Column() {
        Text(
            modifier = Modifier.padding(12.dp),
            text = "ПЛАНИРОВЩИК, сегодня",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Основной контент — с отступом справа, чтобы не залезать под "лужу"
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 16.dp,
                            top = 16.dp,
                            end = blobWidth + 16.dp, // ← ключевой отступ!
                            bottom = 16.dp
                        ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Сегодня отличный день для продуктивности",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Divider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    SectionTitle("Задачи на сегодня")
                    repeat(5) { TaskItem("Задача $it") }

                    SectionTitle("Активные задачи")
                    repeat(3) { TaskItem("Активная задача $it") }
                }

                // "Лужа" — произвольная мягкая форма
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 12.dp, end = 12.dp)
                        .size(width = 120.dp, height = 80.dp)
                        .clip(BlobShape()) // ← теперь работает!
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "20 января\n2026",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Clip,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

// Эти вспомогательные функции остаются без изменений
@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun TaskItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(color = MaterialTheme.colorScheme.onPrimary, shape = CircleShape)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

class BlobShape : Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val w = size.width
        val h = size.height
        return Outline.Generic(
            Path().apply {
                // Начинаем чуть ниже верхнего левого края
                moveTo(w * 0.1f, h * 0.25f)

                // Верхняя дуга → плавно вправо
                cubicTo(
                    x1 = w * 0.3f, y1 = h * 0.1f,
                    x2 = w * 0.7f, y2 = h * 0.1f,
                    x3 = w * 0.9f, y3 = h * 0.25f
                )

                // Правая сторона → вниз с "пульсом"
                cubicTo(
                    x1 = w * 1.05f, y1 = h * 0.4f,  // слегка выходим за границу для выпуклости
                    x2 = w * 1.0f, y2 = h * 0.65f,
                    x3 = w * 0.85f, y3 = h * 0.8f
                )

                // Нижняя дуга → влево
                cubicTo(
                    x1 = w * 0.7f, y1 = h * 0.9f,
                    x2 = w * 0.3f, y2 = h * 0.9f,
                    x3 = w * 0.15f, y3 = h * 0.8f
                )

                // Левая сторона → вверх с "впадиной"
                cubicTo(
                    x1 = w * 0.0f, y1 = h * 0.65f,
                    x2 = w * -0.05f, y2 = h * 0.4f, // слегка внутрь
                    x3 = w * 0.1f, y3 = h * 0.25f
                )

                // Форма замкнута автоматически последней точкой
            }
        )
    }
}