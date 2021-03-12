package com.minexd.source.chat.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import com.minexd.source.chat.ChatSettings
import com.minexd.source.chat.slow.SlowChatSession
import com.minexd.source.util.Permissions
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object SlowChatCommand {

    @Command(
        names = ["slowchat", "slow-chat", "source slow-chat"],
        description = "Slow the chat by putting users on cooldown",
        permission = Permissions.SLOW_CHAT,
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param("duration", defaultValue = "0") duration: Int) {
        if (duration > 30) {
            sender.sendMessage("${ChatColor.RED}You can't slow the chat for this long.")
            return
        }

        if (duration == 0) {
            val session = ChatSettings.slowed
            if (session == null) {
                sender.sendMessage("${ChatColor.RED}The chat isn't being slowed.")
                return
            } else {
                ChatSettings.slowed = null
                Bukkit.broadcastMessage("${ChatColor.LIGHT_PURPLE}Public chat has been unslowed by ${sender.name}.")
            }
            return
        }

        val session = SlowChatSession(sender.name, duration * 1000L)
        ChatSettings.slowed = session

        Bukkit.broadcastMessage("${ChatColor.LIGHT_PURPLE}Public chat has been slowed by ${sender.name}.")
    }

}