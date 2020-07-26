/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.source.captcha

import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.source.captcha.menu.CaptchaMenu
import org.bukkit.Bukkit
import java.util.*

object CaptchaHandler {

    private val completedCaptcha: MutableSet<UUID> = hashSetOf()

    fun initialLoad() {
        Tasks.asyncTimer(40L, 40L) {
            for (player in Bukkit.getOnlinePlayers()) {
                if (!hasCompletedCaptcha(player.uniqueId)) {
                    if (!Menu.currentlyOpenedMenus.containsKey(player.uniqueId)) {
                        CaptchaMenu().openMenu(player)
                    }
                }
            }
        }
    }

    fun hasCompletedCaptcha(uuid: UUID): Boolean {
        return completedCaptcha.contains(uuid)
    }

    fun completedCaptcha(uuid: UUID) {
        completedCaptcha.add(uuid)
    }

    fun forgetPlayer(uuid: UUID) {
        completedCaptcha.remove(uuid)
    }

}