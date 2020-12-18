package net.evilblock.source.chat.filter.command

import net.evilblock.cubed.command.Command
import net.evilblock.source.chat.filter.ChatFilter
import net.evilblock.source.chat.filter.ChatFilterHandler
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender

object ChatFilterImportDefaultsCommand {

    @Command(
        names = ["source chat-filter import-defaults"],
        description = "Imports the default chat filters",
        permission = "op",
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        if (sender !is ConsoleCommandSender) {
            sender.sendMessage("${ChatColor.RED}That command must be executed through console.")
            return
        }

        for (filter in defaults) {
            ChatFilterHandler.trackFilter(filter)
            ChatFilterHandler.saveFilter(filter)
        }
    }

    private val defaults = listOf<ChatFilter>(
        ChatFilter(
            description = "Restricted Phrase \"ip farm\"",
            regex = "[i1l1|]+p+ ?f[a4]+rm+"
        ),
        ChatFilter(
            description = "Restricted Phrase \"dupe\"",
            regex = "(dupe)|(duplication)"
        ),
        ChatFilter(
            description = "Racism \"Nigger\"",
            regex = "n+[i1l|]+gg+[e3]+r+"
        ),
        ChatFilter(
            description = "Racism \"Beaner\"",
            regex = "b+[e3]+[a4]+n+[e3]+r+"
        ),
        ChatFilter(
            description = "Suicide Encouragement",
            regex = "k+i+l+l+ *y*o*u+r+ *s+e+l+f+"
        ),
        ChatFilter(
            description = "Suicide Encouragement",
            regex = "\\bk+y+s+\\b"
        ),
        ChatFilter(
            description = "Offensive \"Faggot\"",
            regex = "f+[a4]+g+[o0]+t+"
        ),
        ChatFilter(
            description = "IP Address",
            regex = "(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])([.,])){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])"
        ),
        ChatFilter(
            description = "Phishing Link \"optifine\"",
            regex = "optifine\\.(?=\\w+)(?!net)"
        ),
        ChatFilter(
            description = "Phishing Link \"gyazo\"",
            regex = "gyazo\\.(?=\\w+)(?!com)"
        ),
        ChatFilter(
            description = "Phishing Link \"prntscr\"",
            regex = "prntscr\\.(?=\\w+)(?!com)"
        )
    )

}