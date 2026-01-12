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
import com.example.planote.model.plan.repository.source.local.room.entity.PlanWeek
import com.example.planote.model.plan.repository.source.local.room.entity.PlanWeekDay
import com.example.planote.model.plan.repository.source.local.room.entity.PlanWeekDayTask
import kotlinx.coroutines.flow.Flow

/*****************************************************************
 * Classes
 ****************************************************************/
/*****************************************************************
 * Week repository for DB
 ****************************************************************/
class PlanWeekRepository(private val weekDao: PlanWeekDao) {
    suspend fun insertWeek(week: PlanWeek): Long = weekDao.insertWeek(week)
    suspend fun updateYear(week: PlanWeek) = weekDao.updateWeek(week)
    suspend fun deleteYear(week: PlanWeek) = weekDao.deleteWeek(week)
    fun getWeeks(): Flow<List<PlanWeek>> = weekDao.getWeeks()
    fun getWeekDays(weekId : Long): Flow<List<PlanWeekDay>> = weekDao.getWeekDays(weekId)
    fun getDayTasks(dayId : Long): Flow<List<PlanWeekDayTask>> = weekDao.getDayTasks(dayId)
}