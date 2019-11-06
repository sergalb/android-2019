package ru.ifmo.rain.balahin.imageviewer.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Image(
    val id: String,
    val width: Int,
    val height: Int,
    val description: String? = null,
    val alt_description: String? = null,
    val urls: Map<String, String> = emptyMap(),
    val links: Map<String, String> = emptyMap(),
    val user: User? = null,
    var used: Boolean = false
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class User(
    val id: String,
    val userName: String? = null,
    val name: String? = null
)