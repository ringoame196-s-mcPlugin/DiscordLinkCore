package com.github.ringoame196_s_mcPlugin.managers

import com.github.ringoame196_s_mcPlugin.DataBaseData
import org.bukkit.entity.Player
import kotlin.random.Random

class LinkManager {
    private val databaseManager = DataBaseManager()

    fun isLinkingPlayer(mcUUID: String): Boolean {
        return acquisitionDiscordID(mcUUID) == null
    }

    fun issueAuthKey(player: Player): String? {
        var authKey: String?
        val max = 99999
        val playerUUID = player.uniqueId.toString()
        val playerName = player.name

        // 重ならない認証キーを生成する
        while (true) {
            authKey = Random.nextInt(max).toString()
            if (checkDuplicationAuthKey(authKey)) break
        }

        // 同じプレイヤーに認証キーを複数設定されないように
        deletingDuplicatePlayer(playerUUID)

        try {
            // 認証キーを登録
            val sqlCommand =
                "INSERT INTO ${DataBaseData.LINK_KEY_TABLE_NAME} (${DataBaseData.AUTH_KEY}, ${DataBaseData.MC_UUID}, ${DataBaseData.MC_NAME}) VALUES (?,?,?);"
            databaseManager.runSQLCommand(
                DataBaseData.DataBaseFilePath ?: return null,
                sqlCommand,
                mutableListOf(authKey ?: return null, playerUUID, playerName)
            )
        } catch (e: Exception) {
            // db保存時にエラーが起きれば プレイヤーに認証キーを渡さないように
            return null
        }

        return authKey
    }

    private fun checkDuplicationAuthKey(authKey: String): Boolean {
        val sqlCommand =
            "SELECT ${DataBaseData.MC_UUID} FROM ${DataBaseData.LINK_KEY_TABLE_NAME} WHERE ${DataBaseData.AUTH_KEY} = ?;"
        return databaseManager.acquisitionStringValue(
            DataBaseData.DataBaseFilePath ?: return false,
            sqlCommand,
            mutableListOf(authKey),
            DataBaseData.AUTH_KEY
        ) == null
    }

    private fun deletingDuplicatePlayer(playerUUID: String) {
        val deleteCommand = "DELETE FROM ${DataBaseData.LINK_KEY_TABLE_NAME} WHERE ${DataBaseData.MC_UUID} = ?"
        databaseManager.runSQLCommand(
            DataBaseData.DataBaseFilePath ?: return,
            deleteCommand,
            mutableListOf(playerUUID)
        )
    }

    fun acquisitionDiscordID(mcUUID: String): String? {
        return null
    }

    fun acquisitionDiscordName(mcUUID: String): String? {
        return null
    }

    fun acquisitionPlayer(discordID: String): Player? {
        return null
    }
}
