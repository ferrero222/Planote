/*****************************************************************
 *  Package for main screen with circular pager
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.view.plan.component.week

/*****************************************************************
 * Imported packages
 ****************************************************************/
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.planote.PreviewContainer
import com.example.planote.viewModel.plan.PlanWeekDialogMode
import com.example.planote.viewModel.plan.PlanWeekDialogPlanDataHolder
import com.example.planote.viewModel.plan.PlanWeekDomain
import com.example.planote.viewModel.plan.PlanWeekLoading
import com.example.planote.viewModel.plan.PlanWeekViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import me.trishiraj.shadowglow.shadowGlow

/*****************************************************************
 * Variables, data, enum
 ****************************************************************/
sealed class WeekDialogPlanChangeAlert {
    object None : WeekDialogPlanChangeAlert()
    data class DeletePlan(val week: PlanWeekDomain) : WeekDialogPlanChangeAlert()
}

/*****************************************************************
 * Interfaces
 ****************************************************************/
/*****************************************************************
 * Private functions
 ****************************************************************/
@OptIn(DelicateCoroutinesApi::class)
@Composable
private fun WeekDialogPlanChangeContent(
    dialogState: PlanWeekDialogPlanDataHolder,
    loading: PlanWeekLoading,
    deletingWeekIds: Set<Long>,
    onDismissClick: () -> Unit,
    onSelect: (PlanWeekDomain) -> Unit,
    onEdit: (PlanWeekDomain) -> Unit,
    onDelete: (PlanWeekDomain) -> Unit,
    onAdd: () -> Unit,
    onSave: () -> Unit,
    onDeletePlan: (PlanWeekDomain) -> Unit,
    onDismissAlert: () -> Unit,
) {
    var planDialogAlert by remember { mutableStateOf<WeekDialogPlanChangeAlert>(WeekDialogPlanChangeAlert.None) }

    WeekLoading(loading) {
        Column(
            verticalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier.fillMaxSize().padding(25.dp),
        ) {
            WeekDialogPlanAddContentHeader(
                onDismissClick = onDismissClick,
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(15.dp),
                modifier = Modifier.weight(1f).fillMaxWidth(),
            ) {
                WeekDialogPlanAddContentBody(
                    weeks = dialogState.weeks,
                    selectedWeekId = dialogState.weeks.find { it.isToggle && (!it.title.isNullOrEmpty() || !it.description.isNullOrEmpty()) }?.id ?: 0L,
                    deletingWeekIds = deletingWeekIds,
                    onSelect = onSelect,
                    onEdit = onEdit,
                    onDelete = { week ->
                        planDialogAlert = WeekDialogPlanChangeAlert.DeletePlan(week)
                        onDelete(week)
                    },
                )
            }
            WeekDialogPlanAddContentFooter(
                onAdd = onAdd,
                onSave = onSave,
            )
        }

        WeekDialogPlanChangeAlertHandler(
            planDialogAlert = planDialogAlert,
            onDismiss = onDismissAlert,
            onDeletePlan = onDeletePlan,
        )
    }
}

