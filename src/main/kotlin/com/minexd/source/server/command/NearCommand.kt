package com.minexd.source.server.command

import com.minexd.source.Source
import com.minexd.source.util.Validation
import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.util.math.Numbers
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object NearCommand {

    @Command(
        names = ["near", "nearme", "nearby"],
        description = "Shows players that are nearby",
        permission = "essentials.near",
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "radius", defaultValue = "-69") inputRadius: Int) {
        val radius = if (inputRadius == -69) {
            Source.instance.getNearRadius()
        } else {
            inputRadius
        }

        if (radius < 0) {
            player.sendMessage("${ChatColor.RED}You must input a positive radius!")
            return
        }

        if (radius > Source.instance.getNearRadius() && !player.hasPermission("essentials.near.unlimited")) {
            player.sendMessage("${ChatColor.RED}You don't have permission to show nearby players that far away!")
            return
        }

        val nearbyPlayers = arrayListOf<Pair<Player, Int>>()

        for (onlinePlayer in Bukkit.getOnlinePlayers()) {
            if (Validation.isVanished(player) && !player.hasPermission("essentials.near.vanished")) {
                continue
            }

            if (player.world == onlinePlayer.world) {
                val dist = player.location.distance(onlinePlayer.location)
                if (dist <= radius) {
                    nearbyPlayers.add(Pair(onlinePlayer, dist.toInt()))
                }
            }
        }

        player.sendMessage(buildString {
            append("${ChatColor.GOLD}Nearby Players: ${ChatColor.GRAY}")

            if (nearbyPlayers.isEmpty()) {
                append("None")
            } else {
                append(nearbyPlayers.joinToString { "${it.first.name} (${Numbers.format(it.second)})" })
            }
        })
    }

}