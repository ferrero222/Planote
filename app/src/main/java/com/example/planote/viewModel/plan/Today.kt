/*****************************************************************
 *  Package for MVVM plan data repository
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.viewModel.plan

/*****************************************************************
 * Imported packages
 ****************************************************************/
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planote.model.plan.repository.PlanCalendarDaysRepository
import com.example.planote.model.plan.repository.PlanCalendarMonthsRepository
import com.example.planote.model.plan.repository.PlanCalendarYearsRepository
import com.example.planote.model.plan.repository.PlanWeekRepository
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarDayTaskEntity
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarMonthTaskEntity
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarYearTaskEntity
import com.example.planote.model.plan.repository.source.local.room.entity.PlanWeekDayTaskEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

/*****************************************************************
 * Data
 ****************************************************************/
data class PlanTodayTaskDomain(
    val id: Long = 0,
    val ownerId: Long = 0,
    val title: String? = null,
    val description: String? = null,
    val isDone: Boolean = false,
    val time: LocalTime? = null,
    val source: PlanTodaySource = PlanTodaySource.CALENDAR_DAY,
)

enum class PlanTodaySource { WEEK, CALENDAR_DAY, CALENDAR_MONTH, CALENDAR_YEAR }

data class PlanTodayDataHolder(
    val weekTasks: List<PlanTodayTaskDomain> = emptyList(),
    val dayTasks: List<PlanTodayTaskDomain> = emptyList(),
    val monthTasks: List<PlanTodayTaskDomain> = emptyList(),
    val yearTasks: List<PlanTodayTaskDomain> = emptyList(),
    val loading: Boolean = false,
)

/*****************************************************************
 * Interfaces
 ****************************************************************/
/*****************************************************************
 * Classes
 ****************************************************************/
