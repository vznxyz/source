/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package com.minexd.source.captcha

import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.bukkit.Tasks
import com.minexd.source.Source
import com.minexd.source.captcha.menu.CaptchaMenu
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import java.util.*
import java.util.concurrent.TimeUnit

object CaptchaHandler {

    private val completedCaptcha: MutableSet<UUID> = hashSetOf()

    fun initialLoad() {
        Tasks.asyncTimer(20L, 20L) {
            for (player in Bukkit.getOnlinePlayers()) {
                if (!hasCompletedCaptcha(player.uniqueId)) {
                    if (hasGracePeriod(player)) {
                        continue
                    }

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

    fun hasGracePeriod(player: Player): Boolean {
        return if (player.hasMetadata("CAPTCHA_GRACE_PERIOD")) {
            if (System.currentTimeMillis() >= player.getMetadata("CAPTCHA_GRACE_PERIOD")[0].asLong()) {
                removeGracePeriod(player)
                false
            } else {
                true
            }
        } else {
            false
        }
    }

    fun applyGracePeriod(player: Player) {
        player.setMetadata("CAPTCHA_GRACE_PERIOD", FixedMetadataValue(Source.instance, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(3L)))
    }

    fun removeGracePeriod(player: Player) {
        player.removeMetadata("CAPTCHA_GRACE_PERIOD", Source.instance)
    }

}