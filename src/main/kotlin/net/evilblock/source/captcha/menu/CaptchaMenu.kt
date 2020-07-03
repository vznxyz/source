/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.source.captcha.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.bukkit.ColorUtil
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.source.captcha.CaptchaHandler
import net.evilblock.source.util.Formats
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class CaptchaMenu : Menu() {

    private val color: ChatColor = ColorUtil.CHAT_COLOR_TO_WOOL_DATA.keys.random()

    override fun getTitle(player: Player): String {
        return "${color}Select the ${ChatColor.BOLD}${Formats.capitalizeFully(color.name)} ${color}wool"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (i in 0..26) {
            buttons[i] = WrongAnswerButton()
        }

        buttons[(0..26).random()] = CorrectAnswerButton()

        return buttons
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            Tasks.delayed(1L) {
                CaptchaMenu().openMenu(player) // new menu instead of re-using this one
            }
        }
    }

    private inner class CorrectAnswerButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GREEN}${ChatColor.BOLD}CLICK ME!"
        }

        override fun getMaterial(player: Player): Material {
            return Material.WOOL
        }

        override fun getDamageValue(player: Player): Byte {
            return ColorUtil.CHAT_COLOR_TO_WOOL_DATA.getValue(color).toByte()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            CaptchaHandler.completedCaptcha(player.uniqueId)
            player.closeInventory()
        }
    }

    private inner class WrongAnswerButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.RED}${ChatColor.BOLD}DON'T CLICK ME!"
        }

        override fun getMaterial(player: Player): Material {
            return Material.WOOL
        }

        override fun getDamageValue(player: Player): Byte {
            return ColorUtil.CHAT_COLOR_TO_WOOL_DATA.filter { it.key != color }.values.random().toByte()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            CaptchaMenu().openMenu(player)
        }
    }

}