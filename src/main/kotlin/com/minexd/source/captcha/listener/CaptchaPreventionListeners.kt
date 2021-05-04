/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package com.minexd.source.captcha.listener

import com.minexd.source.Source
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.nms.MinecraftReflection
import com.minexd.source.captcha.CaptchaHandler
import com.minexd.source.captcha.menu.CaptchaMenu
import com.minexd.source.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object CaptchaPreventionListeners : Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onPlayerCommandPreprocessEvent(event: PlayerCommandPreprocessEvent) {
        if (!CaptchaHandler.hasCompletedCaptcha(event.player.uniqueId)) {
            event.isCancelled = true
            event.player.sendMessage("${ChatColor.RED}You can't execute any commands until you've completed the captcha!")
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onAsyncPlayerChatEvent(event: AsyncPlayerChatEvent) {
        if (!CaptchaHandler.hasCompletedCaptcha(event.player.uniqueId)) {
            event.isCancelled = true
            event.player.sendMessage("${ChatColor.RED}You can't chat until you've completed the captcha!")
            return
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        CaptchaHandler.applyGracePeriod(event.player)

        if (MinecraftReflection.getHandle(event.player)::class.java.simpleName == "FakeEntityPlayer") {
            CaptchaHandler.completedCaptcha(event.player.uniqueId)
            return
        }

        if (Source.instance.config.getBoolean("captcha")) {
            Tasks.delayed(10L) {
                if (event.player.isOp || event.player.hasPermission(Permissions.CAPTCHA_BYPASS)) {
                    CaptchaHandler.completedCaptcha(event.player.uniqueId)
                } else {
                    CaptchaMenu().openMenu(event.player)
                }
            }
        } else {
            CaptchaHandler.completedCaptcha(event.player.uniqueId)
        }
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        CaptchaHandler.removeGracePeriod(event.player)
        CaptchaHandler.forgetPlayer(event.player.uniqueId)
    }

}