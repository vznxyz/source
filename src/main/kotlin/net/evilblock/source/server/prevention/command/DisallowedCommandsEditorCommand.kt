package net.evilblock.source.server.prevention.command

import net.evilblock.cubed.command.Command
import net.evilblock.source.server.prevention.menu.EditDisallowedCommandsMenu
import net.evilblock.source.util.Permissions
import org.bukkit.entity.Player

object DisallowedCommandsEditorCommand {

    @Command(
        names = ["source disallowed-cmds editor"],
        description = "Opens the Disallowed Commands editor",
        permission = Permissions.DISALLOWED_COMMANDS_EDITOR
    )
    @JvmStatic
    fun execute(player: Player) {
        EditDisallowedCommandsMenu().openMenu(player)
    }

}