/*****************************************************************
 *  Repository source package
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.model.plan.repository

/*****************************************************************
 * Imported packages
 ****************************************************************/
import com.example.planote.model.plan.repository.source.local.room.dao.PlanCalendarDaysDao
import com.example.planote.model.plan.repository.source.local.room.dao.PlanCalendarMonthsDao
import com.example.planote.model.plan.repository.source.local.room.dao.PlanCalendarYearsDao
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarDayEntity
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarDayTaskEntity
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarMonthEntity
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarMonthTaskEntity
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarYearEntity
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarYearTaskEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/*****************************************************************
 * Classes
 ****************************************************************/
/*****************************************************************
 * DB repository's
 ****************************************************************/
class PlanCalendarDaysRepository(private val daysDao: PlanCalendarDaysDao) {
    suspend fun insertDay(day: PlanCalendarDayEntity): Long = daysDao.insertEntity(day)
    suspend fun updateDay(day: PlanCalendarDayEntity) = daysDao.updateEntity(day)
    suspend fun deleteDay(day: PlanCalendarDayEntity) = daysDao.deleteEntity(day)
    suspend fun insertDayTask(task: PlanCalendarDayTaskEntity): Long = daysDao.insertEntityTask(task)
    suspend fun updateDayTask(task: PlanCalendarDayTaskEntity) = daysDao.updateEntityTask(task)
    suspend fun deleteDayTask(task: PlanCalendarDayTaskEntity) = daysDao.deleteEntityTask(task)
    suspend fun getDaysBeforeThan(cutoffDate: LocalDate): Flow<List<PlanCalendarDayEntity>> = daysDao.getDaysBeforeThan(cutoffDate)
    suspend fun getDaysAfterThan(cutoffDate: LocalDate): Flow<List<PlanCalendarDayEntity>> = daysDao.getDaysAfterThan(cutoffDate)
    suspend fun getTasksForDay(dayId: Long): Flow<List<PlanCalendarDayTaskEntity>> = daysDao.getTasksForDay(dayId)
}

class PlanCalendarMonthsRepository(private val monthsDao: PlanCalendarMonthsDao) {
    suspend fun insertMonth(month: PlanCalendarMonthEntity): Long = monthsDao.insertEntity(month)
    suspend fun updateMonth(month: PlanCalendarMonthEntity) = monthsDao.updateEntity(month)
    suspend fun deleteMonth(month: PlanCalendarMonthEntity) = monthsDao.deleteEntity(month)
    suspend fun insertMonthTask(task: PlanCalendarMonthTaskEntity): Long = monthsDao.insertEntityTask(task)
    suspend fun updateMonthTask(task: PlanCalendarMonthTaskEntity) = monthsDao.updateEntityTask(task)
    suspend fun deleteMonthTask(task: PlanCalendarMonthTaskEntity) = monthsDao.deleteEntityTask(task)
    suspend fun getMonthsBeforeThan(cutoffDate: LocalDate): Flow<List<PlanCalendarMonthEntity>> = monthsDao.getMonthsBeforeThan(cutoffDate)
    suspend fun getMonthsAfterThan(cutoffDate: LocalDate): Flow<List<PlanCalendarMonthEntity>> = monthsDao.getMonthsAfterThan(cutoffDate)
    suspend fun getTasksForMonth(monthId: Long): Flow<List<PlanCalendarMonthTaskEntity>> = monthsDao.getTasksForMonth(monthId)
}

class PlanCalendarYearsRepository(private val yearsDao: PlanCalendarYearsDao) {
    suspend fun insertYear(year: PlanCalendarYearEntity): Long = yearsDao.insertEntity(year)
    suspend fun updateYear(year: PlanCalendarYearEntity) = yearsDao.updateEntity(year)
    suspend fun deleteYear(year: PlanCalendarYearEntity) = yearsDao.deleteEntity(year)
    suspend fun insertYearTask(task: PlanCalendarYearTaskEntity): Long = yearsDao.insertEntityTask(task)
    suspend fun updateYearTask(task: PlanCalendarYearTaskEntity) = yearsDao.updateEntityTask(task)
    suspend fun deleteYearTask(task: PlanCalendarYearTaskEntity) = yearsDao.deleteEntityTask(task)
    suspend fun getYearsBeforeThan(cutoffDate: LocalDate): Flow<List<PlanCalendarYearEntity>> = yearsDao.getYearsBeforeThan(cutoffDate)
    suspend fun getYearsAfterThan(cutoffDate: LocalDate): Flow<List<PlanCalendarYearEntity>> = yearsDao.getYearsAfterThan(cutoffDate)
    suspend fun getTasksForYear(yearId: Long): Flow<List<PlanCalendarYearTaskEntity>> = yearsDao.getTasksForYear(yearId)
}
