package net.evilblock.source.messaging

import mkremins.fanciful.FancyMessage
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.util.bukkit.SoundCompat
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.source.Source
import net.evilblock.source.chat.filter.ChatFilterHandler
import net.evilblock.source.chat.filter.event.PrivateMessageFilteredEvent
import net.evilblock.source.messaging.event.PlayerMessageEvent
import net.evilblock.source.util.Permissions
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

object MessagingManager : Listener {

    private const val REDIS_KEY = "Source:Messaging"

    val globalSpy: MutableSet<UUID> = hashSetOf()
    val ignoreList: MutableMap<UUID, MutableSet<UUID>> = hashMapOf()

    /**
     * Gets the last messaged player for a player.
     *
     * @param player the player
     *
     * @return the last player's uuid that [player] has messaged
     */
    fun getLastMessaged(player: UUID): UUID? {
        return Cubed.instance.redis.runRedisCommand { redis ->
            redis.hget("$REDIS_KEY:$player", "LastMessaged")
        }?.run { UUID.fromString(this) }
    }

    /**
     * Sets the last messaged player for a player.
     */
    fun setLastMessaged(player: UUID, lastMessaged: UUID) {
        Cubed.instance.redis.runRedisCommand { redis ->
            redis.hset("$REDIS_KEY:$player", hashMapOf("LastMessaged" to "$lastMessaged"))
        }
    }

    /**
     * Gets if a player's private messages are disabled.
     */
    fun isMessagesDisabled(player: UUID): Boolean {
        return Cubed.instance.redis.runRedisCommand { redis ->
            redis.hget("$REDIS_KEY:$player", "MessagesDisabled")
        }?.toBoolean() ?: false
    }

    /**
     * Toggles if a player's messages are disabled.
     */
    fun toggleMessages(player: UUID, value: Boolean): Boolean {
        Cubed.instance.redis.runRedisCommand { redis ->
            redis.hmset("$REDIS_KEY:$player", hashMapOf("MessagesDisabled" to value.toString()))
        }

        return value
    }

    /**
     * Gets if a player's message sounds are disabled.
     */
    fun isSoundsDisabled(player: UUID): Boolean {
        return Cubed.instance.redis.runRedisCommand { redis ->
            redis.hget("$REDIS_KEY:$player", "SoundsDisabled")
        }?.toBoolean() ?: false
    }

    /**
     * Toggles if a player's message sounds are disabled.
     */
    fun toggleSounds(player: UUID, value: Boolean): Boolean {
        Cubed.instance.redis.runRedisCommand { redis ->
            redis.hmset("$REDIS_KEY:$player", hashMapOf("SoundsDisabled" to value.toString()))
        }

        return value
    }

    /**
     * Retrieves a player's ignore list.
     */
    fun getIgnoreList(player: UUID): Set<UUID> {
        return Cubed.instance.redis.runRedisCommand { redis ->
            if (redis.exists("$REDIS_KEY:IgnoreList:$player")) {
                redis.smembers("$REDIS_KEY:IgnoreList:$player").map { UUID.fromString(it) }.toSet()
            } else {
                emptySet()
            }
        }
    }

    /**
     * Gets whether or not the [player] is ignored by the [target].
     */
    fun isIgnored(player: UUID, target: UUID): Boolean {
        return getIgnoreList(player).contains(target)
    }

    /**
     * Adds a target to a player's ignore list.
     */
    fun addToIgnoreList(player: UUID, target: UUID) {
        if (ignoreList.containsKey(player)) {
            ignoreList[player]?.add(target)
        }

        Cubed.instance.redis.runRedisCommand { redis ->
            redis.sadd("$REDIS_KEY:IgnoreList:$player", target.toString())
        }
    }

    /**
     * Removes a target from a player's ignore list.
     */
    fun removeFromIgnoreList(player: UUID, target: UUID) {
        if (ignoreList.containsKey(player)) {
            ignoreList[player]?.remove(target)
        }

        Cubed.instance.redis.runRedisCommand { redis ->
            redis.srem("$REDIS_KEY:IgnoreList:$player", target.toString())
        }
    }

    /**
     * Clears a player's ignore list.
     */
    fun clearIgnoreList(player: UUID) {
        Cubed.instance.redis.runRedisCommand { redis ->
            redis.del("$REDIS_KEY:IgnoreList:$player")
        }
    }

