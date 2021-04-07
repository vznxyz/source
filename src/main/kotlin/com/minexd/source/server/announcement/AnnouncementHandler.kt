package com.minexd.source.server.announcement

import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.ReplaceOptions
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.pidgin.message.Message
import net.evilblock.pidgin.message.handler.IncomingMessageHandler
import net.evilblock.pidgin.message.listener.MessageListener
import com.minexd.source.Source
import com.minexd.source.server.announcement.event.AnnouncementBroadcastEvent
import net.evilblock.cubed.serializers.Serializers
import org.bson.Document
import org.bson.json.JsonMode
import org.bson.json.JsonWriterSettings
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.scheduler.BukkitTask
import java.util.concurrent.atomic.AtomicInteger

object AnnouncementHandler : MessageListener {

    private val JSON_WRITER_SETTINGS = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build()

    private lateinit var mongoCollection: MongoCollection<Document>

    private var groups: MutableList<AnnouncementGroup> = arrayListOf()
    private var activeGroup: AnnouncementGroup? = null

    private val index: AtomicInteger = AtomicInteger(0)
    private var task: BukkitTask? = null

    fun initialLoad() {
        mongoCollection = Cubed.instance.mongo.client.getDatabase("Source").getCollection("announcements")

        fetchGroups()

        activeGroup = getGroupById(Source.instance.config.getString("announcements.active-group", "Hub"))

        if (activeGroup != null) {
            startTask()
        }
    }

    fun getGroups(): List<AnnouncementGroup> {
        return groups
    }

    fun getGroupById(id: String): AnnouncementGroup? {
        for (group in groups) {
            if (group.id.equals(id, ignoreCase = true)) {
                return group
            }
        }
        return null
    }

    fun getActiveGroup(): AnnouncementGroup? {
        return activeGroup
    }

    fun setActiveGroup(group: AnnouncementGroup?) {
        if (task != null && !task!!.isCancelled) {
            task!!.cancel()
        }

        activeGroup = group

        if (activeGroup != null) {
            startTask()
        }

        Source.instance.config.set("announcements.active-group", group?.id)
        Source.instance.saveConfig()
    }

    private fun deserializeDocument(document: Document): AnnouncementGroup {
        return Serializers.gson.fromJson(document.toJson(JSON_WRITER_SETTINGS), object : TypeToken<AnnouncementGroup>() {}.type)
    }

    fun fetchGroups() {
        val groups = arrayListOf<AnnouncementGroup>()

        val iterator = mongoCollection.find().iterator()
        while (iterator.hasNext()) {
            groups.add(deserializeDocument(iterator.next()))
        }

        AnnouncementHandler.groups = groups
    }

    fun fetchGroup(id: String): AnnouncementGroup? {
        val document = mongoCollection.find(Document("id", id)).first() ?: return null
        return deserializeDocument(document)
    }

    fun saveGroup(group: AnnouncementGroup) {
        if (!groups.contains(group)) {
            groups.add(group)
        }

        mongoCollection.replaceOne(Document("id", group.id), Document.parse(Serializers.gson.toJson(group)), ReplaceOptions().upsert(true))
        Source.instance.pidgin.sendMessage(Message(id = "Announcement", data = mapOf("ID" to group.id, "Action" to "UPDATE")))
    }

    fun deleteGroup(group: AnnouncementGroup) {
        groups.remove(group)
        mongoCollection.deleteOne(Document("id", group.id))
        Source.instance.pidgin.sendMessage(Message(id = "Announcement", data = mapOf("ID" to group.id, "Action" to "DELETE")))
    }

    fun startTask() {
        if (activeGroup == null) {
            throw IllegalStateException("There is no active group")
        }

        val activeGroup = activeGroup!!

        if (task != null && !task!!.isCancelled) {
            task!!.cancel()
        }

        task = Tasks.asyncTimer(60L, 20L * activeGroup.interval) {
            if (index.get() >= activeGroup.announcements.size) {
                index.set(0)
            }

            if (index.get() == 0 && activeGroup.announcements.isEmpty()) {
                return@asyncTimer
            }

            val announcement = activeGroup.announcements[index.getAndIncrement()]

            val event = AnnouncementBroadcastEvent(ArrayList(Bukkit.getOnlinePlayers()))
            event.call()

            if (event.receivers.isEmpty()) {
                return@asyncTimer
            }

            val lines = announcement.lines.map { ChatColor.translateAlternateColorCodes('&', it) }

            for (player in event.receivers) {
                for (line in lines) {
                    player.sendMessage(line)
                }
            }
        }
    }

    @IncomingMessageHandler(id = "Announcement")
    fun onAnnouncementsUpdate(data: JsonObject) {
        val id = data["ID"].asString

        when (data["Action"].asString) {
            "UPDATE" -> {
                val freshGroup =
                    fetchGroup(id)
                if (freshGroup != null) {
                    val existingGroup =
                        getGroupById(id)
                    if (existingGroup != null) {
                        existingGroup.announcements = freshGroup.announcements
                        existingGroup.interval = freshGroup.interval

                        // interval might have changed, lets restart the task
                        if (activeGroup == existingGroup) {
                            startTask()
                        }
                    } else {
                        groups.add(freshGroup)
                    }

                    Source.instance.systemLog("Update triggered for $id announcement group")
                } else {
                    Source.instance.systemLog("${ChatColor.RED}Update triggered for $id announcement group, but couldn't fetch updates")
                }
            }
            "DELETE" -> {
                val group =
                    getGroupById(id)
                if (group != null) {
                    groups.remove(group)

                    if (activeGroup == group) {
                        activeGroup = null
                    }
                }

                Source.instance.systemLog("Delete triggered for $id announcement group")
            }
        }
    }

}