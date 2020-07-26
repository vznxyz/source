/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.source.captcha.listener

import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.source.captcha.CaptchaHandler
import net.evilblock.source.captcha.menu.CaptchaMenu
import net.evilblock.source.util.Permissions
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
        if (event.player.isOp || event.player.hasPermission(Permissions.CAPTCHA_BYPASS)) {
            CaptchaHandler.completedCaptcha(event.player.uniqueId)
        } else {
            Tasks.delayed(10L) {
                CaptchaMenu().openMenu(event.player)
            }
        }
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        CaptchaHandler.forgetPlayer(event.player.uniqueId)
    }

}