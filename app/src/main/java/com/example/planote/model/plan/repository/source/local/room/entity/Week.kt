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
import java.time.LocalTime

/*****************************************************************
 * Classes
 ****************************************************************/
/*****************************************************************
 * Entity class for weekly plan module data in DB
 ****************************************************************/
@Entity(tableName = "planWeek")
data class PlanWeekEntity(
    @PrimaryKey(autoGenerate = true)
    val bdId: Long = 0,
    val title: String? = null,
    val description: String? = null,
    val isToggle: Boolean = false,
)

@Entity(
    tableName = "planWeekDay",
    foreignKeys = [
        ForeignKey(
            entity = PlanWeekEntity::class,
            parentColumns = ["bdId"],
            childColumns = ["ownerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["ownerId"])]
)
data class PlanWeekDayEntity(
    @PrimaryKey(autoGenerate = true)
    val bdId: Long = 0,
    val ownerId: Long,
    val title: String? = null,
    val num: Int, //0...6 days of week number
)

@Entity(
    tableName = "planWeekDayTask",
    foreignKeys = [
        ForeignKey(
            entity = PlanWeekDayEntity::class,
            parentColumns = ["bdId"],
            childColumns = ["ownerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["ownerId"])]
)
data class PlanWeekDayTaskEntity(
    @PrimaryKey(autoGenerate = true)
    val bdId: Long = 0,
    val ownerId: Long,
    val title: String? = null,
    val time: LocalTime,
    val description: String? = null,
    val isDone : Boolean = false
)