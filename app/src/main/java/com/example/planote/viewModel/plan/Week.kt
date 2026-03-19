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
data class PlanWeekEntityDomain(
    val bdId: Long = 0,
    val title: String? = null,
    val description: String? = null,
    val isToggle: Boolean = false,
    val days: List<PlanWeekDayEntityDomain> = emptyList()
)

data class PlanWeekDayEntityDomain(
    val bdId: Long = 0,
    val ownerId: Long = 0,
    val title: String? = null,
    val description: String? = null,
    val num: Int = 0,
    val tasks: List<PlanWeekDayTaskEntityDomain> = emptyList()
)

data class PlanWeekDayTaskEntityDomain(
    val bdId: Long = 0,
    val ownerId: Long = 0,
    val title: String? = null,
    val time: LocalDate = LocalDate.MIN,
    val description: String? = null,
    val isDone : Boolean = false
)

data class PlanWeekDataHolder(
    val data: List<PlanWeekEntityDomain> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class PlanWeekLoadingStatus { IDLE, PROC, DONE }

/*****************************************************************
 * Interfaces
 ****************************************************************/
/*****************************************************************
 * Interface containing all public methods implementations of
 * viewModel class
 ****************************************************************/
interface PlanWeekImplements {
}

/*****************************************************************
 * Classes
 ****************************************************************/
/*****************************************************************
 * ViewModel class for week
 ****************************************************************/
@HiltViewModel
class PlanWeekViewModel @Inject constructor(private val weekRepository: PlanWeekRepository, ) : ViewModel(), PlanWeekImplements {
    /************************************************************
     * Private variables
     ************************************************************/
    private val _dataState = MutableStateFlow(PlanWeekDataHolder())

    /************************************************************
     * Public variables
     ************************************************************/
    val dataState: StateFlow<PlanWeekDataHolder> = _dataState.asStateFlow()

    /*************************************************************
     * Constructors and init
     *************************************************************/
    /**
     * Subscribe function to week data. Each time when
     * entity of data base will be changed this function will be
     * called and change the state tree of data with new
     * changed data from database
     **/
    init {
        viewModelScope.launch {
            combine(
                weekRepository.getWeeks(),
                weekRepository.getWeekDays(),
                weekRepository.getWeekDayTasks()
            ) { weeks, days, tasks ->
                val tasksGrouped  = tasks.map{it.toDomain()}.groupBy{it.ownerId}
                val daysWithTasks = days.map{it.toDomain().copy(tasks = tasksGrouped[it.bdId].orEmpty())}.groupBy{it.ownerId}
                val weeksWithDays = weeks.map{it.toDomain().copy(days = daysWithTasks[it.bdId].orEmpty())}
                weeksWithDays
            }.collect { tree ->
                _dataState.update { state -> state.copy(data = tree) }
            }
        }
    }

    /*************************************************************
     * Private functions
     *************************************************************/
    private fun PlanWeekEntity.toDomain() = PlanWeekEntityDomain(this.bdId, this.title, this.description, this.isToggle)
    private fun PlanWeekDayEntity.toDomain() = PlanWeekDayEntityDomain(this.bdId, this.ownerId, this.title, this.description, this.num)
    private fun PlanWeekDayTaskEntity.toDomain() = PlanWeekDayTaskEntityDomain(this.bdId, this.ownerId, this.title, this.time, this.description, this.isDone)

    private fun PlanWeekEntityDomain.toEntity()  = PlanWeekEntity(this.bdId, this.title, this.description, this.isToggle)
    private fun PlanWeekDayEntityDomain.toEntity() = PlanWeekDayEntity(this.bdId, this.ownerId, this.title, this.description, this.num)
    private fun PlanWeekDayTaskEntityDomain.toEntity() = PlanWeekDayTaskEntity(this.bdId, this.ownerId, this.title, this.time, this.description, this.isDone)

    private fun PlanWeekEntityDomain.isEmpty(): Boolean = this.title.isNullOrBlank() && this.days.isEmpty()
    private fun PlanWeekDayEntityDomain.isEmpty(): Boolean = this.title.isNullOrBlank() && this.description.isNullOrBlank() && this.tasks.isEmpty()
    private fun PlanWeekDayTaskEntityDomain.isEmpty(): Boolean = this.title.isNullOrBlank() && this.description.isNullOrBlank()

    /*************************************************************
     * Public functions
     *************************************************************/
/*****************************************************************
 * End of class
 ****************************************************************/
}