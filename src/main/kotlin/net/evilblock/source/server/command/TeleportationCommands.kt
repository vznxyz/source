package net.evilblock.source.server.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.flag.Flag
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.util.bukkit.OfflinePlayerWrapper
import net.evilblock.source.server.listener.TeleportationListeners
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

object TeleportationCommands {

    @Command(
        names = ["teleport", "tp", "tpto", "goto"],
        description = "Teleport yourself to a player",
        permission = "essentials.teleport"
    )
    @JvmStatic
    fun teleport(sender: Player, @Param(name = "player") target: OfflinePlayerWrapper) {
        target.loadAsync { player ->
            if (player == null) {
                sender.sendMessage("${ChatColor.RED}No online or offline player with the name " + target.source + " found.")
                return@loadAsync
            }

            val state = if (player.isOnline) {
                ""
            } else {
                "offline player "
            }

            sender.teleport(player as Entity)
            sender.sendMessage("${ChatColor.GOLD}Teleporting you to $state${ChatColor.WHITE}${player.displayName}${ChatColor.GOLD}.")
        }
    }

    @Command(
        names = ["tphere", "bring", "s"],
        description = "Teleport a player to you",
        permission = "essentials.teleport.other"
    )
    @JvmStatic
    fun tphere(
        sender: Player,
        @Param(name = "player") target: Player,
        @Flag(value = ["s", "silent"], description = "Silently teleport the player (staff members always get messaged)") silent: Boolean
    ) {
        target.teleport(sender as Entity)
        sender.sendMessage("${ChatColor.GOLD}Teleporting " + ChatColor.WHITE + target.displayName + ChatColor.GOLD + " to you.")

        if (!silent || target.hasPermission("stark.staff")) {
            target.sendMessage("${ChatColor.GOLD}Teleporting you to " + ChatColor.WHITE + sender.displayName + ChatColor.GOLD + ".")
        }
    }

    @Command(
        names = ["back"],
        description = "Teleport to your last location",
        permission = "essentials.teleport"
    )
    @JvmStatic
    fun back(sender: Player) {
        if (!TeleportationListeners.lastLocation.containsKey(sender.uniqueId)) {
            sender.sendMessage("${ChatColor.RED}No previous location recorded.")
            return
        }

        sender.teleport(TeleportationListeners.lastLocation[sender.uniqueId] as Location)
        sender.sendMessage("${ChatColor.GOLD}Teleporting you to your last recorded location.")
    }

    @Command(
        names = ["tppos"],
        description = "Teleport to coordinates",
        permission = "essentials.teleport"
    )
    @JvmStatic
    fun teleport(sender: Player, @Param(name = "x") x: Double, @Param(name = "y") y: Double, @Param(name = "z") z: Double, @Param(name = "player", defaultValue = "self") target: Player) {
        var x = x
        var z = z

        if (sender != target && !sender.hasPermission("essentials.teleport.other")) {
            sender.sendMessage("${ChatColor.RED}No permission to teleport other players.")
            return
        }

        if (isBlock(x)) {
            x += if (z >= 0.0) 0.5 else -0.5
        }

        if (isBlock(z)) {
            z += if (x >= 0.0) 0.5 else -0.5
        }

        target.teleport(Location(target.world, x, y, z))

        val location = ChatColor.translateAlternateColorCodes('&', String.format("&e[&f%s&e, &f%s&e, &f%s&e]&6", x, y, z))
        if (sender != target) {
            sender.sendMessage("${ChatColor.GOLD}Teleporting " + ChatColor.WHITE + target.displayName + ChatColor.GOLD + " to " + location + ".")
        }

        target.sendMessage("${ChatColor.GOLD}Teleporting you to " + location + ".")
    }

    private fun isBlock(value: Double): Boolean {
        return value % 1.0 == 0.0
    }

}