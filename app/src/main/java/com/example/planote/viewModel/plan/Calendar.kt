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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
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
    val date: LocalDate = LocalDate.MIN
)

data class PlanCalendarTaskDomain(
    val id: Long = 0,
    val ownerId: Long = 0,
    val title: String? = null,
    val description: String? = null,
    val isDone: Boolean = false,
)

data class PlanCalendarDataHolder(
    val days: List<PlanCalendarEntityDomain> = emptyList(),
    val months: List<PlanCalendarEntityDomain> = emptyList(),
    val years: List<PlanCalendarEntityDomain> = emptyList(),
    val type: PlanCalendarType = PlanCalendarType.DAYS,
    val error: String? = null
)

enum class PlanCalendarType { DAYS, MONTHS, YEARS }

data class PlanCalendarDialogDataHolder(
    val entity: PlanCalendarEntityDomain = PlanCalendarEntityDomain(),
    val tasks: List<PlanCalendarTaskDomain> = emptyList(),
    val editingTask: PlanCalendarTaskDomain = PlanCalendarTaskDomain(),
    val loading: PlanCalendarLoading = PlanCalendarLoading.Idle
)

sealed class PlanCalendarLoading {
    object Idle : PlanCalendarLoading()
    object Loading : PlanCalendarLoading()
    object Saving : PlanCalendarLoading()
    object Deleting : PlanCalendarLoading()
    data class Error(val message: String?) : PlanCalendarLoading()
}

enum class PlanCalendarDialogMode { VIEW, EDIT, TASK, IDLE }

/*****************************************************************
 * Interfaces
 ****************************************************************/
/*****************************************************************
 * Interface containing all public methods implementations of
 * viewModel class
 ****************************************************************/
/* Data methods */
interface PlanDataCalendarImplements {
    fun subscribeToData()
    fun changeType(type: PlanCalendarType) //Unload this editable task from local memory
}

/* Dialog methods */
interface PlanDialogCalendarImplements {
    /* CALENDARBLOCK */
    fun observeEntityTasks(entity: PlanCalendarEntityDomain, type: PlanCalendarType): Flow<List<PlanCalendarTaskDomain>> //Observe tasks for a day

    /* VIEW */
    fun loadEntityAndTasks(entity: PlanCalendarEntityDomain, type: PlanCalendarType) //Get cur entity and tasks from DB to local memory

    /* EDIT */
    fun updateEntity(entity: PlanCalendarEntityDomain) //Update entity in local memory with new params
    fun deleteTask(task:PlanCalendarTaskDomain)        //Delete task from list in memory
    fun updateTask(task:PlanCalendarTaskDomain)        //Update task from list in memory
    fun saveEntityAndTasks()                           //Save local entity and tasks from memory to DB
    fun discardEntityAndTasks()                        //Restore entity and tasks from DB
    fun clearEntityAndTasks()                          //Delete all data for this entity and tasks from DB

