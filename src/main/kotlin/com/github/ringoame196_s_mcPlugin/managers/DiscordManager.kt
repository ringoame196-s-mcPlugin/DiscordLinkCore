package com.github.ringoame196_s_mcPlugin.managers

import com.github.ringoame196_s_mcPlugin.data.Data
import com.github.ringoame196_s_mcPlugin.discord.DiscordCommandConst
import com.github.ringoame196_s_mcPlugin.discord.SlashCommandEvent
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin

class DiscordManager {
    fun bootDiscordBOT(plugin: Plugin) {
        val config = plugin.config

        val token = config.getString("token")
        val activity = config.getString("activity") ?: "Minecraft"

        var jda: JDA? = null

        try {
            if (token != "" && token != null) { // tokenが設定されていないと 実行しない
                val jdaBuilder = JDABuilder.createDefault(token)
                jdaBuilder.setActivity(Activity.playing(activity))
                jda = jdaBuilder.addEventListeners(SlashCommandEvent()).build() // bot起動
            } else {
                val message = "Token not set"
                Bukkit.getLogger().info(message) // token未設定時にメッセージを出す
            }
        } catch (e: Exception) {
            val errorMessage = "BOT起動中にエラーが発生しました。 ${e.message}"
            Bukkit.getLogger().info(errorMessage) // エラー文出力
        }

        jda ?: return // jdaが設定されていなければ処理終了
        // DiscordBOTコマンド登録
        val commands = mutableListOf(
            CommandData(DiscordCommandConst.LINK_COMMAND, DiscordCommandConst.LINK_COMMAND_LORE)
                .addOption(
                    OptionType.STRING,
                    DiscordCommandConst.AUTH_KEY_OPTION,
                    DiscordCommandConst.AUTH_KEY_OPTION_LORE,
                    true
                )
        )

        jda.updateCommands().addCommands(*commands.toTypedArray()).queue {
            Bukkit.getLogger().info("コマンドの登録が完了しました")
        }

        Data.jda = jda
    }
}
