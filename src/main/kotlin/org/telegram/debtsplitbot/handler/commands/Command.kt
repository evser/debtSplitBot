package org.telegram.debtsplitbot.handler.commands

import org.telegram.debtsplitbot.handler.ChatContext
import org.telegram.debtsplitbot.handler.TextMessageHandler

abstract class Command {

    protected lateinit var handler: TextMessageHandler

    fun init(handler: TextMessageHandler) {
        this.handler = handler
    }

    /**
     * @return successful or not
     */
    abstract fun execute(command: String): Boolean

    protected fun executeInContext(command: String, regexStr: String, commandFormat: String, consumer: (groups: MatchGroupCollection, chatContext: ChatContext) -> Unit): Boolean {
        val chatContext = TextMessageHandler.chatContexts[handler.message.chatId]
        return if (chatContext != null) {
            execute(command, regexStr, commandFormat) { consumer.invoke(it, chatContext) }
        } else {
            handler.sendMessage("Please create list using ${Commands.NEW_LIST} first.")
            false
        }
    }

    protected fun execute(command: String, regexStr: String, commandFormat: String, consumer: (groups: MatchGroupCollection) -> Unit): Boolean {
        val regex = regexStr.toRegex()
        return if (command.matches(regex)) {
            consumer.invoke(regex.matchEntire(command)!!.groups)
            true
        } else {
            sendFormatError(commandFormat)
            false
        }
    }

    protected fun sendFormatError(error: String) {
        handler.sendMessage("Please use the right command syntax:\r\n$error")
    }
}