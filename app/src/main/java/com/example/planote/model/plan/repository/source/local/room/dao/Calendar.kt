/*****************************************************************
 *  DAO package for room
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.model.plan.repository.source.local.room.dao

/*****************************************************************
 * Imported packages
 ****************************************************************/

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarDayEntity
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarDayTaskEntity
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarMonthEntity
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarMonthTaskEntity
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarYearEntity
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarYearTaskEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/*****************************************************************
 * Interfaces
 ****************************************************************/
/*****************************************************************
 * DAO bases interface
 ****************************************************************/
interface PlanCalendarEntityBaseDao<Entity> {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertEntity(entity: Entity): Long
    @Update suspend fun updateEntity(entity: Entity)
    @Delete suspend fun deleteEntity(entity: Entity)
}

interface PlanCalendarTaskBaseDao<Task> {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertEntityTask(task: Task): Long
    @Update suspend fun updateEntityTask(task: Task)
    @Delete suspend fun deleteEntityTask(task: Task)
}

/*****************************************************************
 * Classes
 ****************************************************************/
/*****************************************************************
 * DAO classes for DB.
 ****************************************************************/
@Dao
abstract class PlanCalendarDaysDao : PlanCalendarEntityBaseDao<PlanCalendarDayEntity>,
    PlanCalendarTaskBaseDao<PlanCalendarDayTaskEntity> {
    @Query("SELECT * FROM planCalendarDay WHERE date < :cutoffDate ORDER BY date ASC") abstract fun getDaysBeforeThan(cutoffDate: LocalDate): List<PlanCalendarDayEntity>
    @Query("SELECT * FROM planCalendarDay WHERE date >= :cutoffDate ORDER BY date ASC") abstract fun getDaysAfterThan(cutoffDate: LocalDate): Flow<List<PlanCalendarDayEntity>>
    @Query("SELECT * FROM planCalendarDayTask WHERE ownerId = :dayId") abstract fun getTasksForDay(dayId: Long): List<PlanCalendarDayTaskEntity>
}

@Dao
abstract class PlanCalendarMonthsDao : PlanCalendarEntityBaseDao<PlanCalendarMonthEntity>,
    PlanCalendarTaskBaseDao<PlanCalendarMonthTaskEntity> {
    @Query("SELECT * FROM planCalendarMonth WHERE date < :cutoffDate ORDER BY date ASC") abstract fun getMonthsBeforeThan(cutoffDate: LocalDate): List<PlanCalendarMonthEntity>
    @Query("SELECT * FROM planCalendarMonth WHERE date >= :cutoffDate ORDER BY date ASC") abstract fun getMonthsAfterThan(cutoffDate: LocalDate): Flow<List<PlanCalendarMonthEntity>>
    @Query("SELECT * FROM planCalendarMonthTask WHERE ownerId = :monthId") abstract fun getTasksForMonth(monthId: Long): List<PlanCalendarMonthTaskEntity>
}

@Dao
abstract class PlanCalendarYearsDao : PlanCalendarEntityBaseDao<PlanCalendarYearEntity>,
    PlanCalendarTaskBaseDao<PlanCalendarYearTaskEntity> {
    @Query("SELECT * FROM planCalendarYear WHERE date < :cutoffDate ORDER BY date ASC") abstract fun getYearsBeforeThan(cutoffDate: LocalDate): List<PlanCalendarYearEntity>
    @Query("SELECT * FROM planCalendarYear WHERE date >= :cutoffDate ORDER BY date ASC") abstract fun getYearsAfterThan(cutoffDate: LocalDate): Flow<List<PlanCalendarYearEntity>>
    @Query("SELECT * FROM planCalendarYearTask WHERE ownerId = :yearId") abstract fun getTasksForYear(yearId: Long): List<PlanCalendarYearTaskEntity>
}
