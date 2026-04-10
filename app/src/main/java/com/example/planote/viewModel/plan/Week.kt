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
import com.example.planote.model.plan.repository.PlanWeekRepository
import com.example.planote.model.plan.repository.source.local.room.entity.PlanWeekDayEntity
import com.example.planote.model.plan.repository.source.local.room.entity.PlanWeekDayTaskEntity
import com.example.planote.model.plan.repository.source.local.room.entity.PlanWeekEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
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
data class PlanWeekDomain(
    val id: Long = 0,
    val title: String? = null,
    val description: String? = null,
    val isToggle: Boolean = false,
)

data class PlanWeekDayDomain(
    val id: Long = 0,
    val ownerId: Long = 0,
    val title: String? = null,
    val num: Int = 0,
)

data class PlanWeekDayTaskDomain(
    val id: Long = 0,
    val ownerId: Long = 0,
    val title: String? = null,
    val time: LocalTime = LocalTime.MIDNIGHT,
    val description: String? = null,
    val isDone : Boolean = false
)

data class PlanWeekDataHolder(
    val weeks: List<PlanWeekDomain> = emptyList(),
    val days: List<PlanWeekDayDomain> = emptyList(),
)

data class PlanWeekDialogPlanDataHolder(
    val weeks: List<PlanWeekDomain> = emptyList(),
    val editWeek: PlanWeekDomain = PlanWeekDomain(),
    val loading: PlanWeekLoading = PlanWeekLoading.Idle
)

data class PlanWeekDialogDayDataHolder(
    val day: PlanWeekDayDomain = PlanWeekDayDomain(),
    val tasks: List<PlanWeekDayTaskDomain> = emptyList(),
    val editingTask: PlanWeekDayTaskDomain = PlanWeekDayTaskDomain(),
    val loading: PlanWeekLoading = PlanWeekLoading.Idle
)

sealed class PlanWeekLoading {
    object Idle : PlanWeekLoading()
    object Loading : PlanWeekLoading()
    object Saving : PlanWeekLoading()
    object Deleting : PlanWeekLoading()
    data class Error(val message: String?) : PlanWeekLoading()
}

enum class PlanWeekDialogMode { DAYVIEW, DAYEDIT, DAYTASK, PLANCHANGE, PLANADD, IDLE }

/*****************************************************************
 * Interfaces
 ****************************************************************/
/*****************************************************************
 * Interface containing all public methods implementations of
 * viewModel class
 ****************************************************************/
/* Data methods */
interface PlanWeekDataImplements {
    fun subscribeToData()                          //Subscribe to week data from bd used in UI
}

/* Dialog methods */
interface PlanWeekDialogImplements {

    /* DAYBLOCK */
    suspend fun observeDayTasks(day: PlanWeekDayDomain): Flow<List<PlanWeekDayTaskDomain>> //Observe tasks for a day reactively

    /* DAYVIEW */
    fun loadDayAndTasks(day: PlanWeekDayDomain)     //Get cur day and tasks from DB to local memory

    /* DAYEDIT */
    fun updateDay(day: PlanWeekDayDomain)           //Update day in local memory with new params
    fun deleteTask(task: PlanWeekDayTaskDomain)     //Delete task from list in memory
    fun updateTask(task: PlanWeekDayTaskDomain)     //Update task from list in memory
    fun saveDayAndTasks()                           //Save local day and tasks from memory to DB
    fun discardDayAndTasks()                        //Discard day and tasks changes
    fun clearDayAndTasks()                          //Delete all data for this day and tasks from DB

    /* DAYTASK */
    fun loadEditTask(task: PlanWeekDayTaskDomain)   //Get editable task from local memory to another local memory
    fun updateEditTask(task: PlanWeekDayTaskDomain) //Update editable task in local memory with new params
    fun saveEditTask()                              //Save editable task to local tasks list
    fun discardEditTask()                           //Unload this editable task from local memory

