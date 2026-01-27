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
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarDayEntity
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarDayTaskEntity
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarMonthEntity
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarMonthTaskEntity
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarYearEntity
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarYearTaskEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/*****************************************************************
 * Data
 ****************************************************************/
/**
 * Domains
 **/
data class PlanCalendarDay(val id: Long, val title: String?, val date: LocalDate)
data class PlanCalendarMonth(val id: Long, val title: String?, val date: LocalDate)
data class PlanCalendarYear(val id: Long, val title: String?, val date: LocalDate)
data class PlanCalendarDayTask(val id: Long, val ownerId: Long, val title: String, val description: String? = null, val isDone: Boolean = false)
data class PlanCalendarMonthTask(val id: Long, val ownerId: Long, val title: String, val description: String? = null, val isDone: Boolean = false)
data class PlanCalendarYearTask(val id: Long, val ownerId: Long, val title: String, val description: String? = null, val isDone: Boolean = false)

/**
 * Instance of calendar state
 **/
data class PlanCalendarDataHolder(
    val days: List<PlanCalendarDay> = emptyList(),
    val months: List<PlanCalendarMonth> = emptyList(),
    val years: List<PlanCalendarYear> = emptyList(),
    val isLoading: Boolean = false,
    val currentViewType: CalendarViewType = CalendarViewType.DAYS,
    val error: String? = null
)

/**
 * Enum
 **/
enum class CalendarViewType { DAYS, MONTHS, YEARS }

/*****************************************************************
 * Interfaces
 ****************************************************************/
/*****************************************************************
 * Interface containing all public methods implementations of
 * viewModel class
 ****************************************************************/
interface PlanCalendarImplements {
    fun getCurrentDateImpl() : LocalDate
    fun getDays(): List<PlanCalendarDay>
    fun getMonths(): List<PlanCalendarMonth>
    fun getYears(): List<PlanCalendarYear>
    fun getDayTasks(day: PlanCalendarDay): List<PlanCalendarDayTask>
    fun getMonthTasks(month: PlanCalendarMonth): List<PlanCalendarMonthTask>
    fun getYearsTasks(year: PlanCalendarYear): List<PlanCalendarYearTask>
    fun updateDay(day: PlanCalendarDay)
    fun updateMonth(month: PlanCalendarMonth)
    fun updateYear(year: PlanCalendarYear)
    fun updateDayTask(day: PlanCalendarDay, task: PlanCalendarDayTask)
    fun updateMonthTask(month: PlanCalendarMonth, task: PlanCalendarMonthTask)
    fun updateYearTask(year: PlanCalendarYear, task: PlanCalendarYearTask)
    fun deleteDay(day: PlanCalendarDay)
    fun deleteMonth(month: PlanCalendarMonth)
    fun deleteYear(year: PlanCalendarYear)
    fun deleteDayTask(day: PlanCalendarDay, task: PlanCalendarDayTask)
    fun deleteMonthTask(month: PlanCalendarMonth, task: PlanCalendarMonthTask)
    fun deleteYearTask(year: PlanCalendarYear, task: PlanCalendarYearTask)
    fun setViewType(viewType: CalendarViewType)
}

/*****************************************************************
 * Classes
 ****************************************************************/
/*****************************************************************
 * ViewModel class for calendar
 ****************************************************************/
