package net.evilblock.source.server.announcement

import com.google.gson.JsonObject
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.ReplaceOptions
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
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

object AnnouncementHandler : MessageListener {

    private const val MESSAGE_ID = "AnnouncementsUpdate"

    private val collection: MongoCollection<Document> = Cubed.instance.mongo.client.getDatabase("source").getCollection("announcements")

    private val index: AtomicInteger = AtomicInteger(0)
    internal var interval: Int = 60 * 3 // in seconds
    internal var announcements: MutableList<Announcement> = arrayListOf()

    private var task: BukkitTask? = null

    fun initialLoad() {
        fetch()
        startTask()
    }

    fun fetch() {
        Cubed.instance.redis.runRedisCommand { redis ->
            if (redis.exists("Source:Announcements.Interval")) {
                interval = redis.get("Source:Announcements.Interval").toInt()
            }
        }

        announcements.clear()

        val documents = collection.find()
        for (document in documents) {
            announcements.add(
                Announcement(
                    UUID.fromString(document.getString("id")),
                    document.getList("lines", String::class.java),
                    document.getInteger("order")
                )
            )
        }
    }

    fun setInterval(seconds: Int) {
        interval = seconds

        Cubed.instance.redis.runRedisCommand { redis ->
            redis.set("Source:Announcements.Interval", interval.toString())
        }

        Source.instance.pidgin.sendMessage(Message(MESSAGE_ID, mapOf()))
    }

    fun startTask() {
        if (task != null) {
            task!!.cancel()
        }

        task = Tasks.asyncTimer(20L * interval, 20L * interval) {
            if (index.get() >= announcements.size) {
                index.set(0)
            }

            if (index.get() == 0 && announcements.isEmpty()) {
                return@asyncTimer
            }

            val announcement = announcements[index.getAndIncrement()]

            for (line in announcement.lines) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', line))
            }
        }
    }

    fun saveAnnouncement(announcement: Announcement) {
        val document = Document()
        document["id"] = announcement.id.toString()
        document["lines"] = announcement.lines
        document["order"] = announcement.order

        collection.replaceOne(Document("id", announcement.id.toString()), document, ReplaceOptions().upsert(true))

        Source.instance.pidgin.sendMessage(Message(MESSAGE_ID, mapOf()))
    }

    fun deleteAnnouncement(announcement: Announcement) {
        collection.deleteOne(Document("id", announcement.id.toString()))

        Source.instance.pidgin.sendMessage(Message(MESSAGE_ID, mapOf()))
    }

    @IncomingMessageHandler(id = MESSAGE_ID)
    fun onAnnouncementsUpdate(data: JsonObject) {
        fetch()

        Source.instance.systemLog("Update triggered, fetched ${announcements.size} announcements")
    }

}