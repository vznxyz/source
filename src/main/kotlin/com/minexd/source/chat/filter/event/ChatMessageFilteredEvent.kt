package com.minexd.source.chat.filter.event

import net.evilblock.cubed.plugin.PluginEvent
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable

class ChatMessageFilteredEvent(val player: Player, var message: String) : PluginEvent(), Cancellable {

    private var cancelled: Boolean = false

    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }

}