package net.evilblock.source.chat.filter

import com.google.common.collect.Maps
import mkremins.fanciful.FancyMessage
import net.evilblock.source.chat.ChatSettings
import net.evilblock.source.util.Permissions
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

object ChatFilterListeners : Listener {

    private val badMessages: MutableMap<UUID, String> = Maps.newConcurrentMap()

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerChatLowest(event: AsyncPlayerChatEvent) {
        if (!event.player.hasPermission(Permissions.FILTER_BYPASS)) {
            val message = event.message.toLowerCase()
            for (filter in ChatFilter.bannedRegexes) {
                if (filter.pattern.matcher(message).find()) {
                    this.badMessages[event.player.uniqueId] = event.message
                    break
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
                .tooltip("${ChatColor.YELLOW}This message was hidden from public chat.")
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