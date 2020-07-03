package net.evilblock.source.util

object Formats {

    @JvmStatic
    fun capitalizeFully(name: String): String {
        return if (name.length > 1) {
            if (name.contains("_")) {
                val sbName = StringBuilder()

                for (subName in name.split("_").toTypedArray()) {
                    sbName.append(subName.substring(0, 1).toUpperCase() + subName.substring(1).toLowerCase()).append(" ")
                }

                sbName.toString().substring(0, sbName.length - 1)
            } else {
                name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase()
            }
        } else {
            name.toUpperCase()
        }
    }

}