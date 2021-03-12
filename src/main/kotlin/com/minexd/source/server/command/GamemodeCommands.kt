package com.minexd.source.server.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object GamemodeCommands {

    @Command(
        names = ["gamemode", "gm"],
        permission = "essentials.gamemode",
        description = "Set a player's gamemode"
    )
    @JvmStatic
    fun gamemode(sender: CommandSender, @Param(name = "mode", defaultValue = "-0*toggle*0-") mode: GameMode, @Param(name = "player", defaultValue = "self") target: Player) {
        run(sender, target, mode)
    }

    @Command(
        names = ["gms", "gm0"],
        permission = "essentials.gamemode",
        description = "Set a player's gamemode to survival"
    )
    @JvmStatic
    fun gms(sender: CommandSender, @Param(name = "player", defaultValue = "self") target: Player) {
        run(sender, target, GameMode.SURVIVAL)
    }

    @Command(
        names = ["gmc", "gm1"],
        permission = "essentials.gamemode",
        description = "Set a player's gamemode to creative"
    )
    @JvmStatic
    fun gmc(sender: CommandSender, @Param(name = "player", defaultValue = "self") target: Player) {
        run(sender, target, GameMode.CREATIVE)
    }

    @Command(
        names = ["gma", "gm2"],
        permission = "essentials.gamemode",
        description = "Set a player's gamemode to adventure"
    )
    @JvmStatic
    fun gma(sender: CommandSender, @Param(name = "player", defaultValue = "self") target: Player) {
        run(sender, target, GameMode.ADVENTURE)
    }

    private fun run(sender: CommandSender, target: Player, mode: GameMode) {
        if (sender != target && !sender.hasPermission("essentials.gamemode.other")) {
            sender.sendMessage("${ChatColor.RED}No permission to set other player's gamemode.")
            return
        }

        target.gameMode = mode

        if (sender != target) {
            sender.sendMessage("${target.displayName}${ChatColor.GOLD} is now in ${ChatColor.WHITE}$mode${ChatColor.GOLD} mode.")
        }

        target.sendMessage("${ChatColor.GOLD}You are now in ${ChatColor.WHITE}$mode${ChatColor.GOLD} mode.")
    }

}