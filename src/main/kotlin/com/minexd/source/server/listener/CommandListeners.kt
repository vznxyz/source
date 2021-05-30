package com.minexd.source.server.listener

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.TabCompleteEvent

object CommandListeners : Listener {

    @EventHandler
    fun onTabCompletionEvent(event: TabCompleteEvent) {
        if (event.isCommand && event.buffer.length == 1) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onAsyncTabCompleteEvent(event: AsyncTabCompleteEvent) {
        if (event.isCommand && event.buffer.length == 1) {
            event.isCancelled = true
        }
    }

}