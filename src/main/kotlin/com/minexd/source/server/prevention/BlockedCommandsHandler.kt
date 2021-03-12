package com.minexd.source.server.prevention

import com.google.gson.JsonObject
import net.evilblock.cubed.Cubed
import net.evilblock.pidgin.message.Message
import net.evilblock.pidgin.message.handler.IncomingMessageHandler
import net.evilblock.pidgin.message.listener.MessageListener
import com.minexd.source.Source

object BlockedCommandsHandler : MessageListener {

    private const val REDIS_KEY = "Source:BlockedCommands"
    private const val MESSAGE_ID = "BlockedCommandsUpdate"

    internal var blockedCommands: MutableList<String> = arrayListOf()

    fun initialLoad() {
        Cubed.instance.redis.runRedisCommand { redis ->
            if (redis.exists(REDIS_KEY)) {
                blockedCommands = redis.lrange(
                    REDIS_KEY, 0, -1)
            } else {
                blockedCommands.clear()
            }
        }
    }

    fun saveToRedis() {
        Cubed.instance.redis.runRedisCommand { redis ->
            redis.del(REDIS_KEY)

            if (blockedCommands.isNotEmpty()) {
                redis.lpush(REDIS_KEY, *blockedCommands.toTypedArray())
            }
        }

        Source.instance.pidgin.sendMessage(Message(MESSAGE_ID, mapOf()))
    }

    @IncomingMessageHandler(id = MESSAGE_ID)
    fun onBlockedCommandsUpdate(data: JsonObject) {
        initialLoad()

        Source.instance.systemLog("Update triggered, fetched ${blockedCommands.size} blocked commands")
    }

}