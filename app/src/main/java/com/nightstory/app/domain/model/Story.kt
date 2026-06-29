package com.nightstory.app.domain.model

data class Story(
    val id: Long = 0,
    val title: String,
    val prompt: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)
