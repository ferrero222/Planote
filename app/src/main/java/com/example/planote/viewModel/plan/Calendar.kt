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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
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
    val type: PlanCalendarType = PlanCalendarType.DAYS,
    val error: String? = null
)

enum class PlanCalendarType { DAYS, MONTHS, YEARS }

enum class PlanCalendarLoadingStatus { IDLE, PROC, DONE }

/*****************************************************************
 * Interfaces
 ****************************************************************/
/*****************************************************************
 * Interface containing all public methods implementations of
 * viewModel class
 ****************************************************************/
interface PlanCalendarImplements {
    fun changeType(type: PlanCalendarType)

    fun getEntityTasks(
        status: PlanCalendarLoadingStatus,
        coroutineScope: CoroutineScope,
        type: PlanCalendarType,
        entity: PlanCalendarEntityDomain,
        onGetTasks: (List<PlanCalendarTaskDomain>) -> Unit,
        onStatusChange: (PlanCalendarLoadingStatus) -> Unit
    )

    fun updateEntityAndTasks(
        status: PlanCalendarLoadingStatus,
        coroutineScope: CoroutineScope,
        type: PlanCalendarType,
        entity: PlanCalendarEntityDomain,
        newTasks: List<PlanCalendarTaskDomain>,
        sourceTasks: List<PlanCalendarTaskDomain>,
        onStatusChange: (PlanCalendarLoadingStatus) -> Unit
    )
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
    private val currentDate: LocalDate
        get() = LocalDate.now()

    /************************************************************
     * Public variables
     ************************************************************/
    val dataState: StateFlow<PlanCalendarDataHolder> = _dataState.asStateFlow()

    /*************************************************************
     * Constructors and init
     *************************************************************/
    /**
     * Subscribe function to calendar data. Each time when
     * entity of data base will be changed this function will be
     * called and change the state of calendar data with new
     * changed data from database
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

    private fun PlanCalendarEntityDomain.isEmpty(type: PlanCalendarType, tasks: List<PlanCalendarTaskDomain>): Boolean = when(type){
        PlanCalendarType.DAYS   -> this.title.isNullOrBlank() && (tasks.isEmpty())
        PlanCalendarType.MONTHS -> this.title.isNullOrBlank() && (tasks.isEmpty())
        PlanCalendarType.YEARS  -> this.title.isNullOrBlank() && (tasks.isEmpty())
    }

    private fun PlanCalendarEntityDomain.exist(type: PlanCalendarType): Boolean = when(type){
        PlanCalendarType.DAYS   -> _dataState.value.days.find{ it.id == this.id } != null
        PlanCalendarType.MONTHS -> _dataState.value.months.find{ it.id == this.id } != null
        PlanCalendarType.YEARS  -> _dataState.value.years.find{ it.id == this.id } != null
    }

    private fun PlanCalendarEntityDomain.isSame(type: PlanCalendarType): Boolean = when(type){
        PlanCalendarType.DAYS   -> _dataState.value.days.find{ it == this} != null
        PlanCalendarType.MONTHS -> _dataState.value.months.find{ it == this } != null
        PlanCalendarType.YEARS  -> _dataState.value.years.find{ it == this } != null
    }

    private fun PlanCalendarTaskDomain.isEmpty(): Boolean = this.title.isNullOrBlank() && this.description.isNullOrBlank()

    private fun PlanCalendarTaskDomain.exist(source: List<PlanCalendarTaskDomain>): Boolean = source.find{ it.id == this.id } != null

    private fun PlanCalendarTaskDomain.isSame(source: List<PlanCalendarTaskDomain>): Boolean = source.find{ it == this } != null

    /*************************************************************
     * Public functions
     *************************************************************/
    override fun changeType(type: PlanCalendarType) {
        _dataState.value = _dataState.value.copy(type = type)
    }

