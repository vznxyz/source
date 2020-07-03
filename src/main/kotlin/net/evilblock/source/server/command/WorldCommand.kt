package net.evilblock.source.server.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import org.bukkit.World
import org.bukkit.entity.Player

object WorldCommand {

    @Command(
        names = ["world"],
        description = "Teleport to a world's spawn-point",
        permission = "essentials.world"
    )
    @JvmStatic
    fun world(sender: Player, @Param(name = "world") world: World) {
        sender.teleport(world.spawnLocation.clone().add(0.5, 0.0, 0.5))
    }

}