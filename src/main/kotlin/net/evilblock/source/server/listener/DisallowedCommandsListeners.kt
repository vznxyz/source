package net.evilblock.source.server.listener

import net.evilblock.source.server.ServerSettings
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

object DisallowedCommandsListeners : Listener {

    @EventHandler
    fun onCommand(event: PlayerCommandPreprocessEvent) {
        val command = if (event.message.contains(" ")) {
            event.message.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        } else {
            event.message
        }

        ServerSettings.disallowedCommands.filter { blocked -> blocked.equals(this.strip(command), ignoreCase = true) }.forEach {
            event.isCancelled = true
            event.player.sendMessage("${ChatColor.RED}This action can only be performed by the console.")
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