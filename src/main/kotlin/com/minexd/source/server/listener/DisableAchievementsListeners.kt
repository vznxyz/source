package com.minexd.source.server.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.WorldLoadEvent

object DisableAchievementsListeners : Listener {

    @EventHandler
    fun onWorldLoadEvent(event: WorldLoadEvent) {
        event.world.setGameRuleValue("announceAdvancements", "false");
    }

}