@HiltViewModel
class PlanCalendarViewModel @Inject constructor(
    private val daysRepository: PlanCalendarDaysRepository,
    private val monthsRepository: PlanCalendarMonthsRepository,
    private val yearsRepository: PlanCalendarYearsRepository,
) : ViewModel(), PlanCalendarImplements {
    /************************************************************
     * Private variables
     ************************************************************/
    /** val for calendar data state **/
    private val _dataState = MutableStateFlow(PlanCalendarDataHolder())
    /** val for bdId state for gettin tasks **/
    /** val for getting current date **/
    private val currentDate: LocalDate
        get() = LocalDate.now()

    /************************************************************
     * Public variables
     ************************************************************/
    /** val for instance to _planCalendarDataState for read in @Composable **/
    val dataState: StateFlow<PlanCalendarDataHolder> = _dataState.asStateFlow()

    /*************************************************************
     * Constructors and init
     *************************************************************/
    /**
     * Subscribe function to calendar data. Each time when
     * entity of data base will be changed this function will be
     * called and change the state of calendar data with new
     * changed data from database
     * @param
     * @return None
     **/
    init {
        viewModelScope.launch {
            combine(
                daysRepository.getDaysAfterThan(currentDate),
                monthsRepository.getMonthsAfterThan(currentDate),
                yearsRepository.getYearsAfterThan(currentDate)
            ) { days, months, years ->
                Triple(days, months, years)
            }.collect { (days, months, years) ->
                _dataState.update { state ->
                    state.copy(
                        days = days.map{entity -> entity.toDomain()},
                        months = months.map{entity -> entity.toDomain()},
                        years = years.map{entity -> entity.toDomain()},
                        isLoading = false
                    )
                }
            }
        }
    }

    /*************************************************************
    * Private functions
    *************************************************************/
    /**
     * domain entity convertors
     * @param
     * @return None
     **/
    private fun PlanCalendarDayEntity.toDomain()       = PlanCalendarDay(this.bdId, this.title, this.date)
    private fun PlanCalendarMonthEntity.toDomain()     = PlanCalendarMonth(this.bdId, this.title, this.date)
    private fun PlanCalendarYearEntity.toDomain()      = PlanCalendarYear(this.bdId, this.title, this.date)
    private fun PlanCalendarDayTaskEntity.toDomain()   = PlanCalendarDayTask(this.bdId, this.ownerId, this.title, this.description, this.isDone)
    private fun PlanCalendarMonthTaskEntity.toDomain() = PlanCalendarMonthTask(this.bdId, this.ownerId, this.title, this.description, this.isDone)
    private fun PlanCalendarYearTaskEntity.toDomain()  = PlanCalendarYearTask(this.bdId, this.ownerId, this.title, this.description, this.isDone)

    private fun PlanCalendarDay.toEntity()       = PlanCalendarDayEntity(this.id, this.title, this.date)
    private fun PlanCalendarMonth.toEntity()     = PlanCalendarMonthEntity(this.id, this.title, this.date)
    private fun PlanCalendarYear.toEntity()      = PlanCalendarYearEntity(this.id, this.title, this.date)
    private fun PlanCalendarDayTask.toEntity()   = PlanCalendarDayTaskEntity(this.id, this.ownerId, this.title, this.description, this.isDone)
    private fun PlanCalendarMonthTask.toEntity() = PlanCalendarMonthTaskEntity(this.id, this.ownerId, this.title, this.description, this.isDone)
    private fun PlanCalendarYearTask.toEntity()  = PlanCalendarYearTaskEntity(this.id, this.ownerId, this.title, this.description, this.isDone)

    /**
     * @param
     * @return None
     **/
    private fun clearError() = _dataState.update { it.copy(error = null) }

    private fun PlanCalendarDay.exist(): Boolean   = _dataState.value.days.contains(this)
    private fun PlanCalendarMonth.exist(): Boolean = _dataState.value.months.contains(this)
    private fun PlanCalendarYear.exist(): Boolean  = _dataState.value.years.contains(this)

    private fun PlanCalendarDayTask.exist(day: PlanCalendarDay): Boolean       = daysRepository.getTasksForDay(day.id).map{entity -> entity.toDomain()}.contains(this)
    private fun PlanCalendarMonthTask.exist(month: PlanCalendarMonth): Boolean = monthsRepository.getTasksForMonth(month.id).map{entity -> entity.toDomain()}.contains(this)
    private fun PlanCalendarYearTask.exist(year: PlanCalendarYear): Boolean    = yearsRepository.getTasksForYear(year.id).map{entity -> entity.toDomain()}.contains(this)

    private fun PlanCalendarDay.isEmpty(): Boolean   = this.title.isNullOrBlank() && (daysRepository.getTasksForDay(this.id).isEmpty())
    private fun PlanCalendarMonth.isEmpty(): Boolean = this.title.isNullOrBlank() && (monthsRepository.getTasksForMonth(this.id).isEmpty())
    private fun PlanCalendarYear.isEmpty(): Boolean  = this.title.isNullOrBlank() && (yearsRepository.getTasksForYear(this.id).isEmpty())

    /*************************************************************
     * Public functions
     *************************************************************/
    /**
     * @param
     * @return None
     **/
    override fun setViewType(viewType: CalendarViewType) = _dataState.update { it.copy(currentViewType = viewType) }
    override fun getCurrentDateImpl(): LocalDate = currentDate

    override fun getDays(): List<PlanCalendarDay> = _dataState.value.days
    override fun getMonths(): List<PlanCalendarMonth> = _dataState.value.months
    override fun getYears(): List<PlanCalendarYear> = _dataState.value.years

    override fun getDayTasks(day: PlanCalendarDay): List<PlanCalendarDayTask>         = daysRepository.getTasksForDay(day.id).map{entity -> entity.toDomain()}
    override fun getMonthTasks(month: PlanCalendarMonth): List<PlanCalendarMonthTask> = monthsRepository.getTasksForMonth(month.id).map{entity -> entity.toDomain()}
    override fun getYearsTasks(year: PlanCalendarYear): List<PlanCalendarYearTask>    = yearsRepository.getTasksForYear(year.id).map{entity -> entity.toDomain()}

    /**
     * @param
     * @return None
     **/
    override fun updateDay(day: PlanCalendarDay) {
        viewModelScope.launch {
            try {
                if (day.exist()) {
                    if(day.isEmpty()) deleteDay(day)
                    else daysRepository.updateDay(day.toEntity())
                }
                else {
                    daysRepository.insertDay(day.toEntity())
                }
            } catch (e: Exception) {
                _dataState.update { it.copy(error = "DB: Day adding error: ${e.message}") }
            }
        }
    }
    override fun updateMonth(month: PlanCalendarMonth) {
        viewModelScope.launch {
            try {
                if (month.exist()) {
                    if(month.isEmpty()) deleteMonth(month)
                    else monthsRepository.updateMonth(month.toEntity())
                }
                else {
                    monthsRepository.insertMonth(month.toEntity())
                }
            } catch (e: Exception) {
                _dataState.update { it.copy(error = "DB: Month adding error: ${e.message}") }
            }
        }
    }
    override fun updateYear(year: PlanCalendarYear) {
        viewModelScope.launch {
            try {
                if (year.exist()) {
                    if(year.isEmpty()) deleteYear(year)
                    else yearsRepository.updateYear(year.toEntity())
                }
                else {
                    yearsRepository.insertYear(year.toEntity())
                }
            } catch (e: Exception) {
                _dataState.update { it.copy(error = "DB: Year adding error: ${e.message}") }
            }
        }
    }

    /**
     * @param
     * @return None
     **/
    override fun updateDayTask(day: PlanCalendarDay, task: PlanCalendarDayTask) {
        viewModelScope.launch {
            try {
                if (!day.exist())    daysRepository.insertDay(day.toEntity())
                if (task.exist(day)) daysRepository.updateDayTask(task.toEntity())
                else                 daysRepository.insertDayTask(task.toEntity())
            } catch (e: Exception) {
                _dataState.update { it.copy(error = "DB: Day adding task error: ${e.message}") }
            }
        }
    }
    override fun updateMonthTask(month: PlanCalendarMonth, task: PlanCalendarMonthTask) {
        viewModelScope.launch {
            try {
                if (!month.exist())    monthsRepository.insertMonth(month.toEntity())
                if (task.exist(month)) monthsRepository.updateMonthTask(task.toEntity())
                else                   monthsRepository.insertMonthTask(task.toEntity())
            } catch (e: Exception) {
                _dataState.update { it.copy(error = "DB: Year adding task error: ${e.message}") }
            }
        }
    }
    override fun updateYearTask(year: PlanCalendarYear, task: PlanCalendarYearTask) {
        viewModelScope.launch {
            try {
                if (!year.exist())    yearsRepository.insertYear(year.toEntity())
                if (task.exist(year)) yearsRepository.updateYearTask(task.toEntity())
                else                  yearsRepository.insertYearTask(task.toEntity())
            } catch (e: Exception) {
                _dataState.update { it.copy(error = "DB: Year adding task error: ${e.message}") }
            }
        }
    }

    /**
     * @param
     * @return None
     **/
    override fun deleteDay(day: PlanCalendarDay) {
        viewModelScope.launch {
            try {
                if(day.exist()) daysRepository.deleteDay(day.toEntity())
            } catch (e: Exception) {
                _dataState.update { it.copy(error = "DB: Day deleting error: ${e.message}") }
            }
        }
    }
    override fun deleteMonth(month: PlanCalendarMonth) {
        viewModelScope.launch {
            try {
                if(month.exist()) monthsRepository.deleteMonth(month.toEntity())
            } catch (e: Exception) {
                _dataState.update { it.copy(error = "DB: Month deleting error: ${e.message}") }
            }
        }
    }
    override fun deleteYear(year: PlanCalendarYear) {
        viewModelScope.launch {
            try {
                if(year.exist()) yearsRepository.deleteYear(year.toEntity())
            } catch (e: Exception) {
                _dataState.update { it.copy(error = "DB: Year deleting error: ${e.message}") }
            }
        }
    }

    /**
     * @param
     * @return None
     **/
    override fun deleteDayTask(day: PlanCalendarDay, task: PlanCalendarDayTask) {
        viewModelScope.launch {
            try {
                if(day.exist() && task.exist(day)) daysRepository.deleteDayTask(task.toEntity())
            } catch (e: Exception) {
                _dataState.update { it.copy(error = "DB: Day deleting task error: ${e.message}") }
            }
        }
    }
    override fun deleteMonthTask(month: PlanCalendarMonth, task: PlanCalendarMonthTask) {
        viewModelScope.launch {
            try {
                if(month.exist() && task.exist(month)) monthsRepository.deleteMonthTask(task.toEntity())
            } catch (e: Exception) {
                _dataState.update { it.copy(error = "DB: Month deleting task error: ${e.message}") }
            }
        }
    }
    override fun deleteYearTask(year: PlanCalendarYear, task: PlanCalendarYearTask) {
        viewModelScope.launch {
            try {
                if(year.exist() && task.exist(year)) yearsRepository.deleteYearTask(task.toEntity())
            } catch (e: Exception) {
                _dataState.update { it.copy(error = "DB: Year deleting task error: ${e.message}") }
            }
        }
    }

}
/*****************************************************************
 * End of class
 ****************************************************************/