package com.github.ringoame196_s_mcPlugin.managers

import com.github.ringoame196_s_mcPlugin.data.DBConstData
import com.github.ringoame196_s_mcPlugin.data.Data
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.UUID
import kotlin.random.Random

class LinkManager() {
    private val databaseManager = DataBaseManager("${Data.plugin?.dataFolder}/data.db")

    fun creationAuthKey(player: Player): String? {
        val length = 4
        val mcUUID = player.uniqueId.toString()
        val mcName = player.name
        var authKey: String

        var count = 0
        val maxTryTimes = 10

        while (true) {
            authKey = generateRandomString(length)
            if (count >= maxTryTimes) return null
            count ++
            if (acquisitionMcUUIDFromAuthKey(authKey) == null) break
        }

        val sql = "INSERT INTO ${DBConstData.TABLE_NAME} (${DBConstData.MC_UUID_KEY}, ${DBConstData.MC_NAME_KEY}, ${DBConstData.AUTH_KEY_KEY}) VALUES (?,?,?) ON CONFLICT(${DBConstData.MC_UUID_KEY}) DO UPDATE SET ${DBConstData.MC_NAME_KEY} = excluded.${DBConstData.MC_NAME_KEY}, ${DBConstData.AUTH_KEY_KEY} = excluded.${DBConstData.AUTH_KEY_KEY};"
        databaseManager.executeUpdate(sql, mutableListOf(mcUUID, mcName, authKey))

        return authKey
    }

    private fun generateRandomString(length: Int): String {
        val characters = "12346789ABCDEFGHIJKLMNPQRTUVWXYZ" // 1-9 と A-Z の文字列
        return (1..length)
            .map { characters[Random.nextInt(characters.length)] }
            .joinToString("")
    }

    private fun acquisitionMcUUIDFromAuthKey(authKey: String): String? {
        val sql = "SELECT ${DBConstData.MC_UUID_KEY} FROM ${DBConstData.TABLE_NAME} WHERE ${DBConstData.AUTH_KEY_KEY} = ?;"
        return databaseManager.acquisitionValue(sql, mutableListOf(authKey), DBConstData.MC_UUID_KEY)?.toString()
    }

    fun acquisitionMcName(discordID: String): String? {
        val sql = "SELECT ${DBConstData.MC_NAME_KEY} FROM ${DBConstData.TABLE_NAME} WHERE ${DBConstData.DISCORD_ID_KEY} = ?;"
        return databaseManager.acquisitionValue(sql, mutableListOf(discordID), DBConstData.MC_NAME_KEY)?.toString()
    }

    fun acquisitionMcUUD(discordID: String): String? {
        val sql = "SELECT ${DBConstData.MC_UUID_KEY} FROM ${DBConstData.TABLE_NAME} WHERE ${DBConstData.DISCORD_ID_KEY} = ?;"
        return databaseManager.acquisitionValue(sql, mutableListOf(discordID), DBConstData.MC_UUID_KEY)?.toString()
    }

    fun acquisitionDiscordID(mcUUID: String): String? {
        val sql = "SELECT ${DBConstData.DISCORD_ID_KEY} FROM ${DBConstData.TABLE_NAME} WHERE ${DBConstData.MC_UUID_KEY} = ?;"
        return databaseManager.acquisitionValue(sql, mutableListOf(mcUUID), DBConstData.DISCORD_ID_KEY)?.toString()
    }

    fun acquisitionPlayer(discordID: String): OfflinePlayer? {
        val mcUUID = acquisitionMcUUD(discordID)
        return Bukkit.getOfflinePlayer(UUID.fromString(mcUUID))
    }

    fun authenticate(authKey: String, discordID: String): Boolean {
        val mcUUID = acquisitionMcUUIDFromAuthKey(authKey) ?: return false
        val sql = "UPDATE ${DBConstData.TABLE_NAME} SET ${DBConstData.DISCORD_ID_KEY} = ?, ${DBConstData.AUTH_KEY_KEY} = null, ${DBConstData.IS_VERIFIED_KEY} = 1 WHERE ${DBConstData.AUTH_KEY_KEY} = ?;"
        databaseManager.executeUpdate(sql, mutableListOf(discordID, authKey))

        val player = Bukkit.getPlayer(UUID.fromString(mcUUID))
        val message = "${ChatColor.GOLD}Discordアカウントと連携しました"
        player?.sendMessage(message)

        return true
    }

    fun unlink(mcUUID: String) {
        val sql = "UPDATE ${DBConstData.TABLE_NAME} SET ${DBConstData.DISCORD_ID_KEY} = null, ${DBConstData.AUTH_KEY_KEY} = null, ${DBConstData.IS_VERIFIED_KEY} = 0 WHERE ${DBConstData.MC_UUID_KEY} = ?;"
        databaseManager.executeUpdate(sql, mutableListOf(mcUUID))
    }

    fun resetAuthKey() {
        val sql = "UPDATE ${DBConstData.TABLE_NAME} SET ${DBConstData.AUTH_KEY_KEY} = null;"
        databaseManager.executeUpdate(sql)
    }
}
