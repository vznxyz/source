package net.evilblock.source.messaging.listener

import net.evilblock.source.messaging.MessagingManager
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

object ChatIgnoreListeners : Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onAsyncPlayerChatEvent(event: AsyncPlayerChatEvent) {
        val recipientsIterator = event.recipients.iterator()
        while (recipientsIterator.hasNext()) {
            val recipient = recipientsIterator.next()
            if (MessagingManager.isIgnored(recipient.uniqueId, event.player.uniqueId)) {
                recipientsIterator.remove()
            }
        }
    }

}