package com.minexd.source.server.command

import net.evilblock.cubed.command.Command
import com.minexd.source.Source
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object SpawnCommand {

    @Command(
        names = ["spawn"],
        description = "Teleports to the server spawn point",
        permission = "essentials.spawn"
    )
    @JvmStatic
    fun execute(sender: Player) {
        sender.teleport(Source.instance.getSpawnLocation())
        sender.sendMessage("${ChatColor.YELLOW}You have been teleported to spawn!")
    }

}
