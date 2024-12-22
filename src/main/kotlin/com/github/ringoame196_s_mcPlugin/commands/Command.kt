package com.github.ringoame196_s_mcPlugin.commands

import com.github.ringoame196_s_mcPlugin.data.Data
import com.github.ringoame196_s_mcPlugin.managers.LinkManager
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Command() : CommandExecutor {
    private val linkManager = LinkManager()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) return false
        val subCommand = args[0]
        try {
            when (subCommand) {
                McCommandConst.LINK_COMMAND -> linkCommand(sender)
                McCommandConst.UN_LINK_COMMAND -> unLink(sender)
                McCommandConst.SHOW_COMMAND -> show(sender, args)
                else -> {
                    val message = "${ChatColor.RED}コマンド構文が間違っています"
                    sender.sendMessage(message)
                }
            }
        } catch (error: Exception) {
            val message = "${ChatColor.RED}コマンドの実行にエラー発生しました"
            sender.sendMessage(message)
            Bukkit.getLogger().info("[エラー]${error.message}")
        }

        return true
    }

    private fun linkCommand(sender: CommandSender) {
        if (sender !is Player) {
            val message = "${ChatColor.RED}このコマンドはプレイヤーのみ実行可能です"
            sender.sendMessage(message)
            return
        }

        if (Data.jda == null) {
            val message = "${ChatColor.RED}現在DiscordBOTが設定されていません"
            sender.sendMessage(message)
            return
        }

        val mcUUID = sender.uniqueId.toString()

        val discordID = linkManager.acquisitionDiscordID(mcUUID)
        if (discordID == null) {
            val authKey = linkManager.creationAuthKey(sender)

            if (authKey != null) {
                val message = "${ChatColor.GOLD}[認証キー] $authKey\n" +
                    "${ChatColor.YELLOW}Discord鯖で「/link $authKey」を実行してください"
                sender.sendMessage(message)
            } else {
                val message = "${ChatColor.RED}認証キー生成に失敗しました しばらく時間を明けて実行してください"
                sender.sendMessage(message)
            }
        } else {
            val message = "${ChatColor.RED}「$discordID」と既に連携しています"
            sender.sendMessage(message)
        }
    }

    private fun unLink(sender: CommandSender) {
        if (sender !is Player) {
            val message = "${ChatColor.RED}このコマンドはプレイヤーのみ実行可能です"
            sender.sendMessage(message)
            return
        }
        val mcUUID = sender.uniqueId.toString()

        linkManager.unlink(mcUUID)

        val message = "${ChatColor.RED}連携を解除しました"
        sender.sendMessage(message)
    }

    private fun show(sender: CommandSender, args: Array<out String>) {
        if (sender !is Player) {
            val message = "${ChatColor.RED}このコマンドはプレイヤーのみ実行可能です"
            sender.sendMessage(message)
            return
        }

        var targets = mutableListOf<Player>()

        if (args.size < 2 || !sender.isOp) {
            targets.add(sender)
        } else {
            targets = acquisitionSelectPlayer(sender, args[1])
        }

        if (targets.isEmpty()) {
            val message = "${ChatColor.RED}対象のプレイヤーが見つかりませんでした"
            sender.sendMessage(message)
            return
        }

        for (target in targets) {
            val mcUUID = target.uniqueId.toString()
            val mcName = target.name
            val discordID = linkManager.acquisitionDiscordID(mcUUID)

            val message = if (discordID != null) {
                "${ChatColor.AQUA}${mcName}は「$discordID」と連携しています"
            } else {
                "${ChatColor.RED}${mcName}は連携していません"
            }
            sender.sendMessage(message)
        }
    }

    private fun acquisitionSelectPlayer(sender: Player, target: String): MutableList<Player> {
        return Bukkit.selectEntities(sender, target)
            .filterIsInstance<Player>()
            .toMutableList()
    }
}
