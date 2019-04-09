package org.telegram.debtsplitbot.handler.commands

import org.telegram.debtsplitbot.handler.ChatContext
import org.telegram.debtsplitbot.handler.TextMessageHandler
import org.telegram.debtsplitbot.handler.TextMessageHandler.Companion.chatContexts

abstract class Command(val handler: TextMessageHandler) {

    /**
     * @return successful or not
     */
    abstract fun execute(command: String): Boolean

    protected fun executeInContext(command: String, regexStr: String, commandFormat: String, consumer: (groups: MatchGroupCollection, chatContext: ChatContext) -> Unit): Boolean {
        val chatId = handler.message.chatId
        if (!chatContexts.containsKey(chatId) && !handler.isMuted()) {
            try {
                handler.mute()
                handler.commandService.findByChatId(chatId).stream()
                        .sorted(compareBy { it.timestamp })
                        .forEach { handler.executeCommand(it.command) }
            } finally {
                handler.unmute()
            }
        }

        val chatContext = chatContexts[chatId]
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

    private fun sendFormatError(error: String) {
        handler.sendMessage("Please use the right command syntax:\r\n$error")
    }
}