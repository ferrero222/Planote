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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/*****************************************************************
 * Data
 ****************************************************************/
data class PlanCalendarEntityDomain(
    val id: Long = 0,
    val title: String? = null,
    val date: LocalDate = LocalDate.MIN)

data class PlanCalendarTaskDomain(
    val id: Long = 0,
    val ownerId: Long = 0,
    val title: String? = null,
    val description: String? = null,
    val isDone: Boolean = false)

data class PlanCalendarDataHolder(
    val days: List<PlanCalendarEntityDomain> = emptyList(),
    val months: List<PlanCalendarEntityDomain> = emptyList(),
    val years: List<PlanCalendarEntityDomain> = emptyList(),
    val isLoading: Boolean = false,
    val type: CalendarType = CalendarType.DAYS,
    val error: String? = null
)

data class PlanCalendarTempTaskHolder(
    val tasks: List<PlanCalendarTaskDomain> = emptyList(),
)

enum class CalendarType { DAYS, MONTHS, YEARS }

/*****************************************************************
 * Interfaces
 ****************************************************************/
/*****************************************************************
 * Interface containing all public methods implementations of
 * viewModel class
 ****************************************************************/
interface PlanCalendarImplements {
    fun getCurrentDateImpl() : LocalDate
    fun getEntity(type :CalendarType): List<PlanCalendarEntityDomain>
    fun getEntityTasks(type: CalendarType, entity: PlanCalendarEntityDomain)
    fun updateEntity(type: CalendarType, entity: PlanCalendarEntityDomain, task: PlanCalendarTaskDomain)
    fun setType(type: CalendarType)
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
    private val _dataState = MutableStateFlow(PlanCalendarDataHolder())
    private val _taskState = MutableStateFlow(PlanCalendarTempTaskHolder())
    private val currentDate: LocalDate
        get() = LocalDate.now()

    /************************************************************
     * Public variables
     ************************************************************/
    val dataState: StateFlow<PlanCalendarDataHolder> = _dataState.asStateFlow()
    val taskState: StateFlow<PlanCalendarTempTaskHolder> = _taskState.asStateFlow()

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
                daysRepository.getDaysAfterThan(currentDate.minusMonths(24)),
                monthsRepository.getMonthsAfterThan(currentDate.minusMonths(24)),
                yearsRepository.getYearsAfterThan(currentDate.minusMonths(24))
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
    private fun PlanCalendarDayEntity.toDomain()         = PlanCalendarEntityDomain(this.bdId, this.title, this.date)
    private fun PlanCalendarMonthEntity.toDomain()       = PlanCalendarEntityDomain(this.bdId, this.title, this.date)
    private fun PlanCalendarYearEntity.toDomain()        = PlanCalendarEntityDomain(this.bdId, this.title, this.date)
    private fun PlanCalendarDayTaskEntity.toDomain()     = PlanCalendarTaskDomain(this.bdId, this.ownerId, this.title, this.description, this.isDone)
    private fun PlanCalendarMonthTaskEntity.toDomain()   = PlanCalendarTaskDomain(this.bdId, this.ownerId, this.title, this.description, this.isDone)
    private fun PlanCalendarYearTaskEntity.toDomain()    = PlanCalendarTaskDomain(this.bdId, this.ownerId, this.title, this.description, this.isDone)

    private fun PlanCalendarEntityDomain.toEntityDay()   = PlanCalendarDayEntity(this.id, this.title, this.date)
    private fun PlanCalendarEntityDomain.toEntityMonth() = PlanCalendarMonthEntity(this.id, this.title, this.date)
    private fun PlanCalendarEntityDomain.toEntityYear()  = PlanCalendarYearEntity(this.id, this.title, this.date)
    private fun PlanCalendarTaskDomain.toEntityDay()     = PlanCalendarDayTaskEntity(this.id, this.ownerId, this.title, this.description, this.isDone)
    private fun PlanCalendarTaskDomain.toEntityMonth()   = PlanCalendarMonthTaskEntity(this.id, this.ownerId, this.title, this.description, this.isDone)
    private fun PlanCalendarTaskDomain.toEntityYear()    = PlanCalendarYearTaskEntity(this.id, this.ownerId, this.title, this.description, this.isDone)

    private fun PlanCalendarEntityDomain.isEmpty(type: CalendarType): Boolean = when(type){
        CalendarType.DAYS   -> this.title.isNullOrBlank() && (_taskState.value.tasks.isEmpty())
        CalendarType.MONTHS -> this.title.isNullOrBlank() && (_taskState.value.tasks.isEmpty())
        CalendarType.YEARS  -> this.title.isNullOrBlank() && (_taskState.value.tasks.isEmpty())
    }

