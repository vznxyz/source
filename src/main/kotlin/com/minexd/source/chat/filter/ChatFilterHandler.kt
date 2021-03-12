package com.minexd.source.chat.filter

import com.google.gson.JsonObject
import net.evilblock.cubed.Cubed
import net.evilblock.pidgin.message.Message
import net.evilblock.pidgin.message.handler.IncomingMessageHandler
import net.evilblock.pidgin.message.listener.MessageListener
import com.minexd.source.Source
import java.util.*

object ChatFilterHandler : MessageListener {

    private const val MESSAGE_ID = "ChatFilterRefresh"

    private var filters: MutableMap<UUID, ChatFilter> = hashMapOf()

    fun initialLoad() {
        Cubed.instance.redis.runRedisCommand { redis ->
            val map = hashMapOf<UUID, ChatFilter>()
            for (filterId in redis.smembers("Source:ChatFilters.Filters")) {
                if (redis.exists("Source:ChatFilters.Filter.$filterId")) {
                    val filter =
                        ChatFilter(redis.hgetAll("Source:ChatFilters.Filter.$filterId"))
                    map[filter.id] = filter
                }
            }

            filters = map
        }
    }

    fun getFilters(): Collection<ChatFilter> {
        return filters.values
    }

    fun trackFilter(filter: ChatFilter) {
        filters[filter.id] = filter
    }

    fun forgetFilter(filter: ChatFilter) {
        filters.remove(filter.id)
    }

    fun saveFilter(filter: ChatFilter) {
        Cubed.instance.redis.runRedisCommand { redis ->
            redis.sadd("Source:ChatFilters.Filters", filter.id.toString())
            redis.hmset("Source:ChatFilters.Filter.${filter.id}", filter.toMap())
        }

        sendUpdate()
    }

    fun deleteFilter(filter: ChatFilter) {
        filters.remove(filter.id)

        Cubed.instance.redis.runRedisCommand { redis ->
            redis.srem("Source:ChatFilters.Filters", filter.id.toString())
            redis.del("Source:ChatFilters:Filter")
        }

        sendUpdate()
    }

    private fun sendUpdate() {
        Source.instance.pidgin.sendMessage(Message(MESSAGE_ID))
    }

    @IncomingMessageHandler(id = MESSAGE_ID)
    fun onChatFilterRefresh(data: JsonObject) {
        initialLoad()

        Source.instance.systemLog("Update triggered, fetched ${filters.size} chat filters")
    }

    fun filterMessage(message: String): ChatFilter? {
        for (filter in filters.values) {
            if (filter.pattern.matcher(message.toLowerCase()).find()) {
                return filter
            }
        }
        return null
    }

}