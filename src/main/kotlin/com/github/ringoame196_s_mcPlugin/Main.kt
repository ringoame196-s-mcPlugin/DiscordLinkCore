package com.github.ringoame196_s_mcPlugin

import com.github.ringoame196_s_mcPlugin.commands.Command
import com.github.ringoame196_s_mcPlugin.commands.TabCompleter
import com.github.ringoame196_s_mcPlugin.managers.DataBaseManager
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class Main : JavaPlugin() {
    override fun onEnable() {
        super.onEnable()
        val plugin = this
        makeDataBaseFile()
        val command = getCommand("discord")
        command!!.setExecutor(Command())
        command.tabCompleter = TabCompleter()
    }

    private fun makeDataBaseFile() {
        val plugin = this
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
}
