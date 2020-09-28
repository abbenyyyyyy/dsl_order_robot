package com.dsl.orderrobot.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * @author dsl-abben
 * on 2020/09/25.
 */
@Dao
interface TriggerDao {

    @Insert
    fun insertTrigger(triggerBeans: List<TriggerBean>): Single<List<Long>>

    @Insert
    fun insertSingleTrigger(triggerBean: TriggerBean): Single<Long>

    @Query("SELECT * FROM TriggerBean")
    fun queryTriggers(): Flowable<List<TriggerBean>>

    @Query("SELECT * FROM TriggerBean")
    fun queryTriggersOnWorkThread(): List<TriggerBean>

    @Delete
    fun deleteTrigger(triggerBean: TriggerBean):Single<Int>

}