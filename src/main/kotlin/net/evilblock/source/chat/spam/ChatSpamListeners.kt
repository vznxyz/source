package net.evilblock.source.chat.spam

import mkremins.fanciful.FancyMessage
import net.evilblock.source.chat.spam.algorithm.JaroWrinkler
import net.evilblock.source.util.Permissions
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import java.util.*

object ChatSpamListeners : Listener {

    var SPAM_PREVENTION_ENABLED = true

    @Transient
    private var chatHistory = LinkedList<ChatHistoryRecord>()

    data class ChatHistoryRecord(val sender: UUID, val message: String, val time: Long = System.currentTimeMillis())

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    fun onAsyncPlayerChatEvent(event: AsyncPlayerChatEvent) {
        if (!SPAM_PREVENTION_ENABLED) {
            return
        }

        if (event.message.length < 12) {
            return
        }

        while (chatHistory.size > 60) {
            chatHistory.removeAt(0)
        }

        var matchingRecords = 0
        for (record in chatHistory) {
            if (System.currentTimeMillis() >= record.time + 5_000L) {
                continue
            }

            val apply = JaroWrinkler.apply(record.message, event.message)
            if (apply >= 0.80) {
                matchingRecords++
            } else {
                println(apply)
            }
        }

        chatHistory.add(ChatHistoryRecord(event.player.uniqueId, event.message))

        if (matchingRecords > 3) {
            event.recipients.clear()
            event.recipients.add(event.player)

            val toSend = FancyMessage("[Anti-Spam] ")
                .color(ChatColor.RED)
                .style(ChatColor.BOLD)
                .tooltip("${ChatColor.YELLOW}This message was hidden from public chat.")
                .then(event.player.displayName)
                .then("${ChatColor.WHITE}: ${event.message}")

            for (other in Bukkit.getOnlinePlayers()) {
                if (other.hasPermission(Permissions.ANTI_SPAM_VIEW)) {
                    toSend.send(other)
                }
            }
        }
    }

}