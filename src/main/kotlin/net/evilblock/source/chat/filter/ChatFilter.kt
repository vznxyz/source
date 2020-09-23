package net.evilblock.source.chat.filter

import java.util.*
import java.util.regex.Pattern

class ChatFilter(
    val id: UUID = UUID.randomUUID(),
    val description: String,
    regex: String
) {

    constructor(map: Map<String, String>) : this(
        UUID.fromString(map.getValue("ID")),
        map.getValue("Description"),
        map.getValue("Pattern")
    )

    var pattern: Pattern = Pattern.compile(regex)

    fun toMap(): Map<String, String> {
        return mapOf(
            "ID" to id.toString(),
            "Description" to description,
            "Pattern" to pattern.toString()
        )
    }

}