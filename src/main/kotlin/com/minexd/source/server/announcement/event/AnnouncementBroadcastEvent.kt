package com.minexd.source.server.announcement.event

import net.evilblock.cubed.plugin.PluginEvent
import org.bukkit.entity.Player

class AnnouncementBroadcastEvent(val receivers: MutableList<Player>) : PluginEvent()