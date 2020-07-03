package net.evilblock.source.server

object ServerSettings {

    internal val disallowedCommands = setOf(
        "/calc",
        "/calculate",
        "/eval",
        "/evaluate",
        "/solve",
        "/calc",
        "/eval",
        "calc",
        "eval",
        "me",
        "pl",
        "bukkit:me",
        "icanhasbukkit",
        "bukkit:icanhasbukkit",
        "minecraft:me",
        "bukkit:plugins",
        "bukkit:pl",
        "tell"
    )

}