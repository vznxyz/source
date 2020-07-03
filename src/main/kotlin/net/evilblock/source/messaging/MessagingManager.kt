package net.evilblock.source.messaging

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.util.bukkit.SoundCompat
import net.evilblock.source.messaging.event.PlayerMessageEvent
import net.evilblock.source.util.Permissions
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

object MessagingManager {

    private const val REDIS_KEY = "Source:Messaging"
    val globalSpy: MutableSet<UUID> = hashSetOf()

    /**
     * Gets the last messaged player for a player.
     *
     * @param player the player
     *
     * @return the last player's uuid that [player] has messaged
     */
    fun getLastMessaged(player: UUID): UUID? {
        return Cubed.instance.redis.runRedisCommand { redis ->
            redis.hget("$REDIS_KEY:$player", "lastMessaged")
        }?.run { UUID.fromString(this) }
    }

    /**
     * Sets the last messaged player for a player.
     *
     * @param player the player
     */
    fun setLastMessaged(player: UUID, lastMessaged: UUID) {
        Cubed.instance.redis.runRedisCommand { redis ->
            redis.hset("$REDIS_KEY:$player", hashMapOf("lastMessaged" to "$lastMessaged"))
        }
    }

    /**
     * Gets if a player's messages are disabled.
     *
     * @param player the player
     */
    fun isMessagesDisabled(player: UUID): Boolean {
        return Cubed.instance.redis.runRedisCommand { redis ->
            redis.hget("$REDIS_KEY:$player", "messagesDisabled")
        }?.toBoolean() ?: false
    }

    /**
     * Toggles if a player's messages are disabled.
     *
     * @param player the player
     *
     * @return if the [player]'s messages are disabled after toggle
     */
    fun toggleMessages(player: UUID, value: Boolean): Boolean {
        Cubed.instance.redis.runRedisCommand { redis ->
            redis.hmset("$REDIS_KEY:$player", hashMapOf("messagesDisabled" to value.toString()))
        }

        return value
    }

    /**
     * Gets if a player's message sounds are disabled.
     *
     * @param player the player
     */
    fun isSoundsDisabled(player: UUID): Boolean {
        return Cubed.instance.redis.runRedisCommand { redis ->
            redis.hget("$REDIS_KEY:$player", "soundsDisabled")
        }?.toBoolean() ?: false
    }

    /**
     * Toggles if a player's message sounds are disabled.
     *
     * @param player the player
     *
     * @return if the [player]'s message sounds are disabled after toggle
     */
    fun toggleSounds(player: UUID, value: Boolean): Boolean {
        Cubed.instance.redis.runRedisCommand { redis ->
            redis.hmset("$REDIS_KEY:$player", hashMapOf("soundsDisabled" to value.toString()))
        }

        return value
    }

    /**
     * Gets whether or not the [player] is ignored by the [target].
     *
     * @param player the player
     * @param target the target
     *
     * @return if the [player] is ignored by the [target]
     */
    fun isIgnored(player: UUID, target: UUID): Boolean {
        return Cubed.instance.redis.runRedisCommand { redis ->
            redis.hget("$REDIS_KEY:ignoreList:$player", "$target")
        }?.toBoolean() == true
    }

    /**
     * Adds a target to a player's ignore list.
     */
    fun addToIgnoreList(player: UUID, target: UUID) {
        Cubed.instance.redis.runRedisCommand { redis ->
            redis.hmset("$REDIS_KEY:ignoreList:$player", hashMapOf(target.toString() to "true"))
        }
    }

    /**
     * Removes a target from a player's ignore list.
     *
     * @param player the player's UUID
     * @param target the target's UUID
     */
    fun removeFromIgnoreList(player: UUID, target: UUID) {
        Cubed.instance.redis.runRedisCommand { redis ->
            redis.hdel("$REDIS_KEY:ignoreList:$player", target.toString())
        }
    }

    /**
     * Clears a player's ignore list.
     *
     * @param player the player's UUID
     */
    fun clearIgnoreList(player: UUID) {
        Cubed.instance.redis.runRedisCommand { redis ->
            redis.del("$REDIS_KEY:ignoreList:$player")
        }
    }

    /**
     * Retrieves a player's ignore list.
     *
     * @param player the player's UUID
     *
     * @return the player's ignore list as {@link List<UUID>}
     */
    fun getIgnoreList(player: UUID): List<UUID> {
        return Cubed.instance.redis.runRedisCommand { redis ->
            redis.hgetAll("$REDIS_KEY:ignoreList:$player").map { entry -> UUID.fromString(entry.key) }
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
            sender.sendMessage(ChatColor.RED.toString() + "That player is ignoring you.")
            return false
        }

        if (isIgnored(target.uniqueId, sender.uniqueId)) {
            sender.sendMessage(ChatColor.RED.toString() + "You are ignoring that player.")
            return false
        }

        if (isMessagesDisabled(sender.uniqueId)) {
            sender.sendMessage(ChatColor.RED.toString() + "You have messages turned off.")
        }

        if (isMessagesDisabled(target.uniqueId)) {
            sender.sendMessage(target.displayName + ChatColor.RED + " has messages turned off.")
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

        setLastMessaged(sender.uniqueId, target.uniqueId)
        setLastMessaged(target.uniqueId, sender.uniqueId)

        target.sendMessage("${ChatColor.GRAY}(From ${ChatColor.WHITE}${sender.displayName}${ChatColor.GRAY}) " + message)
        sender.sendMessage("${ChatColor.GRAY}(To ${ChatColor.WHITE}${target.displayName}${ChatColor.GRAY}) " + message)

        if (!isSoundsDisabled(target.uniqueId)) {
            SoundCompat.MESSAGE_RECEIVED.playSound(target)
        }

        for (player in Bukkit.getOnlinePlayers()) {
            if (player !== sender) {
                if (player === target) {
                    continue
                }

                if (globalSpy.contains(player.uniqueId)) {
                    player.sendMessage(ChatColor.GRAY.toString() + "(" + ChatColor.WHITE + sender.displayName + ChatColor.GRAY + " to " + ChatColor.WHITE + target.displayName + ChatColor.GRAY + ") " + message)
                }
            }
        }
    }

}