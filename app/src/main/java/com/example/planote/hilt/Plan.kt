/*****************************************************************
 *  Package for HILT implementation
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.hilt

/*****************************************************************
 * Imported packages
 ****************************************************************/
import android.content.Context
import androidx.room.Room
import com.example.planote.model.plan.repository.PlanCalendarDaysRepository
import com.example.planote.model.plan.repository.PlanCalendarMonthsRepository
import com.example.planote.model.plan.repository.PlanCalendarYearsRepository
import com.example.planote.model.plan.repository.source.local.room.PlanDataBase
import com.example.planote.model.plan.repository.source.local.room.dao.PlanCalendarDaysDao
import com.example.planote.model.plan.repository.source.local.room.dao.PlanCalendarMonthsDao
import com.example.planote.model.plan.repository.source.local.room.dao.PlanCalendarYearsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/*****************************************************************
 * Objects
 ****************************************************************/
/*****************************************************************
 * Hilt object for Plan Module
 ****************************************************************/
@Module
@InstallIn(SingletonComponent::class)
object PlanModuleDI {
    @Provides @Singleton fun providePlanCalendarDaysRepository(daysDao: PlanCalendarDaysDao): PlanCalendarDaysRepository {
        return PlanCalendarDaysRepository(daysDao)
    }

    @Provides @Singleton fun providePlanCalendarMonthsRepository(monthsDao: PlanCalendarMonthsDao): PlanCalendarMonthsRepository {
        return PlanCalendarMonthsRepository(monthsDao)
    }

    @Provides @Singleton fun providePlanCalendarYearsRepository(yearsDao: PlanCalendarYearsDao): PlanCalendarYearsRepository {
        return PlanCalendarYearsRepository(yearsDao)
    }
}

/*****************************************************************
 * Hilt object for Data Base plan
 ****************************************************************/
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PlanDataBase {
        return Room.databaseBuilder(
            context,
            PlanDataBase::class.java,
            "planDB"
        ).build()
    }

    @Provides
    @Singleton
    fun providePlanCalendarDaysDao(database: PlanDataBase): PlanCalendarDaysDao {
        return database.daoPlanCalendarDays()
    }

    @Provides
    @Singleton
    fun providePlanCalendarMonthsDao(database: PlanDataBase): PlanCalendarMonthsDao {
        return database.daoPlanCalendarMonths()
    }

    @Provides
    @Singleton
    fun providePlanCalendarYearsDao(database: PlanDataBase): PlanCalendarYearsDao {
        return database.daoPlanCalendarYears()
    }
}