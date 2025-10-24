package com.solar.tabor

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import androidx.core.content.edit

/**
 * Date：2025/10/11
 * Describe:
 */
class StringCacheImpl(val key: String = "") : ReadWriteProperty<Any, String> {
    private val keyPer = "Raven_"

    private fun getKey(name: String): String {
        return key.ifBlank { "${keyPer}_${name}" }
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): String {
        return CacheRaven.mmkv.getString(getKey(property.name), "") ?: ""
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String) {
        CacheRaven.mmkv.edit { putString(getKey(property.name), value) }
    }
}