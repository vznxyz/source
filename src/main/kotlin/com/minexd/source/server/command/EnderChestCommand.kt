/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package com.minexd.source.server.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.util.bukkit.player.OfflinePlayerWrapper
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object EnderChestCommand {

    @Command(
        names = ["ec", "e-chest", "echest"],
        description = "Opens your ender chest",
        permission = "essentials.enderchest"
    )
    @JvmStatic
    fun execute(player: Player) {
        player.sendMessage("${ChatColor.GREEN}Opening your ender chest...")
        player.openInventory(player.enderChest)
    }

    @Command(
        names = ["ec other", "e-chest other", "echest other"],
        description = "Opens your ender chest",
        permission = "essentials.enderchest.other",
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "player") offlinePlayer: OfflinePlayerWrapper) {
        val targetPlayer = offlinePlayer.loadSync()
        if (targetPlayer == null) {
            player.sendMessage("${ChatColor.RED}Failed to load offline player data for ${ChatColor.WHITE}${offlinePlayer.source}${ChatColor.RED}!")
        } else {
            player.sendMessage("${ChatColor.GREEN}Opening ${ChatColor.WHITE}${targetPlayer.name}${ChatColor.GREEN}'s ender chest...")
            player.openInventory(targetPlayer.enderChest)
        }
    }

}