@Composable
private fun WeekDialogPlanAddContentHeader(
    onDismissClick: () -> Unit
){
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = { onDismissClick() },
            modifier = Modifier.align(Alignment.CenterStart).size(18.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Закрыть",
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
        Text(
            text = "ПЛАНЫ",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.align(Alignment.Center)
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .background(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(10.dp)
                )

        ) {
            Text(
                text = "VIEW",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
        }
    }
}

@Composable
private fun WeekDialogPlanItem(
    week: PlanWeekDomain,
    isSelected: Boolean,
    isDeleting: Boolean = false,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if(isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
        animationSpec = tween(500),
        label = "borderColor"
    )

    val editIconColor by animateColorAsState(
        targetValue = if(isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        animationSpec = tween(300),
        label = "editIconColor"
    )

    val deleteIconColor by animateColorAsState(
        targetValue = if(isSelected) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        animationSpec = tween(300),
        label = "editIconColor"
    )

    AnimatedVisibility(
        visible = !isDeleting,
        exit = fadeOut(animationSpec = tween(150)) + shrinkVertically(animationSpec = tween(150)),
        enter = fadeIn(animationSpec = tween(150)) + expandVertically(animationSpec = tween(150))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if(isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface)
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable { onSelect() }
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = week.title ?: "Без названия",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if(!week.description.isNullOrBlank()) {
                            Text(
                                text = week.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Row {
                        IconButton(
                            onClick = { onEdit() },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EditNote,
                                contentDescription = "Редактировать",
                                tint = editIconColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        IconButton(
                            onClick = { onDelete() },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Удалить",
                                tint = deleteIconColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeekDialogPlanAddContentBody(
    weeks: List<PlanWeekDomain>,
    selectedWeekId: Long,
    deletingWeekIds: Set<Long> = emptySet(),
    onSelect: (PlanWeekDomain) -> Unit,
    onEdit: (PlanWeekDomain) -> Unit,
    onDelete: (PlanWeekDomain) -> Unit
) {
    val listState = rememberLazyListState()
    Column{
        Box(modifier = Modifier.fillMaxWidth()){
            Text(
                text = "АКТИВНЫЕ ПЛАНЫ",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.CenterStart)
            )
        }
        Box {
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(
                    weeks.filterNot { it.title.isNullOrBlank() && it.description.isNullOrBlank() },
                    key = { it.id }
                ) { week ->
                    WeekDialogPlanItem(
                        week = week,
                        isSelected = week.id == selectedWeekId,
                        isDeleting = deletingWeekIds.contains(week.id),
                        onSelect = { onSelect(week) },
                        onEdit = { onEdit(week) },
                        onDelete = { onDelete(week) }
                    )
                }
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                AnimatedVisibility(
                    visible = listState.canScrollForward,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    enter = fadeIn(animationSpec = tween(150)),
                    exit = fadeOut(animationSpec = tween(150))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                        MaterialTheme.colorScheme.surface
                                    )
                                )
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun WeekDialogPlanAddContentFooter(
    onAdd: () -> Unit,
    onSave: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ){
        OutlinedButton(
            onClick = onAdd,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth().height(48.dp),
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Добавить новый план")
        }
        Button(
            onClick = onSave,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .shadowGlow(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f), offsetX = 0.dp, offsetY = 0.dp, blurRadius = 17.dp),
        ) {
            Text(
                text = "СОХРАНИТЬ",
                color = MaterialTheme.colorScheme.background,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun WeekDialogPlanChangeAlertHandler(
    planDialogAlert: WeekDialogPlanChangeAlert,
    onDismiss: () -> Unit,
    onDeletePlan: (PlanWeekDomain) -> Unit
) {
    when(planDialogAlert) {
        is WeekDialogPlanChangeAlert.DeletePlan -> {
            val week = planDialogAlert.week
            WeekAlert(
                title = "Удалить план?",
                description = "Этот план будет удален",
                confirmText = "Удалить",
                dismissText = "Отменить",
                onConfirm = {
                    onDismiss()
                    onDeletePlan(week)
                },
                onDismiss = {
                    onDismiss()
                }
            )
        }
        is WeekDialogPlanChangeAlert.None -> { }
    }
}

/*****************************************************************
 * Public functions
 ****************************************************************/
@OptIn(DelicateCoroutinesApi::class)
@Composable
fun WeekDialogPlanChangeContent(
    viewModel: PlanWeekViewModel = hiltViewModel(),
    dialogStateChange: (PlanWeekDialogMode) -> Unit
) {
    val dialogState by viewModel.dialogPlanState.collectAsStateWithLifecycle()
    var deletingWeekIds by remember { mutableStateOf(setOf<Long>()) }
    var planDialogAlert by remember { mutableStateOf<WeekDialogPlanChangeAlert>(WeekDialogPlanChangeAlert.None) }

    WeekDialogPlanChangeContent(
        dialogState = dialogState,
        loading = dialogState.loading,
        deletingWeekIds = deletingWeekIds,
        onDismissClick = { dialogStateChange(PlanWeekDialogMode.IDLE) },
        onSelect = { week ->
            dialogState.weeks.forEach { w -> viewModel.updateWeek(w.copy(isToggle = false)) }
            viewModel.updateWeek(week.copy(isToggle = true))
        },
        onEdit = { week ->
            viewModel.loadEditWeek(week)
            dialogStateChange(PlanWeekDialogMode.PLANADD)
        },
        onDelete = { week ->
            planDialogAlert = WeekDialogPlanChangeAlert.DeletePlan(week)
        },
        onAdd = {
            viewModel.loadEditWeek(PlanWeekDomain())
            dialogStateChange(PlanWeekDialogMode.PLANADD)
        },
        onSave = {
            viewModel.saveWeeks()
            dialogStateChange(PlanWeekDialogMode.IDLE)
        },
        onDeletePlan = { week ->
            deletingWeekIds = deletingWeekIds + week.id
            kotlinx.coroutines.GlobalScope.launch {
                kotlinx.coroutines.delay(250)
                viewModel.updateWeek(week.copy(title = "", description = ""))
                deletingWeekIds = deletingWeekIds - week.id
            }
        },
        onDismissAlert = { planDialogAlert = WeekDialogPlanChangeAlert.None },
    )

    WeekDialogPlanChangeAlertHandler(
        planDialogAlert = planDialogAlert,
        onDismiss = { planDialogAlert = WeekDialogPlanChangeAlert.None },
        onDeletePlan = { week ->
            deletingWeekIds = deletingWeekIds + week.id
            kotlinx.coroutines.GlobalScope.launch {
                kotlinx.coroutines.delay(250)
                viewModel.updateWeek(week.copy(title = "", description = ""))
                deletingWeekIds = deletingWeekIds - week.id
            }
        }
    )
}

/*****************************************************************
 * Preview functions
 ****************************************************************/
@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun WeekDialogPlanChangeContentPreview(
    weeks: List<PlanWeekDomain> = listOf(
        PlanWeekDomain(id = 1, title = "План 1", description = "Основной план", isToggle = true),
        PlanWeekDomain(id = 2, title = "План 2", description = "Запасной вариант", isToggle = false),
        PlanWeekDomain(id = 3, title = "План 3", description = "", isToggle = false)
    ),
    loading: PlanWeekLoading = PlanWeekLoading.Idle
) {
    PreviewContainer {
        WeekDialogCard {
            WeekDialogPlanChangeContent(
                dialogState = PlanWeekDialogPlanDataHolder(weeks = weeks),
                loading = loading,
                deletingWeekIds = emptySet(),
                onDismissClick = {},
                onSelect = {},
                onEdit = {},
                onDelete = {},
                onAdd = {},
                onSave = {},
                onDeletePlan = {},
                onDismissAlert = {},
            )
        }
    }
}





