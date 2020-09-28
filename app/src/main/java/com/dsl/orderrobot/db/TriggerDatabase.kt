package com.dsl.orderrobot.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * @author dsl-abben
 * on 2020/09/25.
 */
@Database(entities = [TriggerBean::class], version = 1, exportSchema = false)
abstract class TriggerDatabase : RoomDatabase() {
    abstract fun commonTriggerDao(): TriggerDao

    companion object {

        @Volatile
        private var INSTANCE: TriggerDatabase? = null

        fun getInstance(context: Context): TriggerDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                TriggerDatabase::class.java, "trigger.db"
            ).build()
    }
}