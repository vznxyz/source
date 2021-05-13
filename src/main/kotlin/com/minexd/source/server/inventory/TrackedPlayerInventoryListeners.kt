package com.minexd.source.server.inventory

import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerJoinEvent

object TrackedPlayerInventoryListeners : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        if (TrackedPlayerInventory.storage.containsKey(player.uniqueId)) {
            TrackedPlayerInventory.storage[player.uniqueId]!!.onJoin(player)
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player
        if (TrackedPlayerInventory.storage.containsKey(player.uniqueId)) {
            TrackedPlayerInventory.storage[player.uniqueId]!!.onQuit()
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as Player
        if (TrackedPlayerInventory.open.contains(player.uniqueId) && !player.hasPermission("essentials.invsee.edit")) {
            event.isCancelled = true
            player.sendMessage("${ChatColor.RED}You do not have permission to edit this inventory.")
        }
    }

}