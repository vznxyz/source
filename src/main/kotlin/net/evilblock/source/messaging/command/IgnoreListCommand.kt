package net.evilblock.source.messaging.command

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.command.Command
import net.evilblock.source.messaging.MessagingManager
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object IgnoreListCommand {

    @Command(
        names = ["ignore list"],
        description = "See a list of people you're currently ignoring",
        async = true
    )
    @JvmStatic
    fun execute(player: Player) {
        val ignoreList = MessagingManager.getIgnoreList(player.uniqueId)

        if (ignoreList.isEmpty()) {
            player.sendMessage("${ChatColor.RED}You aren't ignoring anyone.")
            return
        }

        val names = arrayListOf<String>()
        for (uuid in ignoreList) {
            names.add(Cubed.instance.uuidCache.name(uuid))
        }

        player.sendMessage("${ChatColor.YELLOW}You are currently ignoring ${ChatColor.RED}${names.size} ${ChatColor.YELLOW}player${if (names.size == 1) "" else "s"}:")
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', names.joinToString("&e, ", "&c")))
    }

}