    private fun PlanCalendarEntityDomain.exist(type: CalendarType): Boolean = when(type){
        CalendarType.DAYS   -> _dataState.value.days.contains(this)
        CalendarType.MONTHS -> _dataState.value.months.contains(this)
        CalendarType.YEARS  -> _dataState.value.years.contains(this)
    }

    private fun PlanCalendarTaskDomain.isEmpty(): Boolean = this.title.isNullOrBlank() && this.description.isNullOrBlank()
    private fun PlanCalendarTaskDomain.exist(): Boolean = _taskState.value.tasks.contains(this)

    /*************************************************************
     * Public functions
     *************************************************************/
    /**
     * @param
     * @return None
     **/
    override fun setType(type: CalendarType) = _dataState.update { it.copy(type = type) }

    override fun getCurrentDateImpl(): LocalDate = currentDate

    override fun getEntity(type :CalendarType): List<PlanCalendarEntityDomain> = when(type) {
        CalendarType.DAYS   -> _dataState.value.days
        CalendarType.MONTHS -> _dataState.value.months
        CalendarType.YEARS  -> _dataState.value.years
    }

    override fun getEntityTasks(type: CalendarType, entity: PlanCalendarEntityDomain){
        _taskState.value = PlanCalendarTempTaskHolder()
        if(entity.exist(type)) {
            viewModelScope.launch {
                try {
                    val tasks = when (type) {
                        CalendarType.DAYS   -> { daysRepository.getTasksForDay(entity.id).first().map { day -> day.toDomain() } }
                        CalendarType.MONTHS -> { monthsRepository.getTasksForMonth(entity.id).first().map { month -> month.toDomain() } }
                        CalendarType.YEARS  -> { yearsRepository.getTasksForYear(entity.id).first().map { year -> year.toDomain() } }
                    }
                    _taskState.update { curState -> curState.copy(tasks = tasks) }

                } catch (e: Exception) {
                    _dataState.update { it.copy(error = "DB: get tasks error: ${e.message}") }
                }
            }
        }
    }


    /**
     * @param
     * @return None
     **/
    override fun updateEntity(type: CalendarType, entity: PlanCalendarEntityDomain, task: PlanCalendarTaskDomain) {
        _taskState.value = PlanCalendarTempTaskHolder() //clear temp tasks buffer
        viewModelScope.launch {
            try {

                if (entity.exist(type)) { //entity already exist
                    if(entity.isEmpty(type)) when(type) { //new entity is empty -> delete
                        CalendarType.DAYS   -> daysRepository.deleteDay(entity.toEntityDay())
                        CalendarType.MONTHS -> monthsRepository.deleteMonth(entity.toEntityMonth())
                        CalendarType.YEARS  -> yearsRepository.deleteYear(entity.toEntityYear())
                    }
                    else when(type) { //new entity is not empty -> update
                        CalendarType.DAYS   -> daysRepository.updateDay(entity.toEntityDay())
                        CalendarType.MONTHS -> monthsRepository.updateMonth(entity.toEntityMonth())
                        CalendarType.YEARS  -> yearsRepository.updateYear(entity.toEntityYear())
                    }
                }
                else when(type) { //entity isn exist -> create
                    CalendarType.DAYS   -> daysRepository.insertDay(entity.toEntityDay())
                    CalendarType.MONTHS -> monthsRepository.insertMonth(entity.toEntityMonth())
                    CalendarType.YEARS  -> yearsRepository.insertYear(entity.toEntityYear())
                }

                if (task.exist()) { //task of entity exist
                    if (task.isEmpty()) when (type) { //new tasks is empty -> delete
                        CalendarType.DAYS -> daysRepository.deleteDayTask(task.toEntityDay())
                        CalendarType.MONTHS -> monthsRepository.deleteMonthTask(task.toEntityMonth())
                        CalendarType.YEARS -> yearsRepository.deleteYearTask(task.toEntityYear())
                    }
                    else when (type) { //new task isnt empty -> update
                        CalendarType.DAYS -> daysRepository.updateDayTask(task.toEntityDay())
                        CalendarType.MONTHS -> monthsRepository.updateMonthTask(task.toEntityMonth())
                        CalendarType.YEARS -> yearsRepository.updateYearTask(task.toEntityYear())
                    }
                }
                else when(type) { //task of entity isn exist -> create
                    CalendarType.DAYS   -> daysRepository.insertDayTask(task.toEntityDay())
                    CalendarType.MONTHS -> monthsRepository.insertMonthTask(task.toEntityMonth())
                    CalendarType.YEARS  -> yearsRepository.insertYearTask(task.toEntityYear())
                }

            } catch (e: Exception) {
                _dataState.update { it.copy(error = "DB: Day error trying update entity: ${e.message}") }
            }
        }
    }
}

/*****************************************************************
 * End of class
 ****************************************************************/