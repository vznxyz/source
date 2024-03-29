package com.minexd.source.chat.filter.listener

import com.google.common.collect.Maps
import mkremins.fanciful.FancyMessage
import com.minexd.source.chat.ChatSettings
import com.minexd.source.chat.filter.ChatFilter
import com.minexd.source.chat.filter.ChatFilterHandler
import com.minexd.source.chat.filter.event.ChatMessageFilteredEvent
import com.minexd.source.util.Permissions
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

object ChatFilterListeners : Listener {

    private val badMessages: MutableMap<UUID, Pair<String, ChatFilter>> = Maps.newConcurrentMap()

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerChatLowest(event: AsyncPlayerChatEvent) {
        if (!event.player.hasPermission(Permissions.FILTER_BYPASS)) {
            val filter = ChatFilterHandler.filterMessage(event.message)
            if (filter != null) {
                badMessages[event.player.uniqueId] = Pair(event.message, filter)

                val filterEvent = ChatMessageFilteredEvent(
                    event.player,
                    event.message
                )
                filterEvent.call()

                if (filterEvent.isCancelled) {
                    event.isCancelled = true
                } else {
                    event.message = filterEvent.message
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onPlayerChatHigh(event: AsyncPlayerChatEvent) {
        val player = event.player
        if (ChatSettings.muted && !player.hasPermission(Permissions.MUTE_CHAT_BYPASS)) {
            player.sendMessage("${ChatColor.RED}The chat is currently muted.")
            event.isCancelled = true
            return
        }

        val session = ChatSettings.slowed
        if (session != null && !player.hasPermission(Permissions.SLOW_CHAT_BYPASS)) {
            val time = session.players[player.uniqueId]
            if (time == null || time < System.currentTimeMillis()) {
                session.players[player.uniqueId] = System.currentTimeMillis() + session.duration
            } else {
                player.sendMessage("${ChatColor.RED}The chat is currently slowed. You can send another message in ${(time - System.currentTimeMillis()) / 1000} seconds.")
                event.isCancelled = true
                return
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerChatMonitor(event: AsyncPlayerChatEvent) {
        val player = event.player

        if (badMessages.containsKey(player.uniqueId)) {
            event.recipients.clear()
            event.recipients.add(event.player)

            val toSend = FancyMessage("[Filtered] ")
                .color(ChatColor.RED)
                .style(ChatColor.BOLD)
                .formattedTooltip(listOf(
                    FancyMessage("${ChatColor.YELLOW}This message was hidden from public chat."),
                    FancyMessage("${ChatColor.RED}Filter: ${badMessages[player.uniqueId]!!.second.description}")
                ))
                .then(player.displayName)
                .then("${ChatColor.WHITE}: ${event.message}")

            for (other in Bukkit.getOnlinePlayers()) {
                if (other.hasPermission(Permissions.FILTER_VIEW)) {
                    toSend.send(other)
                }
            }

            badMessages.remove(player.uniqueId)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onQuit(event: PlayerQuitEvent) {
        badMessages.remove(event.player.uniqueId)
    }

}