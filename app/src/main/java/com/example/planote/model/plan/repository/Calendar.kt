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
    fun getDaysBeforeThan(cutoffDate: LocalDate): List<PlanCalendarDayEntity> = daysDao.getDaysBeforeThan(cutoffDate)
    fun getDaysAfterThan(cutoffDate: LocalDate): Flow<List<PlanCalendarDayEntity>> = daysDao.getDaysAfterThan(cutoffDate)
    fun getTasksForDay(dayId: Long): List<PlanCalendarDayTaskEntity> = daysDao.getTasksForDay(dayId)
}

class PlanCalendarMonthsRepository(private val monthsDao: PlanCalendarMonthsDao) {
    suspend fun insertMonth(month: PlanCalendarMonthEntity): Long = monthsDao.insertEntity(month)
    suspend fun updateMonth(month: PlanCalendarMonthEntity) = monthsDao.updateEntity(month)
    suspend fun deleteMonth(month: PlanCalendarMonthEntity) = monthsDao.deleteEntity(month)
    suspend fun insertMonthTask(task: PlanCalendarMonthTaskEntity): Long = monthsDao.insertEntityTask(task)
    suspend fun updateMonthTask(task: PlanCalendarMonthTaskEntity) = monthsDao.updateEntityTask(task)
    suspend fun deleteMonthTask(task: PlanCalendarMonthTaskEntity) = monthsDao.deleteEntityTask(task)
    fun getMonthsBeforeThan(cutoffDate: LocalDate): List<PlanCalendarMonthEntity> = monthsDao.getMonthsBeforeThan(cutoffDate)
    fun getMonthsAfterThan(cutoffDate: LocalDate): Flow<List<PlanCalendarMonthEntity>> = monthsDao.getMonthsAfterThan(cutoffDate)
    fun getTasksForMonth(monthId: Long): List<PlanCalendarMonthTaskEntity> = monthsDao.getTasksForMonth(monthId)
}

class PlanCalendarYearsRepository(private val yearsDao: PlanCalendarYearsDao) {
    suspend fun insertYear(year: PlanCalendarYearEntity): Long = yearsDao.insertEntity(year)
    suspend fun updateYear(year: PlanCalendarYearEntity) = yearsDao.updateEntity(year)
    suspend fun deleteYear(year: PlanCalendarYearEntity) = yearsDao.deleteEntity(year)
    suspend fun insertYearTask(task: PlanCalendarYearTaskEntity): Long = yearsDao.insertEntityTask(task)
    suspend fun updateYearTask(task: PlanCalendarYearTaskEntity) = yearsDao.updateEntityTask(task)
    suspend fun deleteYearTask(task: PlanCalendarYearTaskEntity) = yearsDao.deleteEntityTask(task)
    fun getYearsBeforeThan(cutoffDate: LocalDate): List<PlanCalendarYearEntity> = yearsDao.getYearsBeforeThan(cutoffDate)
    fun getYearsAfterThan(cutoffDate: LocalDate): Flow<List<PlanCalendarYearEntity>> = yearsDao.getYearsAfterThan(cutoffDate)
    fun getTasksForYear(yearId: Long): List<PlanCalendarYearTaskEntity> = yearsDao.getTasksForYear(yearId)
}
