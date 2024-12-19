package com.github.ringoame196_s_mcPlugin.managers

import org.bukkit.entity.Player
import kotlin.random.Random

class LinkManager {
    fun creationAuthKey(player: Player): String? {
        val length = 3
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
        return authKey
    }

    private fun generateRandomString(length: Int): String {
        val characters = "123456789ABCDEFGHIJKLMNPQRSTUVWXYZ" // 1-9 と A-Z の文字列
        return (1..length)
            .map { characters[Random.nextInt(characters.length)] }
            .joinToString("")
    }

    private fun acquisitionMcUUIDFromAuthKey(authKey: String): String? {
        return null
    }

    fun acquisitionMcUUD(discordID: String): String? {
        return null
    }

    fun acquisitionDiscordID(mcUUID: String): String? {
        return null
    }
}
