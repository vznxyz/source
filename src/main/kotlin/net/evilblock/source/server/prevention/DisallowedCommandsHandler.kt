package net.evilblock.source.server.prevention

import com.google.gson.JsonObject
import net.evilblock.cubed.Cubed
import net.evilblock.pidgin.message.Message
import net.evilblock.pidgin.message.handler.IncomingMessageHandler
import net.evilblock.pidgin.message.listener.MessageListener
import net.evilblock.source.Source

object DisallowedCommandsHandler : MessageListener {

    private const val REDIS_KEY = "Source:DisallowedCommands"
    private const val MESSAGE_ID = "DisallowedCommandsUpdate"

    internal var disallowedCommands: MutableList<String> = arrayListOf()

    fun initialLoad() {
        fetch()
    }

    fun fetch() {
        Cubed.instance.redis.runRedisCommand { redis ->
            if (redis.exists(REDIS_KEY)) {
                disallowedCommands = redis.lrange(REDIS_KEY, 0, -1)
            } else {
                disallowedCommands.clear()
            }
        }
    }

    fun upload() {
        Cubed.instance.redis.runRedisCommand { redis ->
            redis.del(REDIS_KEY)

            if (disallowedCommands.isNotEmpty()) {
                redis.lpush(REDIS_KEY, *disallowedCommands.toTypedArray())
            }
        }

        Source.instance.pidgin.sendMessage(Message(MESSAGE_ID, mapOf()))
    }

    @IncomingMessageHandler(id = MESSAGE_ID)
    fun onDisallowedCommandsUpdate(data: JsonObject) {
        fetch()

        Source.instance.systemLog("Update triggered, fetched ${disallowedCommands.size} disallowed commands")
    }

}