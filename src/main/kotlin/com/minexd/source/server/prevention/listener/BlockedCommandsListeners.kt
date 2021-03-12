package com.minexd.source.server.prevention.listener

import com.minexd.source.server.prevention.BlockedCommandsHandler
import com.minexd.source.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

object BlockedCommandsListeners : Listener {

    @EventHandler
    fun onCommand(event: PlayerCommandPreprocessEvent) {
        if (event.message.startsWith("/minecraft:")) {
            event.isCancelled = true
            return
        }

        val command = if (event.message.contains(" ")) {
            event.message.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        } else {
            event.message
        }

        val filtered = BlockedCommandsHandler.blockedCommands.any { blocked -> blocked.equals(
            strip(command), ignoreCase = true) }
        if (filtered) {
            if (!event.player.hasPermission(Permissions.DISALLOWED_COMMANDS_BYPASS)) {
                event.isCancelled = true
                event.player.sendMessage("${ChatColor.RED}That command has been disabled by an administrator!")
            }
        }
    }

    private fun strip(command: String): String {
        return command
            .toLowerCase()
            .replaceFirst("/", "")
            .replace("minecraft:", "")
            .replace("bukkit:", "")
            .replace("worldedit:", "")
    }

}