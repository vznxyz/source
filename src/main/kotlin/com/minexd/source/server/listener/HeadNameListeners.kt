package com.minexd.source.server.listener

import org.bukkit.ChatColor
import org.bukkit.SkullType
import org.bukkit.block.Skull
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

object HeadNameListeners : Listener {

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK) {
            return
        }

        val player = event.player
        val block = event.clickedBlock

        if (block.state is Skull) {
            val skull = block.state as Skull
            if (skull.skullType !== SkullType.PLAYER) {
                return
            }

            val owner = if (skull.owner == null) "Steve" else skull.owner
            player.sendMessage("${ChatColor.YELLOW}This is the head of: " + owner)
        }
    }

}