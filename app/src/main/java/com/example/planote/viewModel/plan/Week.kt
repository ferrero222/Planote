///*****************************************************************
// *  Package for MVVM plan data repository
// *  @author Ferrero
// *  @date 21.08.2025
// ****************************************************************/
//package com.example.planote.viewModel.plan
//
///*****************************************************************
// * Imported packages
// ****************************************************************/
//import android.R.attr.type
//import androidx.lifecycle.ViewModel
//import com.example.planote.model.plan.repository.PlanCalendarDaysRepository
//import com.example.planote.model.plan.repository.PlanCalendarMonthsRepository
//import com.example.planote.model.plan.repository.PlanCalendarYearsRepository
//import com.example.planote.model.plan.repository.PlanWeekRepository
//import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarDayEntity
//import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarDayTaskEntity
//import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarMonthEntity
//import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarMonthTaskEntity
//import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarYearEntity
//import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarYearTaskEntity
//import com.example.planote.model.plan.repository.source.local.room.entity.PlanWeekDayEntity
//import com.example.planote.model.plan.repository.source.local.room.entity.PlanWeekDayTaskEntity
//import com.example.planote.model.plan.repository.source.local.room.entity.PlanWeekEntity
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.launch
//import java.time.LocalDate
//import javax.inject.Inject
//
///*****************************************************************
// * Data
// ****************************************************************/
//data class PlanWeekEntityDomain(
//    val bdId: Long = 0,
//    val title: String? = null)
//
//data class PlanWeekDayEntityDomain(
//    val bdId: Long = 0,
//    val ownerId: Long = 0,
//    val title: String? = null,
//    val description: String? = null)
//
//data class PlanWeekDayTaskEntityDomain(
//    val bdId: Long = 0,
//    val ownerId: Long = 0,
//    val title: String? = null,
//    val time: LocalDate = LocalDate.MIN,
//    val description: String? = null,
//    val isDone : Boolean = false)
//
//enum class PlanWeekLoadingStatus { IDLE, PROC, DONE }
//
///*****************************************************************
// * Interfaces
// ****************************************************************/
///*****************************************************************
// * Interface containing all public methods implementations of
// * viewModel class
// ****************************************************************/
//interface PlanWeekImplements {
//}
//
///*****************************************************************
// * Classes
// ****************************************************************/
///*****************************************************************
// * ViewModel class for calendar
// ****************************************************************/
//@HiltViewModel
//class PlanCalendarViewModel @Inject constructor(private val weekRepository: PlanWeekRepository, ) : ViewModel(), PlanWeekImplements {
//    /************************************************************
//     * Private variables
//     ************************************************************/
//    private val _dataState = MutableStateFlow(PlanCalendarDataHolder())
//
//    /************************************************************
//     * Public variables
//     ************************************************************/
//    val dataState: StateFlow<PlanCalendarDataHolder> = _dataState.asStateFlow()
//
//    /*************************************************************
//     * Private functions
//     *************************************************************/
//    private fun PlanWeekEntity.toDomain() = PlanWeekEntityDomain(this.bdId, this.title)
//    private fun PlanWeekDayEntity.toDomain() = PlanWeekDayEntityDomain(this.bdId, this.ownerId, this.title, this.description)
//    private fun PlanWeekDayTaskEntity.toDomain() = PlanWeekDayTaskEntityDomain(this.bdId, this.ownerId, this.title, this.time, this.description, this.isDone)
//
//    private fun PlanWeekEntityDomain.toEntity()   = PlanWeekEntity(this.bdId, this.title)
//    private fun PlanWeekDayEntityDomain.toEntity() = PlanWeekDayEntity(this.bdId, this.ownerId, this.title, this.description)
//    private fun PlanWeekDayTaskEntityDomain.toEntity()  = PlanWeekDayTaskEntity(this.bdId, this.ownerId, this.title, this.time, this.description, this.isDone)
//
//    private fun PlanWeekEntityDomain.isEmpty(): Boolean = this.title.isNullOrBlank()
//    private fun PlanWeekDayEntityDomain.isEmpty(): Boolean = this.title.isNullOrBlank() && this.description.isNullOrBlank()
//    private fun PlanWeekDayTaskEntityDomain.isEmpty(): Boolean = this.title.isNullOrBlank() && this.description.isNullOrBlank()
//
//    private fun PlanWeekEntityDomain.exist(weeks: List<PlanWeekEntityDomain>): Boolean = weeks.find {it.id == this.id} != null
//
//
//    private fun PlanCalendarEntityDomain.exist(type: PlanCalendarType): Boolean = when(type){
//        PlanCalendarType.DAYS   -> _dataState.value.days.find{ it.id == this.id } != null
//        PlanCalendarType.MONTHS -> _dataState.value.months.find{ it.id == this.id } != null
//        PlanCalendarType.YEARS  -> _dataState.value.years.find{ it.id == this.id } != null
//    }
//
//    private fun PlanCalendarEntityDomain.isSame(type: PlanCalendarType): Boolean = when(type){
//        PlanCalendarType.DAYS   -> _dataState.value.days.find{ it == this} != null
//        PlanCalendarType.MONTHS -> _dataState.value.months.find{ it == this } != null
//        PlanCalendarType.YEARS  -> _dataState.value.years.find{ it == this } != null
//    }
//
//    private fun PlanCalendarTaskDomain.isEmpty(): Boolean = this.title.isNullOrBlank() && this.description.isNullOrBlank()
//
//    private fun PlanCalendarTaskDomain.exist(source: List<PlanCalendarTaskDomain>): Boolean = source.find{ it.id == this.id } != null
//
//    private fun PlanCalendarTaskDomain.isSame(source: List<PlanCalendarTaskDomain>): Boolean = source.find{ it == this } != null
//
//    /*************************************************************
//     * Public functions
//     *************************************************************/
//    override fun changeType(type: PlanCalendarType) {
//        _dataState.value = _dataState.value.copy(type = type)
//    }
//
//    override fun getEntityTasks(
//        status: PlanCalendarLoadingStatus,
//        coroutineScope: CoroutineScope,
//        type: PlanCalendarType,
//        entity: PlanCalendarEntityDomain,
//        onGetTasks: (List<PlanCalendarTaskDomain>) -> Unit,
//        onStatusChange: (PlanCalendarLoadingStatus) -> Unit
//    ){
//        if(status == PlanCalendarLoadingStatus.IDLE){
//            coroutineScope.launch {
//                onStatusChange(PlanCalendarLoadingStatus.PROC)
//                try {
//                    val tasks: List<PlanCalendarTaskDomain> = if(entity.exist(type)) {
//                        when (type) {
//                            PlanCalendarType.DAYS   -> {  daysRepository.getTasksForDay(entity.id).first().map { day -> day.toDomain() } }
//                            PlanCalendarType.MONTHS -> {  monthsRepository.getTasksForMonth(entity.id).first().map { month -> month.toDomain() } }
//                            PlanCalendarType.YEARS  -> {  yearsRepository.getTasksForYear(entity.id).first().map { year -> year.toDomain() } }
//                        }
//                    } else{
//                        emptyList()
//                    }
//                    onGetTasks(tasks)
//                    onStatusChange(PlanCalendarLoadingStatus.DONE)
//                } catch (e: Exception) {
//                    onGetTasks(emptyList())
//                    onStatusChange(PlanCalendarLoadingStatus.DONE)
//                    throw RuntimeException("Fatal DB get entity error", e)
//                }
//            }
//        }
//    }
//
//    override fun updateEntityAndTasks(
//        status: PlanCalendarLoadingStatus,
//        coroutineScope: CoroutineScope,
//        type: PlanCalendarType,
//        entity: PlanCalendarEntityDomain,
//        newTasks: List<PlanCalendarTaskDomain>,
//        sourceTasks: List<PlanCalendarTaskDomain>,
//        onStatusChange: (PlanCalendarLoadingStatus) -> Unit
//    ){
//        var entityId = entity.id
//        if(status == PlanCalendarLoadingStatus.IDLE){
//            coroutineScope.launch {
//                onStatusChange(PlanCalendarLoadingStatus.PROC)
//                try {
//                    if (entity.exist(type)) { //entity already exist
//                        if(entity.isEmpty(type, sourceTasks) && entity.isEmpty(type, newTasks)){
//                            when(type) { //new entity is empty -> delete
//                                PlanCalendarType.DAYS   -> daysRepository.deleteDay(entity.toEntityDay())
//                                PlanCalendarType.MONTHS -> monthsRepository.deleteMonth(entity.toEntityMonth())
//                                PlanCalendarType.YEARS  -> yearsRepository.deleteYear(entity.toEntityYear())
//                            }
//                            onStatusChange(PlanCalendarLoadingStatus.DONE)
//                            return@launch
//                        }
//                        else if(!entity.isSame(type)) when(type) { //new entity is not empty and isnt same -> update
//                            PlanCalendarType.DAYS   -> daysRepository.updateDay(entity.toEntityDay())
//                            PlanCalendarType.MONTHS -> monthsRepository.updateMonth(entity.toEntityMonth())
//                            PlanCalendarType.YEARS  -> yearsRepository.updateYear(entity.toEntityYear())
//                        }
//                    }
//                    else if (!entity.isEmpty(type, sourceTasks) || !entity.isEmpty(type, newTasks)) entityId = when(type) { //entity isn exist and not empty -> create
//                        PlanCalendarType.DAYS   -> daysRepository.insertDay(entity.toEntityDay())
//                        PlanCalendarType.MONTHS -> monthsRepository.insertMonth(entity.toEntityMonth())
//                        PlanCalendarType.YEARS  -> yearsRepository.insertYear(entity.toEntityYear())
//                    }
//                    else {
//                        onStatusChange(PlanCalendarLoadingStatus.DONE)
//                        return@launch
//                    }
//
//                    for (task in newTasks) {
//                        if (task.exist(sourceTasks)) { //task of entity exist
//                            if (task.isEmpty()) when (type) { //new tasks is empty -> delete
//                                PlanCalendarType.DAYS -> daysRepository.deleteDayTask(task.copy(ownerId = entityId).toEntityDay())
//                                PlanCalendarType.MONTHS -> monthsRepository.deleteMonthTask(task.copy(ownerId = entityId).toEntityMonth())
//                                PlanCalendarType.YEARS -> yearsRepository.deleteYearTask(task.copy(ownerId = entityId).toEntityYear())
//                            }
//                            else if(!task.isSame(sourceTasks)) when (type) { //new task isnt empty -> update
//                                PlanCalendarType.DAYS -> daysRepository.updateDayTask(task.copy(ownerId = entityId).toEntityDay())
//                                PlanCalendarType.MONTHS -> monthsRepository.updateMonthTask(task.copy(ownerId = entityId).toEntityMonth())
//                                PlanCalendarType.YEARS -> yearsRepository.updateYearTask(task.copy(ownerId = entityId).toEntityYear())
//                            }
//                        } else if (!task.isEmpty()) when (type) { //task of entity isn exist and not empty -> create
//                            PlanCalendarType.DAYS -> daysRepository.insertDayTask(task.copy(ownerId = entityId).toEntityDay())
//                            PlanCalendarType.MONTHS -> monthsRepository.insertMonthTask(task.copy(ownerId = entityId).toEntityMonth())
//                            PlanCalendarType.YEARS -> yearsRepository.insertYearTask(task.copy(ownerId = entityId).toEntityYear())
//                        }
//                        delay(5)
//                    }
//                    onStatusChange(PlanCalendarLoadingStatus.DONE)
//
//                } catch (e: Exception) {
//                    onStatusChange(PlanCalendarLoadingStatus.DONE)
//                    throw RuntimeException("Fatal DB update entity error", e)
//
//                }
//            }
//        }
//
//    }
//}
//
///*****************************************************************
// * End of class
// ****************************************************************/