    /* PLANCHANGE */
    fun loadWeeks()                                 //Get cur weeks from DB to local memory
    fun updateWeek(week: PlanWeekDomain)            //Update week from list in memory
    fun saveWeeks()                                 //Save local weeks from memory to DB
    fun discardWeeks()                              //Discard weeks

    /* PLANADD */
    fun loadEditWeek(week: PlanWeekDomain)          //Get editable week from local memory to another local memory
    fun updateEditWeek(week: PlanWeekDomain)        //Update editable week in local memory with new params
    fun saveEditWeek()                              //Save editable week to local tasks list
    fun discardEditWeek()                           //Unload this editable week from local memory
}

/*****************************************************************
 * Classes
 ****************************************************************/
/*****************************************************************
 * ViewModel class for week
 ****************************************************************/
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PlanWeekViewModel @Inject constructor(
    private val weekRepository: PlanWeekRepository,
) : ViewModel(), PlanWeekDialogImplements, PlanWeekDataImplements {
    /************************************************************
     * Private variables
     ************************************************************/
    private val _dataState = MutableStateFlow(PlanWeekDataHolder())
    private val _dialogDayState = MutableStateFlow(PlanWeekDialogDayDataHolder())
    private val _dialogPlanState = MutableStateFlow(PlanWeekDialogPlanDataHolder())

    private val currentDate: LocalDate
        get() = LocalDate.now()
    private var newId: Long = 0

    /************************************************************
     * Public variables
     ************************************************************/
    val dataState: StateFlow<PlanWeekDataHolder> = _dataState.asStateFlow()
    val dialogDayState: StateFlow<PlanWeekDialogDayDataHolder> = _dialogDayState.asStateFlow()
    val dialogPlanState: StateFlow<PlanWeekDialogPlanDataHolder> = _dialogPlanState.asStateFlow()

    /*************************************************************
     * Constructors and init
     *************************************************************/
    init {
        subscribeToData()
    }

    /*************************************************************
     * Private functions
     *************************************************************/
    private fun nextId() :Long = --newId

    private fun PlanWeekEntity.toDomain() = PlanWeekDomain(this.bdId, this.title, this.description, this.isToggle)
    private fun PlanWeekDayEntity.toDomain() = PlanWeekDayDomain(this.bdId, this.ownerId, this.title, this.num)
    private fun PlanWeekDayTaskEntity.toDomain() = PlanWeekDayTaskDomain(this.bdId, this.ownerId, this.title, this.time, this.description, this.isDone)

    private fun PlanWeekDomain.toEntity() = PlanWeekEntity(this.id, this.title, this.description, this.isToggle)
    private fun PlanWeekDayDomain.toEntity() = PlanWeekDayEntity(this.id, this.ownerId, this.title, this.num)
    private fun PlanWeekDayTaskDomain.toEntity() = PlanWeekDayTaskEntity(this.id, this.ownerId, this.title, this.time, this.description, this.isDone)

    private fun PlanWeekDomain.isNew(): Boolean = this.id <= 0L
    private fun PlanWeekDayDomain.isNew(): Boolean = this.id <= 0L
    private fun PlanWeekDayTaskDomain.isNew(): Boolean = this.id <= 0L

    private fun PlanWeekDomain.isEmpty(): Boolean {
        return this.title.isNullOrBlank() && this.description.isNullOrBlank()
    }
    private fun PlanWeekDayDomain.isEmpty(tasks: List<PlanWeekDayTaskDomain>): Boolean {
        return this.title.isNullOrBlank() && (tasks.isEmpty())
    }
    private fun PlanWeekDayTaskDomain.isEmpty(): Boolean {
        return this.title.isNullOrBlank() && this.description.isNullOrBlank()
    }

    private fun checkToggleCorrect() {
        _dialogPlanState.update { currentState ->
            val validWeeks = currentState.weeks.filter { !it.title.isNullOrEmpty() || !it.description.isNullOrEmpty() }
            if (validWeeks.size == 1) {
                currentState.copy(
                    weeks = currentState.weeks.map { week -> if (week.id == validWeeks[0].id) week.copy(isToggle = true) else week }
                )
            } else {
                currentState
            }
        }
    }

    private fun sortTimeTasks(tasks: List<PlanWeekDayTaskDomain>) : List<PlanWeekDayTaskDomain>{
        return tasks.sortedWith { a, b ->
            val aHasTime = a.time != LocalTime.MIDNIGHT
            val bHasTime = b.time != LocalTime.MIDNIGHT
            when {
                aHasTime && bHasTime -> a.time.compareTo(b.time)
                aHasTime && !bHasTime -> -1
                !aHasTime && bHasTime -> 1
                else -> 0
            }
        }
    }

    /*************************************************************
     * Public functions @PlanWeekDataImplements
     *************************************************************/
    override fun subscribeToData() {
        viewModelScope.launch {
            val selectedWeekIdFlow = MutableStateFlow(0L)
            launch {
                weekRepository.getWeeks().collect { weeks ->
                    _dataState.update { state -> state.copy(weeks = weeks.map { it.toDomain() }) }
                    val hasToggled = weeks.any { it.isToggle }
                    if(!hasToggled && weeks.isNotEmpty()) { selectedWeekIdFlow.value = weeks.first().bdId }
                }
            }
            launch {
                _dataState.collect { state ->
                    val toggled = state.weeks.find { it.isToggle }
                    if (toggled != null && toggled.id != selectedWeekIdFlow.value) { selectedWeekIdFlow.value = toggled.id }
                }
            }
            launch {
                selectedWeekIdFlow
                    .flatMapLatest { weekId -> if (weekId == 0L)  emptyFlow()  else weekRepository.getWeekDays(weekId) }
                    .collect { days -> _dataState.update { state -> state.copy(days = days.map { it.toDomain() }) } }
            }
        }
    }

    /*************************************************************
     * Public functions @PlanWeekDialogImplements
     *************************************************************/
    /* DAYBLOCK */
    fun observeDayTasksForDayIndex(dayIndex: Int): Flow<List<PlanWeekDayTaskDomain>> {
        return _dataState
            .map { state -> state.weeks.find { it.isToggle }?.id ?: 0L }
            .distinctUntilChanged()
            .flatMapLatest { weekId ->
                if (weekId == 0L) return@flatMapLatest flowOf(emptyList())
                weekRepository.getWeekDays(weekId).flatMapLatest { days ->
                    val day = days.find { it.num == dayIndex }
                    if (day == null || day.bdId <= 0L) flowOf(emptyList())
                    else weekRepository.getWeekDayTasks(day.bdId).map { tasks -> tasks.map { it.toDomain() } }
                }
            }
            .map { tasks -> sortTimeTasks(tasks) }
    }

    /* DAYBLOCK */
    override suspend fun observeDayTasks(day: PlanWeekDayDomain): Flow<List<PlanWeekDayTaskDomain>> {
        return if (day.id <= 0L) emptyFlow()
        else weekRepository.getWeekDayTasks(day.id).map { tasks -> sortTimeTasks(tasks.map { it.toDomain() }) }
    }

    /* DAYVIEW */
    override fun loadDayAndTasks(day: PlanWeekDayDomain) {
        newId = 0
        viewModelScope.launch {
            _dialogDayState.update { it.copy(loading = PlanWeekLoading.Loading) }
            try {
                val days = _dataState.value.days
                val weekId = _dataState.value.weeks.find { it.isToggle }?.id ?: 0L
                val loadedDay = days.find{ it.num == day.num } ?: PlanWeekDayDomain(num = day.num, ownerId = weekId)
                val loadedTasks = if(!loadedDay.isNew()) weekRepository.getWeekDayTasks(loadedDay.id).first().map{ task -> task.toDomain() } else emptyList()

                _dialogDayState.update {
                    it.copy(day = loadedDay, tasks = sortTimeTasks(loadedTasks), loading = PlanWeekLoading.Idle)
                }
            } catch (e: Exception) {
                _dialogDayState.update { it.copy(loading = PlanWeekLoading.Error(e.message)) }
            }
        }
    }

    /* DAYEDIT */
    override fun updateDay(day: PlanWeekDayDomain) {
        _dialogDayState.update { it.copy(day = day) }
    }

    override fun deleteTask(task: PlanWeekDayTaskDomain) {
        val newTasks = if(task.id > 0) {
            _dialogDayState.value.tasks.map { _task -> if(_task.id == task.id) _task.copy(title = "", description = "") else _task }
        } else {
            _dialogDayState.value.tasks.filter { it.id != task.id }
        }
        _dialogDayState.update { it.copy(tasks = newTasks) }
    }

    override fun updateTask(task: PlanWeekDayTaskDomain) {
        _dialogDayState.update{
            it.copy(
                tasks = sortTimeTasks(
                    it.tasks.map { _task -> if(_task.id == task.id) task else _task }
                )
            )
        }
    }

    override fun saveDayAndTasks() {
        viewModelScope.launch {
            _dialogDayState.update { it.copy(loading = PlanWeekLoading.Saving) }
            val day = _dialogDayState.value.day
            var dayId: Long = day.id
            val tasks = _dialogDayState.value.tasks
            val updatedTasks = mutableListOf<PlanWeekDayTaskDomain>()
            try {
                if (!day.isNew()) { //entity already exist
                    if(day.isEmpty(tasks)){
                        weekRepository.deleteWeekDay(day.toEntity()) //new entity is empty -> delete
                        _dialogDayState.update{ it.copy(day = PlanWeekDayDomain(num = day.num), tasks = emptyList(), loading = PlanWeekLoading.Idle) }
                        return@launch
                    }
                    else {
                        weekRepository.updateWeekDay(day.toEntity()) //new entity is not empty -> update
                        _dialogDayState.update { it.copy(day = day) }
                    }
                }
                else if (!day.isEmpty(tasks)) { //entity isn exist and not empty -> create
                    dayId = weekRepository.insertWeekDay(day.toEntity())
                    _dialogDayState.update { it.copy(day = day.copy(id = dayId)) }
                }
                else {
                    _dialogDayState.update { it.copy(loading = PlanWeekLoading.Idle) }
                    return@launch
                }
                for (task in tasks) {
                    if(!task.isNew()) //task already exist
                    {
                        if (task.isEmpty()) { //tasks now is empty -> delete
                            weekRepository.deleteWeekDayTask(task.copy(ownerId = dayId).toEntity())
                        }
                        else { //task now isnt empty -> update
                            weekRepository.updateWeekDayTask(task.copy(ownerId = dayId).toEntity())
                            updatedTasks.add(task)
                        }
                    } else if (!task.isEmpty()) { //task of entity isn exist and not empty -> create
                        val taskId = weekRepository.insertWeekDayTask(task.copy(id = 0, ownerId = dayId).toEntity())
                        updatedTasks.add(task.copy(id = taskId))
                    }
                    delay(5)
                }
                _dialogDayState.update { it.copy( tasks = updatedTasks, loading = PlanWeekLoading.Idle) }
            } catch (e: Exception) {
                _dialogDayState.update { it.copy(loading = PlanWeekLoading.Error(e.message)) }
            }
        }
    }

    override fun discardDayAndTasks() {
        loadDayAndTasks(_dialogDayState.value.day)
    }

    override fun clearDayAndTasks() {
        _dialogDayState.update { it.copy(day = PlanWeekDayDomain(it.day.id), tasks = emptyList()) }
        saveDayAndTasks()
    }

    /* DAYTASK */
    override fun loadEditTask(task: PlanWeekDayTaskDomain) {
        _dialogDayState.update { it.copy(editingTask = task) }
    }

    override fun updateEditTask(task: PlanWeekDayTaskDomain) {
        _dialogDayState.update { it.copy(editingTask = task) }
    }

    override fun saveEditTask() {
        val thisTask = _dialogDayState.value.editingTask
        val originTasks = _dialogDayState.value.tasks
        val thisTaskIndexInOrigin = originTasks.indexOfFirst { it.id == thisTask.id }
        val updatedTasks = if(thisTaskIndexInOrigin == -1) {
            if(!thisTask.isEmpty()) originTasks +thisTask.copy(id = nextId()) else { originTasks }
        } else {
            originTasks.map { task -> if(task.id == thisTask.id) thisTask else task }
        }
        _dialogDayState.update { it.copy(tasks = sortTimeTasks(updatedTasks), editingTask = PlanWeekDayTaskDomain()) }
    }

    override fun discardEditTask() {
        _dialogDayState.update { it.copy(editingTask = PlanWeekDayTaskDomain()) }
    }

    /* PLANCHANGE */
    override fun loadWeeks() {
        newId = 0
        viewModelScope.launch {
            _dialogPlanState.update { it.copy(loading = PlanWeekLoading.Loading) }
            try {
                val loadedWeeks = weekRepository.getWeeks().first().map { week -> week.toDomain() }
                _dialogPlanState.update {
                    it.copy(weeks = loadedWeeks, loading = PlanWeekLoading.Idle)
                }
            } catch (e: Exception) {
                _dialogPlanState.update { it.copy(loading = PlanWeekLoading.Error(e.message)) }
            }
        }
    }

    override fun updateWeek(week: PlanWeekDomain) {
        _dialogPlanState.update{ it.copy(weeks = it.weeks.map { _week -> if(_week.id == week.id) week else _week }) }
        checkToggleCorrect()
    }

    override fun saveWeeks() {
        viewModelScope.launch {
            _dialogPlanState.update { it.copy(loading = PlanWeekLoading.Saving) }
            val weeks = _dialogPlanState.value.weeks
            val updatedWeeks = mutableListOf<PlanWeekDomain>()
            try {
                for (week in weeks) {
                    if(!week.isNew()) //week already exist
                    {
                        if (week.isEmpty()) { //week now is empty -> delete
                            weekRepository.deleteWeek(week.toEntity())
                        }
                        else { //week now isnt empty -> update
                            weekRepository.updateWeek(week.toEntity())
                            updatedWeeks.add(week)
                        }
                    } else if (!week.isEmpty()) { //task of entity isn exist and not empty -> create
                        val weekId = weekRepository.insertWeek(week.copy(id = 0).toEntity())
                        updatedWeeks.add(week.copy(id = weekId))
                    }
                    delay(5)
                }
                _dialogPlanState.update { it.copy( weeks = updatedWeeks, loading = PlanWeekLoading.Idle) }
            } catch (e: Exception) {
                _dialogPlanState.update { it.copy(loading = PlanWeekLoading.Error(e.message)) }
            }
        }
    }

    override fun discardWeeks() {
        loadWeeks()
    }

    /* PLANADD */
    override fun loadEditWeek(week: PlanWeekDomain) {
        _dialogPlanState.update { it.copy(editWeek = week) }
    }

    override fun updateEditWeek(week: PlanWeekDomain) {
        _dialogPlanState.update { it.copy(editWeek = week) }
    }

    override fun saveEditWeek() {
        val thisWeek = _dialogPlanState.value.editWeek
        val originWeeks = _dialogPlanState.value.weeks
        val thisWeekIndexInOrigin = originWeeks.indexOfFirst { it.id == thisWeek.id }
        val updatedWeeks = if(thisWeekIndexInOrigin == -1) {
            if(!thisWeek.isEmpty()) originWeeks +thisWeek.copy(id = nextId()) else { originWeeks }
        } else {
            originWeeks.map { week -> if(week.id == thisWeek.id) thisWeek else week }
        }
        _dialogPlanState.update { it.copy(weeks = updatedWeeks, editWeek = PlanWeekDomain()) }
        checkToggleCorrect()
    }

    override fun discardEditWeek() {
        _dialogPlanState.update { it.copy(editWeek = PlanWeekDomain()) }
    }
}

/*****************************************************************
 * End of class
 ****************************************************************/