/*****************************************************************
 * ViewModel class for Today block
 ****************************************************************/
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PlanTodayViewModel @Inject constructor(
    private val daysRepository: PlanCalendarDaysRepository,
    private val monthsRepository: PlanCalendarMonthsRepository,
    private val yearsRepository: PlanCalendarYearsRepository,
    private val weekRepository: PlanWeekRepository,
) : ViewModel() {
    /************************************************************
     * Private variables
     ************************************************************/
    private val _dataState = MutableStateFlow(PlanTodayDataHolder())

    private val today: LocalDate
        get() = LocalDate.now()

    /************************************************************
     * Public variables
     ************************************************************/
    val dataState: StateFlow<PlanTodayDataHolder> = _dataState.asStateFlow()

    /*************************************************************
     * Constructors and init
     *************************************************************/
    init {
        subscribeToData()
    }

    /*************************************************************
     * Private functions
     *************************************************************/
    private fun PlanWeekDayTaskEntity.toDomain(source: PlanTodaySource) = PlanTodayTaskDomain(
        id = this.bdId,
        ownerId = this.ownerId,
        title = this.title,
        description = this.description,
        isDone = this.isDone,
        time = this.time,
        source = source,
    )

    private fun PlanCalendarDayTaskEntity.toDomain(source: PlanTodaySource) = PlanTodayTaskDomain(
        id = this.bdId,
        ownerId = this.ownerId,
        title = this.title,
        description = this.description,
        isDone = this.isDone,
        time = if (this.isDone) LocalTime.MIDNIGHT else null,
        source = source,
    )

    private fun PlanCalendarMonthTaskEntity.toDomain(source: PlanTodaySource) = PlanTodayTaskDomain(
        id = this.bdId,
        ownerId = this.ownerId,
        title = this.title,
        description = this.description,
        isDone = this.isDone,
        time = null,
        source = source,
    )

    private fun PlanCalendarYearTaskEntity.toDomain(source: PlanTodaySource) = PlanTodayTaskDomain(
        id = this.bdId,
        ownerId = this.ownerId,
        title = this.title,
        description = this.description,
        isDone = this.isDone,
        time = null,
        source = source,
    )

    private fun sortTimeTasks(tasks: List<PlanTodayTaskDomain>): List<PlanTodayTaskDomain> {
        return tasks.sortedWith { a, b ->
            val aHasTime = a.time != null && a.time != LocalTime.MIDNIGHT
            val bHasTime = b.time != null && b.time != LocalTime.MIDNIGHT
            when {
                aHasTime && bHasTime -> a.time.compareTo(b.time)
                aHasTime && !bHasTime -> -1
                !aHasTime && bHasTime -> 1
                else -> 0
            }
        }
    }

    private fun subscribeToData() {
        viewModelScope.launch {
            _dataState.update { it.copy(loading = true) }

            val weekTasksFlow: Flow<List<PlanTodayTaskDomain>> = weekRepository.getWeeks()
                .flatMapLatest { weeks ->
                    val toggledWeek = weeks.find { it.isToggle }
                    if (toggledWeek == null) flowOf(emptyList())
                    else weekRepository.getWeekDays(toggledWeek.bdId)
                        .flatMapLatest { days ->
                            val todayDayIndex = today.dayOfWeek.value - 1
                            val todayDay = days.find { it.num == todayDayIndex }
                            if (todayDay == null || todayDay.bdId <= 0L) flowOf(emptyList())
                            else weekRepository.getWeekDayTasks(todayDay.bdId)
                        }
                }
                .map { tasks -> sortTimeTasks(tasks.map { it.toDomain(PlanTodaySource.WEEK) }) }

            val dayTasksFlow: Flow<List<PlanTodayTaskDomain>> = daysRepository.getDaysAfterThan(today)
                .flatMapLatest { days ->
                    val todayDay = days.find { it.date == today }
                    if (todayDay == null || todayDay.bdId <= 0L) flowOf(emptyList())
                    else daysRepository.getTasksForDay(todayDay.bdId)
                }
                .map { tasks -> tasks.map { it.toDomain(PlanTodaySource.CALENDAR_DAY) } }

            val monthTasksFlow: Flow<List<PlanTodayTaskDomain>> = monthsRepository.getMonthsAfterThan(
                    today.withDayOfMonth(1)
                )
                .flatMapLatest { months ->
                    val thisMonth = months.find { it.date.month == today.month && it.date.year == today.year }
                    if (thisMonth == null || thisMonth.bdId <= 0L) flowOf(emptyList())
                    else monthsRepository.getTasksForMonth(thisMonth.bdId)
                }
                .map { tasks -> tasks.map { it.toDomain(PlanTodaySource.CALENDAR_MONTH) } }

            val yearTasksFlow: Flow<List<PlanTodayTaskDomain>> = yearsRepository.getYearsAfterThan(
                    today.withDayOfYear(1)
                )
                .flatMapLatest { years ->
                    val thisYear = years.find { it.date.year == today.year }
                    if (thisYear == null || thisYear.bdId <= 0L) flowOf(emptyList())
                    else yearsRepository.getTasksForYear(thisYear.bdId)
                }
                .map { tasks -> tasks.map { it.toDomain(PlanTodaySource.CALENDAR_YEAR) } }

            combine(weekTasksFlow, dayTasksFlow, monthTasksFlow, yearTasksFlow) { week, day, month, year ->
                PlanTodayDataHolder(
                    weekTasks = week,
                    dayTasks = day,
                    monthTasks = month,
                    yearTasks = year,
                )
            }.collect { state ->
                _dataState.value = state
            }
        }
    }

    /*************************************************************
     * Public functions
     *************************************************************/
    fun updateTaskDone(task: PlanTodayTaskDomain, isDone: Boolean) {
        viewModelScope.launch {
            try {
                when (task.source) {
                    PlanTodaySource.WEEK -> weekRepository.updateWeekDayTask(
                        PlanWeekDayTaskEntity(
                            bdId = task.id,
                            ownerId = task.ownerId,
                            title = task.title,
                            description = task.description,
                            time = task.time ?: LocalTime.MIDNIGHT,
                            isDone = isDone
                        )
                    )
                    PlanTodaySource.CALENDAR_DAY -> daysRepository.updateDayTask(
                        PlanCalendarDayTaskEntity(
                            bdId = task.id,
                            ownerId = task.ownerId,
                            title = task.title,
                            description = task.description,
                            isDone = isDone
                        )
                    )
                    PlanTodaySource.CALENDAR_MONTH -> monthsRepository.updateMonthTask(
                        PlanCalendarMonthTaskEntity(
                            bdId = task.id,
                            ownerId = task.ownerId,
                            title = task.title,
                            description = task.description,
                            isDone = isDone
                        )
                    )
                    PlanTodaySource.CALENDAR_YEAR -> yearsRepository.updateYearTask(
                        PlanCalendarYearTaskEntity(
                            bdId = task.id,
                            ownerId = task.ownerId,
                            title = task.title,
                            description = task.description,
                            isDone = isDone
                        )
                    )
                }
            } catch (_: Exception) { }
        }
    }
}

/*****************************************************************
 * End of class
 ****************************************************************/
