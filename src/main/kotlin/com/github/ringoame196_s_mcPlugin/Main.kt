package com.github.ringoame196_s_mcPlugin

import com.github.ringoame196_s_mcPlugin.commands.Command
import com.github.ringoame196_s_mcPlugin.commands.TabCompleter
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class Main : JavaPlugin() {
    private val plugin = this

    override fun onEnable() {
        super.onEnable()

        saveDefaultConfig() // config生成
        bootDiscordBOT()

        if (!File(plugin.dataFolder, "data.db").exists()) saveResource("data.db", false)

        val command = getCommand("dcore")
        command!!.setExecutor(Command())
        command.tabCompleter = TabCompleter()
    }

    private fun bootDiscordBOT() {
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
                logger.info(message) // token未設定時にメッセージを出す
            }
        } catch (e: Exception) {
            val errorMessage = "BOT起動中にエラーが発生しました。 ${e.message}"
            logger.info(errorMessage) // エラー文出力
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
            logger.info("コマンドの登録が完了しました")
        }

        Data.jda = jda
    }
}
