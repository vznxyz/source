package com.minexd.source.util

import org.bukkit.entity.Player

object Validation {

    @JvmStatic
    fun isVanished(player: Player): Boolean {
        return player.hasMetadata("vanished")
    }

}