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
import androidx.room.Query
import androidx.room.Update
import com.example.planote.model.plan.repository.source.local.room.entity.PlanWeekDayEntity
import com.example.planote.model.plan.repository.source.local.room.entity.PlanWeekDayTaskEntity
import com.example.planote.model.plan.repository.source.local.room.entity.PlanWeekEntity
import kotlinx.coroutines.flow.Flow

/*****************************************************************
 * Interfaces
 ****************************************************************/
/*****************************************************************
 * DAO interface for DB
 ****************************************************************/
@Dao
interface PlanWeekDao {
    @Insert suspend fun insertWeek(week: PlanWeekEntity): Long
    @Update suspend fun updateWeek(week: PlanWeekEntity)
    @Delete suspend fun deleteWeek(week: PlanWeekEntity)

    @Insert suspend fun insertWeekDay(day: PlanWeekDayEntity): Long
    @Update suspend fun updateWeekDay(day: PlanWeekDayEntity)
    @Delete suspend fun deleteWeekDay(day: PlanWeekDayEntity)

    @Insert suspend fun insertWeekDayTask(task: PlanWeekDayTaskEntity): Long
    @Update suspend fun updateWeekDayTask(task: PlanWeekDayTaskEntity)
    @Delete suspend fun deleteWeekDayTask(task: PlanWeekDayTaskEntity)

    @Query("SELECT * FROM planWeek") fun getWeeks(): Flow<List<PlanWeekEntity>>
    @Query("SELECT * FROM planWeekDay") fun getWeekDays(): Flow<List<PlanWeekDayEntity>>
    @Query("SELECT * FROM planWeekDayTask") fun getWeekDayTasks(): Flow<List<PlanWeekDayTaskEntity>>
}