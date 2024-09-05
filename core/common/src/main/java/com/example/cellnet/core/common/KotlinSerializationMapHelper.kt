package com.example.cellnet.core.common

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.encodeToJsonElement

object KotlinSerializationMapHelper {
    inline fun <reified T> toMap(obj: T): Map<String, Any?> {
        return jsonObjectToMap(Json.encodeToJsonElement(obj).jsonObject)
    }

    fun jsonObjectToMap(element: JsonObject): Map<String, Any?> {
        return element.entries.associate {
            it.key to extractValue(it.value)
        }
    }

    private fun extractValue(element: JsonElement): Any? {
        return when (element) {
            is JsonNull -> null
            is JsonPrimitive -> element.content
            is JsonArray -> element.map { extractValue(it) }
            is JsonObject -> jsonObjectToMap(element)
        }
    }
}