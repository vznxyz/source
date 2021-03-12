package com.minexd.source.server.announcement

import java.util.*

data class Announcement(
    val id: UUID = UUID.randomUUID(),
    var lines: MutableList<String>,
    var order: Int = 0
)