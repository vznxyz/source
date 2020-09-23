package net.evilblock.source.messaging.event

import net.evilblock.cubed.plugin.PluginEvent
import java.util.*

class ToggleSoundsEvent(val uuid: UUID, val playSounds: Boolean) : PluginEvent()