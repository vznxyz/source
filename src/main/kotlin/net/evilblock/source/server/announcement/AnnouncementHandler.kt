package net.evilblock.source.server.announcement

import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.mongodb.client.MongoCollection
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.pidgin.message.Message
import net.evilblock.pidgin.message.handler.IncomingMessageHandler
import net.evilblock.pidgin.message.listener.MessageListener
import net.evilblock.source.Source
import org.bson.Document
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.scheduler.BukkitTask
import java.util.concurrent.atomic.AtomicInteger

object AnnouncementHandler : MessageListener {

    private lateinit var mongoCollection: MongoCollection<Document>

    private var groups: MutableList<AnnouncementGroup> = arrayListOf()
    private var activeGroup: AnnouncementGroup? = null

    private val index: AtomicInteger = AtomicInteger(0)
    private var task: BukkitTask? = null

    fun initialLoad() {
        mongoCollection = Cubed.instance.mongo.client.getDatabase("Source").getCollection("announcements")

        fetchGroups()

        activeGroup = getGroupById(Source.instance.config.getString("announcements.active-group", "Hub"))

        startTask()
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
        activeGroup = group

        Source.instance.config.set("announcements.active-group", group?.id)
        Source.instance.saveConfig()
    }

    private fun deserializeDocument(document: Document): AnnouncementGroup {
        return Cubed.gson.fromJson(document.toString(), object : TypeToken<AnnouncementGroup>() {}.type)
    }

    fun fetchGroups() {
        val groups = arrayListOf<AnnouncementGroup>()

        val iterator = mongoCollection.find().iterator()
        while (iterator.hasNext()) {
            groups.add(deserializeDocument(iterator.next()))
        }

        this.groups = groups
    }

    fun fetchGroup(id: String): AnnouncementGroup? {
        val document = mongoCollection.find(Document("_id", id)).first() ?: return null
        return deserializeDocument(document)
    }

    fun saveGroup(group: AnnouncementGroup) {
        if (!groups.contains(group)) {
            groups.add(group)
        }

        mongoCollection.replaceOne(Document("_id", group.id), Document.parse(Cubed.gson.toJson(group)))
        Source.instance.pidgin.sendMessage(Message(id = "Announcement", data = mapOf("ID" to group.id, "Action" to "UPDATE")))
    }

    fun deleteGroup(group: AnnouncementGroup) {
        groups.remove(group)
        mongoCollection.deleteOne(Document("_id", group.id))
        Source.instance.pidgin.sendMessage(Message(id = "Announcement", data = mapOf("ID" to group.id, "Action" to "DELETE")))
    }

    fun startTask() {
        if (activeGroup == null) {
            throw IllegalStateException("There is no active group")
        }

        val activeGroup = activeGroup!!

        if (task != null) {
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

            for (line in announcement.lines) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', line))
            }
        }
    }

    @IncomingMessageHandler(id = "Announcement")
    fun onAnnouncementsUpdate(data: JsonObject) {
        val id = data["ID"].asString
        val action = data["Action"].asString

        when (action) {
            "UPDATE" -> {
                val freshGroup = fetchGroup(id)
                if (freshGroup != null) {
                    val existingGroup = getGroupById(id)
                    if (existingGroup != null) {
                        existingGroup.announcements = freshGroup.announcements
                        existingGroup.interval = freshGroup.interval
                    } else {
                        groups.add(freshGroup)
                    }
                }
            }
            "DELETE" -> {
                val group = getGroupById(id)
                if (group != null) {
                    groups.remove(group)

                    if (activeGroup == group) {
                        activeGroup = null
                    }
                }
            }
        }

        Source.instance.systemLog("$action triggered for $id announcement group")
    }

}