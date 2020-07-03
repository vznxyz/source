package net.evilblock.source.server.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object SpeedCommand {

    @Command(
        names = ["speed"],
        description = "Change your walk or fly speed",
        permission = "essentials.speed"
    )
    @JvmStatic
    fun speed(sender: Player, @Param(name = "speed") speed: Int) {
        if (speed < 0 || speed > 10) {
            sender.sendMessage(ChatColor.RED.toString() + "Speed must be between 0 and 10.")
            return
        }

        val fly = sender.isFlying
        if (fly) {
            sender.flySpeed = getSpeed(speed, true)
        } else {
            sender.walkSpeed = getSpeed(speed, false)
        }

        sender.sendMessage(ChatColor.GOLD.toString() + (if (fly) "Fly" else "Walk") + " set to " + ChatColor.WHITE + speed + ChatColor.GOLD + ".")
    }

    private fun getSpeed(speed: Int, isFly: Boolean): Float {
        val defaultSpeed = if (isFly) 0.1F else 0.2F
        val maxSpeed = 1.0F

        if (speed < 1.0F) {
            return defaultSpeed * speed
        }

        val ratio = (speed - 1.0F) / 9.0F * (maxSpeed - defaultSpeed)
        return ratio + defaultSpeed
    }

}