package com.neko.v2ray.dto

enum class Language(val code: String) {
    AUTO("auto"),
    ENGLISH("en");

    companion object {
        fun fromCode(code: String): Language {
            return entries.find { it.code == code } ?: AUTO
        }
    }
}
