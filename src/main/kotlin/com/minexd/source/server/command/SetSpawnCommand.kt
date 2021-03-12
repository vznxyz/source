package com.minexd.source.server.command

import net.evilblock.cubed.command.Command
import com.minexd.source.Source
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import java.util.*
import kotlin.math.roundToInt

object SetSpawnCommand {

    private val RADIAL: Array<BlockFace> = arrayOf(BlockFace.WEST, BlockFace.NORTH_WEST, BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST)
    private val NOTCHES: EnumMap<BlockFace, Int> = EnumMap(BlockFace::class.java)

    @Command(
        names = ["setspawn"],
        description = "Sets the server spawn point",
        permission = "essentials.setspawn"
    )
    @JvmStatic
    fun execute(sender: Player) {
        val location = sender.location
        val face = yawToFace(location.yaw)

        Source.instance.serverConfig.spawnLocation = Location(
            location.world,
            location.blockX.toDouble(),
            location.blockY.toDouble(),
            location.blockZ.toDouble(),
            faceToYaw(face).toFloat(),
            0.0F
        )

        Source.instance.saveServerConfig()

        sender.sendMessage("${ChatColor.GOLD}Set the server spawn!")
    }

    private fun yawToFace(yaw: Float): BlockFace {
        return RADIAL[(yaw / 45.0f).roundToInt() and 0x7]
    }

    private fun faceToYaw(face: BlockFace): Int {
        return wrapAngle(
            45 * faceToNotch(
                face
            )
        )
    }

    private fun faceToNotch(face: BlockFace): Int {
        val notch = NOTCHES[face]
        return notch ?: 0
    }

    private fun wrapAngle(angle: Int): Int {
        var wrappedAngle: Int = angle
        while (wrappedAngle <= -180) {
            wrappedAngle += 360
        }
        while (wrappedAngle > 180) {
            wrappedAngle -= 360
        }
        return wrappedAngle
    }

    init {
        for (i in RADIAL.indices) {
            NOTCHES[RADIAL[i]] = i
        }
    }

}
