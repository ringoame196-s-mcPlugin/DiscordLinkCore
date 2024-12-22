package com.github.ringoame196_s_mcPlugin

import com.github.ringoame196_s_mcPlugin.managers.LinkManager
import org.bukkit.OfflinePlayer

object LinkManagerAPI {
    private fun linkManager() = LinkManager()

	fun acquisitionMcName(discordID: String): String? = linkManager().acquisitionMcName(discordID)
	fun acquisitionMcUUD(discordID: String): String? = linkManager().acquisitionMcUUD(discordID)
	fun acquisitionDiscordID(mcUUID: String): String? = linkManager().acquisitionDiscordID(mcUUID)
	fun acquisitionPlayer(discordID: String): OfflinePlayer? = linkManager().acquisitionPlayer(discordID)
}
