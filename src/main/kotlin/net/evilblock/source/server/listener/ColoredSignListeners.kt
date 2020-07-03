package net.evilblock.source.server.listener

import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.SignChangeEvent

object ColoredSignListeners : Listener {

    @EventHandler
    fun onSignChange(event: SignChangeEvent) {
        val player = event.player
        if (!player.hasPermission("essentials.coloredsigns")) {
            return
        }

        for (i in event.lines.indices) {
            event.setLine(i, ChatColor.translateAlternateColorCodes('&', event.lines[i]).trim())
        }
    }

}