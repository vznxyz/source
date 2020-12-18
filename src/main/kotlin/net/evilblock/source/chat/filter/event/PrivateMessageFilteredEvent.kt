package net.evilblock.source.chat.filter.event

import net.evilblock.cubed.plugin.PluginEvent
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable

class PrivateMessageFilteredEvent(val sender: Player, val recipient: Player, var message: String) : PluginEvent(), Cancellable {

    private var cancelled: Boolean = false

    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }

}