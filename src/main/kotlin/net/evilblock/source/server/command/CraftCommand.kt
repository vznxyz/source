package net.evilblock.source.server.command

import net.evilblock.cubed.command.Command
import org.bukkit.entity.Player

object CraftCommand {

    @Command(
        names = ["craft"],
        description = "Opens a crafting table",
        permission = "essentials.craft"
    )
    @JvmStatic
    fun rename(sender: Player) {
        sender.openWorkbench(sender.location, true)
    }

}