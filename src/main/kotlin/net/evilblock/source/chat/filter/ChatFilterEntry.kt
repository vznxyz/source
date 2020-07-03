package net.evilblock.source.chat.filter

import java.util.regex.Pattern

class ChatFilterEntry(val id: String, regex: String) {

    val pattern = Pattern.compile(regex)

}