    override fun getEntityTasks(
        status: PlanCalendarLoadingStatus,
        coroutineScope: CoroutineScope,
        type: PlanCalendarType,
        entity: PlanCalendarEntityDomain,
        onGetTasks: (List<PlanCalendarTaskDomain>) -> Unit,
        onStatusChange: (PlanCalendarLoadingStatus) -> Unit
    ){
        if(status == PlanCalendarLoadingStatus.IDLE){
            coroutineScope.launch {
                onStatusChange(PlanCalendarLoadingStatus.PROC)
                try {
                    val tasks: List<PlanCalendarTaskDomain> = if(entity.exist(type)) {
                        when (type) {
                            PlanCalendarType.DAYS   -> {  daysRepository.getTasksForDay(entity.id).first().map { day -> day.toDomain() } }
                            PlanCalendarType.MONTHS -> {  monthsRepository.getTasksForMonth(entity.id).first().map { month -> month.toDomain() } }
                            PlanCalendarType.YEARS  -> {  yearsRepository.getTasksForYear(entity.id).first().map { year -> year.toDomain() } }
                        }
                    } else{
                        emptyList()
                    }
                    onGetTasks(tasks)
                    onStatusChange(PlanCalendarLoadingStatus.DONE)
                } catch (e: Exception) {
                    onGetTasks(emptyList())
                    onStatusChange(PlanCalendarLoadingStatus.DONE)
                    throw RuntimeException("Fatal DB get entity error", e)
                }
            }
        }
    }

    override fun updateEntityAndTasks(
        status: PlanCalendarLoadingStatus,
        coroutineScope: CoroutineScope,
        type: PlanCalendarType,
        entity: PlanCalendarEntityDomain,
        newTasks: List<PlanCalendarTaskDomain>,
        sourceTasks: List<PlanCalendarTaskDomain>,
        onStatusChange: (PlanCalendarLoadingStatus) -> Unit
    ){
        if(status == PlanCalendarLoadingStatus.IDLE){
            coroutineScope.launch {
                onStatusChange(PlanCalendarLoadingStatus.PROC)
                try {
                    if (entity.exist(type)) { //entity already exist
                        if(entity.isEmpty(type, sourceTasks)) when(type) { //new entity is empty -> delete
                            PlanCalendarType.DAYS   -> daysRepository.deleteDay(entity.toEntityDay())
                            PlanCalendarType.MONTHS -> monthsRepository.deleteMonth(entity.toEntityMonth())
                            PlanCalendarType.YEARS  -> yearsRepository.deleteYear(entity.toEntityYear())
                        }
                        else if(!entity.isSame(type)) when(type) { //new entity is not empty and isnt same -> update
                            PlanCalendarType.DAYS   -> daysRepository.updateDay(entity.toEntityDay())
                            PlanCalendarType.MONTHS -> monthsRepository.updateMonth(entity.toEntityMonth())
                            PlanCalendarType.YEARS  -> yearsRepository.updateYear(entity.toEntityYear())
                        }
                    }
                    else if (!entity.isEmpty(type, sourceTasks)) when(type) { //entity isn exist and not empty -> create
                        PlanCalendarType.DAYS   -> daysRepository.insertDay(entity.toEntityDay())
                        PlanCalendarType.MONTHS -> monthsRepository.insertMonth(entity.toEntityMonth())
                        PlanCalendarType.YEARS  -> yearsRepository.insertYear(entity.toEntityYear())
                    }

                    for (task in newTasks) {
                        if (task.exist(sourceTasks)) { //task of entity exist
                            if (task.isEmpty()) when (type) { //new tasks is empty -> delete
                                PlanCalendarType.DAYS -> daysRepository.deleteDayTask(task.toEntityDay())
                                PlanCalendarType.MONTHS -> monthsRepository.deleteMonthTask(task.toEntityMonth())
                                PlanCalendarType.YEARS -> yearsRepository.deleteYearTask(task.toEntityYear())
                            }
                            else if(!task.isSame(sourceTasks)) when (type) { //new task isnt empty -> update
                                PlanCalendarType.DAYS -> daysRepository.updateDayTask(task.toEntityDay())
                                PlanCalendarType.MONTHS -> monthsRepository.updateMonthTask(task.toEntityMonth())
                                PlanCalendarType.YEARS -> yearsRepository.updateYearTask(task.toEntityYear())
                            }
                        } else if (!task.isEmpty()) when (type) { //task of entity isn exist and not empty -> create
                            PlanCalendarType.DAYS -> daysRepository.insertDayTask(task.toEntityDay())
                            PlanCalendarType.MONTHS -> monthsRepository.insertMonthTask(task.toEntityMonth())
                            PlanCalendarType.YEARS -> yearsRepository.insertYearTask(task.toEntityYear())
                        }
                        delay(5)
                    }

                    onStatusChange(PlanCalendarLoadingStatus.DONE)
                } catch (e: Exception) {
                    onStatusChange(PlanCalendarLoadingStatus.DONE)
                    throw RuntimeException("Fatal DB update entity error", e)

                }
            }
        }

    }
}

/*****************************************************************
 * End of class
 ****************************************************************/