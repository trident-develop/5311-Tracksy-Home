package com.bailing.lark.roll.data

import android.content.Context
import com.bailing.lark.roll.data.db.ScoreStorage
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf


data class LoadingFlowContext(
    val context: Context,
    val storage: ScoreStorage,
    val data: PersistentMap<String, String> = persistentMapOf()
) {
    fun put(key: String, value: String): LoadingFlowContext {
//        log("FlowContext: put $key = $value")
        return copy(data = data.put(key, value))
    }

    fun get(key: String): String? = data[key]
}