    /**
     * Determines whether a sender player can message a target.
     *
     * @param sender the player trying to send the message
     * @param target the player the [sender] is trying to send a message to
     *
     * @return true if the sender can message the target.
     */
    fun canMessage(sender: Player, target: Player): Boolean {
        if (sender.hasPermission(Permissions.MESSAGE_BYPASS)) {
            return true
        }

        if (!sender.canSee(target) && target.hasMetadata("Vanished") && !sender.hasPermission(Permissions.MESSAGE_VANISHED_BYPASS)) {
            sender.sendMessage("${ChatColor.RED}No player with the name ${target.name} found.")
            return false
        }

        if (isIgnored(sender.uniqueId, target.uniqueId)) {
            sender.sendMessage("${ChatColor.RED}That player is ignoring you.")
            return false
        }

        if (isIgnored(target.uniqueId, sender.uniqueId)) {
            sender.sendMessage("${ChatColor.RED}You are ignoring that player.")
            return false
        }

        if (isMessagesDisabled(sender.uniqueId)) {
            sender.sendMessage("${ChatColor.RED}You have messages turned off.")
        }

        if (isMessagesDisabled(target.uniqueId)) {
            sender.sendMessage(target.displayName + "${ChatColor.RED} has messages turned off.")
            return false
        }

        return true
    }

    /**
     * Sends a [message] from the [sender] to the [target].
     *
     * @param sender the player sending the [message]
     * @param target the player the [sender] is sending the [message] to
     * @param message the message the [sender] is trying to send to the [target]
     */
    fun sendMessage(sender: Player, target: Player, message: String) {
        val event = PlayerMessageEvent(sender, target, message)
        Bukkit.getPluginManager().callEvent(event)

        if (event.isCancelled) {
            return
        }

        val senderName = if (useMetadataAdapter()) {
            if (sender.hasMetadata(getMetadataAdapterKey())) {
                ChatColor.translateAlternateColorCodes('&', sender.getMetadata(getMetadataAdapterKey())[0].asString()) + sender.displayName
            } else {
                sender.playerListName
            }
        } else {
            sender.playerListName
        }

        val targetName = if (useMetadataAdapter()) {
            if (target.hasMetadata(getMetadataAdapterKey())) {
                ChatColor.translateAlternateColorCodes('&', target.getMetadata(getMetadataAdapterKey())[0].asString()) + target.displayName
            } else {
                target.playerListName
            }
        } else {
            target.playerListName
        }

        val filter = ChatFilterHandler.filterMessage(message)
        if (filter != null) {
            val filterEvent = PrivateMessageFilteredEvent(sender, target, event.message)
            filterEvent.call()

            if (filterEvent.isCancelled) {
                event.isCancelled = true
                return
            }

            sender.sendMessage("${ChatColor.GRAY}(To ${ChatColor.RESET}$targetName${ChatColor.GRAY}) $message")

            val filterAlert = FancyMessage("[Filtered] ${ChatColor.GRAY}(${ChatColor.RESET}$senderName ${ChatColor.GRAY}to $targetName${ChatColor.GRAY}) ${event.message}")
                .formattedTooltip(listOf(
                    FancyMessage("${ChatColor.YELLOW}This message was hidden from public chat."),
                    FancyMessage("${ChatColor.RED}Filter: ${filter.description}")
                ))

            for (player in Bukkit.getOnlinePlayers()) {
                if (player !== sender && player !== target && globalSpy.contains(player.uniqueId)) {
                    filterAlert.send(player)
                }
            }

            return
        }

        setLastMessaged(sender.uniqueId, target.uniqueId)
        setLastMessaged(target.uniqueId, sender.uniqueId)

        target.sendMessage("${ChatColor.GRAY}(From ${ChatColor.RESET}$senderName${ChatColor.GRAY}) $message")
        sender.sendMessage("${ChatColor.GRAY}(To ${ChatColor.RESET}$targetName${ChatColor.GRAY}) $message")

        if (!isSoundsDisabled(target.uniqueId)) {
            SoundCompat.MESSAGE_RECEIVED.playSound(target)
        }

        for (player in Bukkit.getOnlinePlayers()) {
            if (player !== sender && player !== target && globalSpy.contains(player.uniqueId)) {
                player.sendMessage("${ChatColor.GRAY}(${ChatColor.RESET}$senderName ${ChatColor.GRAY}to $targetName${ChatColor.GRAY}) " + message)
            }
        }
    }

    fun useMetadataAdapter(): Boolean {
        return Source.instance.config.getBoolean("messaging.use-metadata-adapter", true)
    }

    fun getMetadataAdapterKey(): String {
        return Source.instance.config.getString("messaging.metadata-adapter-key", "EP_PLAYER_LIST_NAME")
    }

    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        Tasks.async {
            ignoreList[event.player.uniqueId] = getIgnoreList(event.player.uniqueId).toMutableSet()
        }
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        ignoreList.remove(event.player.uniqueId)
    }

}