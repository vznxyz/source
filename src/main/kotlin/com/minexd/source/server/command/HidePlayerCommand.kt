/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package com.minexd.source.server.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import org.bukkit.entity.Player

object HidePlayerCommand {

    @Command(
        names = ["hideplayer"],
        description = "Hides a player",
        permission = "op"
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "player") target: Player) {
        player.hidePlayer(target)
        player.sendMessage("Now hiding ${target.name}")
    }

}