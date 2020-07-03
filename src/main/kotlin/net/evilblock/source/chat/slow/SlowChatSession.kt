package net.evilblock.source.chat.slow

import java.util.*
import kotlin.collections.HashMap

class SlowChatSession(val issuer: String, val duration: Long) {
    val players = HashMap<UUID, Long>()
}