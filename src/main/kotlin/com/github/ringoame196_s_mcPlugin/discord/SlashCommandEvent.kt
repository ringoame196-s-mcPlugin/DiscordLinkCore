package com.github.ringoame196_s_mcPlugin.discord

import com.github.ringoame196_s_mcPlugin.managers.LinkManager
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin

class SlashCommandEvent(plugin: Plugin) : ListenerAdapter() {
    private val linkManager = LinkManager(plugin)

    override fun onSlashCommand(e: SlashCommandEvent) {
        val command = e.name

        when (command) {
            DiscordCommandConst.LINK_COMMAND -> linkCommand(e)
        }
    }

    private fun linkCommand(e: SlashCommandEvent) {
        val discordID = e.user.id
        val authKey = e.getOption(DiscordCommandConst.AUTH_KEY_OPTION)?.asString ?: return
        try {
            val run = linkManager.authenticate(authKey, discordID)

            if (run) {
                val mcName = linkManager.acquisitionMcName(discordID)
                val message = "${mcName}と連携しました"
                e.reply(message).setEphemeral(true).queue()
            } else {
                val message = "マイクラアカウントと連携に失敗しました 認証番号をご確認ください"
                e.reply(message).queue()
            }
        } catch (error: Exception) {
            val message = "マイクラアカウントと連携中にエラーが発生しました"
            e.reply(message).setEphemeral(true).queue()
            Bukkit.getLogger().info("[エラー]${error.message}")
        }
    }
}
