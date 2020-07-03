package net.evilblock.source.messaging.event

import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import java.util.*

class ToggleSoundsEvent(val uuid: UUID, val playSounds: Boolean) : Event() {

    companion object {
        @JvmStatic val handlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }

}