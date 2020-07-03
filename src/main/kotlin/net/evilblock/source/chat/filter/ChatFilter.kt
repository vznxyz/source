package net.evilblock.source.chat.filter

object ChatFilter {

    val bannedRegexes = setOf(
        ChatFilterEntry(
            "Restricted Phrase \"ip farm\"",
            "[i1l1|]+p+ ?f[a4]+rm+"
        ),
        ChatFilterEntry(
            "Restricted Phrase \"dupe\"",
            "(dupe)|(duplication)"
        ),
        ChatFilterEntry("Racism \"Nigger\"", "n+[i1l|]+gg+[e3]+r+"),
        ChatFilterEntry("Racism \"Beaner\"", "b+[e3]+[a4]+n+[e3]+r+"),
        ChatFilterEntry(
            "Suicide Encouragement",
            "k+i+l+l+ *y*o*u+r+ *s+e+l+f+"
        ),
        ChatFilterEntry("Suicide Encouragement", "\\bk+y+s+\\b"),
        ChatFilterEntry("Offensive \"Faggot\"", "f+[a4]+g+[o0]+t+"),
        ChatFilterEntry(
            "IP Address",
            "(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])([.,])){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])"
        ),
        ChatFilterEntry(
            "Phishing Link \"optifine\"",
            "optifine\\.(?=\\w+)(?!net)"
        ),
        ChatFilterEntry(
            "Phishing Link \"gyazo\"",
            "gyazo\\.(?=\\w+)(?!com)"
        ),
        ChatFilterEntry(
            "Phishing Link \"prntscr\"",
            "prntscr\\.(?=\\w+)(?!com)"
        )
    )

}