    /* TASK */
    fun loadEditTask(task:PlanCalendarTaskDomain)      //Get editable task from local memory to another local memory
    fun updateEditTask(task:PlanCalendarTaskDomain)    //Update editable task in local memory with new params
    fun saveEditTask()                                 //Save editable task to local tasks list
    fun discardEditTask()                              //Unload this editable task from local memory
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
) : ViewModel(), PlanDataCalendarImplements, PlanDialogCalendarImplements {
    /************************************************************
     * Private variables
     ************************************************************/
    private val _dataState = MutableStateFlow(PlanCalendarDataHolder())
    private val _dialogState = MutableStateFlow(PlanCalendarDialogDataHolder())

    private val currentDate: LocalDate
        get() = LocalDate.now()
    private var newId: Long = 0

    /************************************************************
     * Public variables
     ************************************************************/
    val dataState: StateFlow<PlanCalendarDataHolder> = _dataState.asStateFlow()
    val dialogState: StateFlow<PlanCalendarDialogDataHolder> = _dialogState.asStateFlow()

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
        subscribeToData()
    }

    /*************************************************************
     * Private functions
     *************************************************************/
    private fun nextId() :Long = --newId

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

    private fun PlanCalendarTaskDomain.isEmpty(): Boolean = this.title.isNullOrBlank() && this.description.isNullOrBlank()

    private fun PlanCalendarEntityDomain.isNew(type: PlanCalendarType): Boolean = when(type){
        PlanCalendarType.DAYS   -> _dataState.value.days.find{ it.id == this.id } == null
        PlanCalendarType.MONTHS -> _dataState.value.months.find{ it.id == this.id } == null
        PlanCalendarType.YEARS  -> _dataState.value.years.find{ it.id == this.id } == null
    }

    private fun PlanCalendarTaskDomain.isNew(): Boolean = this.id <= 0L

    /*************************************************************
     * Public functions @PlanDataCalendarImplements
     *************************************************************/
    override fun subscribeToData() {
        viewModelScope.launch {
            combine(
                daysRepository.getDaysAfterThan(currentDate.minusMonths(24)),
                monthsRepository.getMonthsAfterThan(currentDate.minusMonths(24)),
                yearsRepository.getYearsAfterThan(currentDate.minusYears(20))
            ) { days, months, years ->
                Triple(days, months, years)
            }.collect { (days, months, years) ->
                _dataState.update { state ->
                    state.copy(
                        days = days.map{entity -> entity.toDomain()},
                        months = months.map{entity -> entity.toDomain()},
                        years = years.map{entity -> entity.toDomain()},
                    )
                }
            }
        }
    }

    override fun changeType(type: PlanCalendarType) {
        _dataState.value = _dataState.value.copy(type = type)
    }

    /*************************************************************
     * Public functions @PlanDialogCalendarImplements
     *************************************************************/
    override fun observeEntityTasks(entity: PlanCalendarEntityDomain, type: PlanCalendarType): Flow<List<PlanCalendarTaskDomain>> =
        if (entity.id == 0L) flowOf(emptyList())
        else when (type) {
            PlanCalendarType.DAYS -> daysRepository.getTasksForDay(entity.id).map { it.map { e -> e.toDomain() } }
            PlanCalendarType.MONTHS -> monthsRepository.getTasksForMonth(entity.id).map { it.map { e -> e.toDomain() } }
            PlanCalendarType.YEARS -> yearsRepository.getTasksForYear(entity.id).map { it.map { e -> e.toDomain() } }
        }.distinctUntilChanged()

    /* VIEW */
    override fun loadEntityAndTasks(entity: PlanCalendarEntityDomain, type: PlanCalendarType) {
        newId = 0
        viewModelScope.launch {
            _dialogState.update { it.copy(loading = PlanCalendarLoading.Loading) }
            try {
                val loadedEntity = when(type){
                    PlanCalendarType.DAYS   -> { _dataState.value.days.find{ it.date == entity.date } ?: PlanCalendarEntityDomain(date = entity.date) }
                    PlanCalendarType.MONTHS -> { _dataState.value.months.find{ it.date == entity.date } ?: PlanCalendarEntityDomain(date = entity.date) }
                    PlanCalendarType.YEARS  -> { _dataState.value.years.find{ it.date == entity.date } ?: PlanCalendarEntityDomain(date = entity.date) }
                }

                val loadedTasks: List<PlanCalendarTaskDomain> = if(!loadedEntity.isNew(type)) {
                    when (type) {
                        PlanCalendarType.DAYS   -> { daysRepository.getTasksForDay(loadedEntity.id).first().map { day -> day.toDomain() } }
                        PlanCalendarType.MONTHS -> { monthsRepository.getTasksForMonth(loadedEntity.id).first().map { month -> month.toDomain() } }
                        PlanCalendarType.YEARS  -> { yearsRepository.getTasksForYear(loadedEntity.id).first().map { year -> year.toDomain() } }
                    }
                } else{
                    emptyList()
                }
                _dialogState.update {
                    it.copy(entity = loadedEntity, tasks = loadedTasks, loading = PlanCalendarLoading.Idle)
                }
            } catch (e: Exception) {
                _dialogState.update { it.copy(loading = PlanCalendarLoading.Error(e.message)) }
            }
        }
    }

    /* EDIT */
    override fun updateEntity(entity: PlanCalendarEntityDomain) {
        _dialogState.update { it.copy(entity = entity) }
    }

    override fun deleteTask(task: PlanCalendarTaskDomain) {
        val newTasks = if(task.id > 0) {
            _dialogState.value.tasks.map { _task -> if(_task.id == task.id) _task.copy(title = "", description = "") else _task }
        } else {
            _dialogState.value.tasks.filter { it.id != task.id }
        }
        _dialogState.update { it.copy(tasks = newTasks) }
    }

    override fun updateTask(task: PlanCalendarTaskDomain) {
        _dialogState.update { it.copy(tasks = it.tasks.map { _task -> if(_task.id == task.id) task else _task }) }
    }

    override fun saveEntityAndTasks() {
        val type = _dataState.value.type
        viewModelScope.launch {
            _dialogState.update { it.copy(loading = PlanCalendarLoading.Saving) }
            val entity = _dialogState.value.entity
            var entityId = entity.id
            val tasks = _dialogState.value.tasks
            val updatedTasks = mutableListOf<PlanCalendarTaskDomain>()
            try {
                if (!entity.isNew(type)) { //entity already exist
                    if(entity.isEmpty(type, tasks)){
                        when(type) { //new entity is empty -> delete
                            PlanCalendarType.DAYS   -> daysRepository.deleteDay(entity.toEntityDay())
                            PlanCalendarType.MONTHS -> monthsRepository.deleteMonth(entity.toEntityMonth())
                            PlanCalendarType.YEARS  -> yearsRepository.deleteYear(entity.toEntityYear())
                        }
                        _dialogState.update { it.copy(entity = PlanCalendarEntityDomain(date = entity.date), tasks = emptyList(), loading = PlanCalendarLoading.Idle) }
                        return@launch
                    }
                    else {
                        when (type) { //new entity is not empty -> update
                            PlanCalendarType.DAYS -> daysRepository.updateDay(entity.toEntityDay())
                            PlanCalendarType.MONTHS -> monthsRepository.updateMonth(entity.toEntityMonth())
                            PlanCalendarType.YEARS -> yearsRepository.updateYear(entity.toEntityYear())
                        }
                        _dialogState.update { it.copy(entity = entity) }
                    }
                }
                else if (!entity.isEmpty(type, tasks)) { //entity isn exist and not empty -> create
                    entityId = when (type) {
                        PlanCalendarType.DAYS -> daysRepository.insertDay(entity.toEntityDay())
                        PlanCalendarType.MONTHS -> monthsRepository.insertMonth(entity.toEntityMonth())
                        PlanCalendarType.YEARS -> yearsRepository.insertYear(entity.toEntityYear())
                    }
                    _dialogState.update { it.copy(entity = entity.copy(id = entityId)) }
                }
                else {
                    _dialogState.update { it.copy(loading = PlanCalendarLoading.Idle) }
                    return@launch
                }
                for (task in tasks) {
                    if(!task.isNew()) //task already exist
                    {
                        if (task.isEmpty()) {
                            when (type) { //tasks now is empty -> delete
                                PlanCalendarType.DAYS -> daysRepository.deleteDayTask(task.copy(ownerId = entityId).toEntityDay())
                                PlanCalendarType.MONTHS -> monthsRepository.deleteMonthTask(task.copy(ownerId = entityId).toEntityMonth())
                                PlanCalendarType.YEARS -> yearsRepository.deleteYearTask(task.copy(ownerId = entityId).toEntityYear())
                            }
                        }
                        else {
                            when (type) { //task now isnt empty -> update
                                PlanCalendarType.DAYS -> daysRepository.updateDayTask(task.copy(ownerId = entityId).toEntityDay())
                                PlanCalendarType.MONTHS -> monthsRepository.updateMonthTask(task.copy(ownerId = entityId).toEntityMonth())
                                PlanCalendarType.YEARS -> yearsRepository.updateYearTask(task.copy(ownerId = entityId).toEntityYear())
                            }
                            updatedTasks.add(task)
                        }
                    } else if (!task.isEmpty()) {
                        val taskId = when (type) { //task of entity isn exist and not empty -> create
                            PlanCalendarType.DAYS -> daysRepository.insertDayTask(task.copy(id = 0, ownerId = entityId).toEntityDay())
                            PlanCalendarType.MONTHS -> monthsRepository.insertMonthTask(task.copy(id = 0, ownerId = entityId).toEntityMonth())
                            PlanCalendarType.YEARS -> yearsRepository.insertYearTask(task.copy(id = 0, ownerId = entityId).toEntityYear())
                        }
                        updatedTasks.add(task.copy(id = taskId))
                    }
                    delay(5)
                }
                _dialogState.update { it.copy( tasks = updatedTasks, loading = PlanCalendarLoading.Idle) }
            } catch (e: Exception) {
                _dialogState.update { it.copy(loading = PlanCalendarLoading.Error(e.message)) }
            }
        }
    }

    override fun discardEntityAndTasks() {
        loadEntityAndTasks(_dialogState.value.entity, _dataState.value.type)
    }

    override fun clearEntityAndTasks() {
        _dialogState.update { it.copy(entity = PlanCalendarEntityDomain(id = it.entity.id, date = it.entity.date), tasks = emptyList()) }
        saveEntityAndTasks()
    }

    /* TASK */
    override fun loadEditTask(task: PlanCalendarTaskDomain) {
        _dialogState.update { it.copy(editingTask = task) }
    }

    override fun updateEditTask(task: PlanCalendarTaskDomain) {
        _dialogState.update { it.copy(editingTask = task) }
    }

    override fun saveEditTask() {
        val thisTask = _dialogState.value.editingTask
        val originTasks = _dialogState.value.tasks
        val thisTaskIndexInOrigin = originTasks.indexOfFirst { it.id == thisTask.id }
        val updatedTasks = if(thisTaskIndexInOrigin == -1) {
            if(!thisTask.isEmpty()) originTasks +thisTask.copy(id = nextId()) else { originTasks }
        } else {
            originTasks.map { task -> if(task.id == thisTask.id) thisTask else task }
        }
        _dialogState.update { it.copy(tasks = updatedTasks, editingTask = PlanCalendarTaskDomain()) }
    }

    override fun discardEditTask() {
        _dialogState.update { it.copy(editingTask = PlanCalendarTaskDomain()) }
    }
}

/*****************************************************************
 * End of class
 ****************************************************************/
