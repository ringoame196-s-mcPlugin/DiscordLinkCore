package com.github.ringoame196_s_mcPlugin.managers

import com.github.ringoame196_s_mcPlugin.DataBaseData
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.io.File
import kotlin.random.Random

class LinkManager {
	private val databaseManager = DataBaseManager()

	fun makeLinkDataBaseFile(plugin: Plugin) {
		val dataBaseManager = DataBaseManager()
		File(plugin.dataFolder.path).mkdirs()
		val dataBaseFile = File(plugin.dataFolder, "linkData.db")
		val dataBaseFilePath = dataBaseFile.path
		DataBaseData.DataBaseFilePath = dataBaseFilePath
		if (!dataBaseFile.exists()) { // テーブル作成
			val makeLinkDataCommand =
				"CREATE TABLE ${DataBaseData.LINK_TABLE_NAME} (${DataBaseData.MC_UUID} BLOB NOT NULL, ${DataBaseData.DISCORD_ID} TEXT NOT NULL, ${DataBaseData.DISCORD_NAME} TEXT NOT NULL);"
			val makeLinkKeyDataCommand =
				"CREATE TABLE ${DataBaseData.LINK_KEY_TABLE_NAME} (${DataBaseData.AUTH_KEY} VARCHAR(5) PRIMARY KEY, ${DataBaseData.MC_UUID} BLOB NOT NULL, ${DataBaseData.MC_NAME} TEXT NOT NULL);"

			dataBaseManager.runSQLCommand(dataBaseFilePath, makeLinkDataCommand)
			dataBaseManager.runSQLCommand(dataBaseFilePath, makeLinkKeyDataCommand)
		}
	}

	fun resetLinkKeyTable() {
		val sqlCommand = "DELETE FROM ${DataBaseData.LINK_KEY_TABLE_NAME}"
		databaseManager.runSQLCommand(DataBaseData.DataBaseFilePath ?: return, sqlCommand)
	}

	fun isLinkingPlayer(mcUUID: String): Boolean {
		return acquisitionDiscordID(mcUUID) == null
	}

	fun issueAuthKey(player: Player): String? {
		var authKey: String?
		val max = 9999
		val playerUUID = player.uniqueId.toString()
		val playerName = player.name
		val tryMax = 15
		var tryCount = 0

		// 同じプレイヤーに認証キーを複数設定されないように
		deletingDuplicatePlayer(playerUUID)

		// 重ならない認証キーを生成する
		while (true) {
			authKey = Random.nextInt(max).toString()
			if (checkDuplicationAuthKey(authKey)) break
			tryCount++

			// 無限ループ対策
			if (tryCount > tryMax) {
				return null
			}
		}

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
		val sqlCommand =
			"SELECT ${DataBaseData.DISCORD_ID} FROM ${DataBaseData.LINK_KEY_TABLE_NAME} WHERE ${DataBaseData.MC_UUID} = ?"
		return databaseManager.acquisitionStringValue(
			DataBaseData.DataBaseFilePath ?: return null,
			sqlCommand,
			mutableListOf(mcUUID),
			DataBaseData.DISCORD_ID
		)
	}

	fun acquisitionDiscordName(mcUUID: String): String? {
		val sqlCommand =
			"SELECT ${DataBaseData.DISCORD_NAME} FROM ${DataBaseData.LINK_KEY_TABLE_NAME} WHERE ${DataBaseData.MC_UUID} = ?"
		return databaseManager.acquisitionStringValue(
			DataBaseData.DataBaseFilePath ?: return null,
			sqlCommand,
			mutableListOf(mcUUID),
			DataBaseData.DISCORD_NAME
		)
	}

	fun acquisitionPlayer(discordID: String): Player? {
		val sqlCommand =
			"SELECT ${DataBaseData.MC_UUID} FROM ${DataBaseData.LINK_KEY_TABLE_NAME} WHERE ${DataBaseData.DISCORD_ID} = ?"
		val playerUUID = databaseManager.acquisitionStringValue(
			DataBaseData.DataBaseFilePath ?: return null,
			sqlCommand,
			mutableListOf(discordID),
			DataBaseData.DISCORD_NAME
		) ?: return null
		return Bukkit.getPlayer(playerUUID)
	}
}
