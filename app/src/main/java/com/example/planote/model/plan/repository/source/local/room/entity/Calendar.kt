/*****************************************************************
 *  Entity package for room
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.model.plan.repository.source.local.room.entity

/*****************************************************************
 * Imported packages
 ****************************************************************/
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

/*****************************************************************
 * Classes
 ****************************************************************/
/*****************************************************************
 * Entity class for plan calendar day module data in DB
 ****************************************************************/
@Entity(tableName = "planCalendarDay")
data class PlanCalendarDayEntity(
    @PrimaryKey(autoGenerate = true)
    val bdId: Long = 0,
    val title: String? = null,
    val date: LocalDate, //
)

@Entity(
    tableName = "planCalendarDayTask",
    foreignKeys = [
        ForeignKey(
            entity = PlanCalendarDayEntity::class,
            parentColumns = ["bdId"],
            childColumns = ["ownerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["ownerId"])]
)
data class PlanCalendarDayTaskEntity(
    @PrimaryKey(autoGenerate = true)
    val bdId: Long = 0,
    val ownerId: Long,
    val title: String,
    val description: String? = null,
    val isDone : Boolean = false
)

/*****************************************************************
 * Entity class for plan calendar month module data in DB
 ****************************************************************/
@Entity(tableName = "planCalendarMonth")
data class PlanCalendarMonthEntity(
    @PrimaryKey(autoGenerate = true)
    val bdId: Long = 0,
    val title: String? = null,
    val date: LocalDate,
)

@Entity(
    tableName = "planCalendarMonthTask",
    foreignKeys = [
        ForeignKey(
            entity = PlanCalendarMonthEntity::class,
            parentColumns = ["bdId"],
            childColumns = ["ownerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["ownerId"])]
)
data class PlanCalendarMonthTaskEntity(
    @PrimaryKey(autoGenerate = true)
    val bdId: Long = 0,
    val ownerId: Long,
    val title: String,
    val description: String? = null,
    val isDone : Boolean = false
)

/*****************************************************************
 * Entity class for plan calendar year module data in DB
 ****************************************************************/
@Entity(tableName = "planCalendarYear")
data class PlanCalendarYearEntity(
    @PrimaryKey(autoGenerate = true)
    val bdId: Long = 0,
    val title: String? = null,
    val date: LocalDate,
)

@Entity(
    tableName = "planCalendarYearTask",
    foreignKeys = [
        ForeignKey(
            entity = PlanCalendarYearEntity::class,
            parentColumns = ["bdId"],
            childColumns = ["ownerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["ownerId"])]
)
data class PlanCalendarYearTaskEntity(
    @PrimaryKey(autoGenerate = true)
    val bdId: Long = 0,
    val ownerId: Long,
    val title: String,
    val description: String? = null,
    val isDone : Boolean = false
)