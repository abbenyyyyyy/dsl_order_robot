package com.dsl.orderrobot.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author dsl-abben
 * on 2020/09/23.
 */
@Entity
data class TriggerBean(
    val trigger: String,
    /**
     * 并集的触发关键词
     */
    val andTrigger: String,
    val autoSend: String,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L
)