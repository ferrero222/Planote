/*****************************************************************
 *  Repository source package
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.model.plan.repository

/*****************************************************************
 * Imported packages
 ****************************************************************/
import com.example.planote.model.plan.repository.source.local.room.dao.PlanWeekDao
import com.example.planote.model.plan.repository.source.local.room.entity.PlanWeekDayEntity
import com.example.planote.model.plan.repository.source.local.room.entity.PlanWeekDayTaskEntity
import com.example.planote.model.plan.repository.source.local.room.entity.PlanWeekEntity
import kotlinx.coroutines.flow.Flow

/*****************************************************************
 * Classes
 ****************************************************************/
/*****************************************************************
 * Week repository for DB
 ****************************************************************/
class PlanWeekRepository(private val weekDao: PlanWeekDao) {
    suspend fun insertWeek(week: PlanWeekEntity): Long = weekDao.insertWeek(week)
    suspend fun updateWeek(week: PlanWeekEntity) = weekDao.updateWeek(week)
    suspend fun deleteWeek(week: PlanWeekEntity) = weekDao.deleteWeek(week)

    suspend fun insertWeekDay(day: PlanWeekDayEntity): Long = weekDao.insertWeekDay(day)
    suspend fun updateWeekDay(day: PlanWeekDayEntity) = weekDao.updateWeekDay(day)
    suspend fun deleteWeekDay(day: PlanWeekDayEntity) = weekDao.deleteWeekDay(day)

    suspend fun insertWeekDayTask(task: PlanWeekDayTaskEntity): Long = weekDao.insertWeekDayTask(task)
    suspend fun updateWeekDayTask(task: PlanWeekDayTaskEntity) = weekDao.updateWeekDayTask(task)
    suspend fun deleteWeekDayTask(task: PlanWeekDayTaskEntity) = weekDao.deleteWeekDayTask(task)

    suspend fun getWeeks(): Flow<List<PlanWeekEntity>> = weekDao.getWeeks()
    suspend fun getWeekDays(weekId : Long): Flow<List<PlanWeekDayEntity>> = weekDao.getWeekDays(weekId)
    suspend fun getWeekDayTasks(dayId : Long): Flow<List<PlanWeekDayTaskEntity>> = weekDao.getWeekDayTasks(dayId)
}