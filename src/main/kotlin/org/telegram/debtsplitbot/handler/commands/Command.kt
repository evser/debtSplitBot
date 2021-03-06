package org.telegram.debtsplitbot.handler.commands

import org.telegram.debtsplitbot.handler.ChatContext
import org.telegram.debtsplitbot.handler.TextMessageHandler
import org.telegram.debtsplitbot.handler.TextMessageHandler.Companion.chatContexts


abstract class Command(val handler: TextMessageHandler) {

    /**
     * @return true if "Accepted." message should be written
     */
    abstract fun execute(command: String): Boolean

    open fun isRevertible(): Boolean = false
    open fun isPersistent(): Boolean = true

    protected fun executeInContext(command: String, regexStr: String, commandFormat: String, consumer: (groups: MatchGroupCollection, chatContext: ChatContext) -> Unit): Boolean {
        val chatId = handler.message.chatId
        if (!chatContexts.containsKey(chatId) && !handler.isRecoveringFromRepository()) {
            try {
                handler.startRecoveryFromRepository()
                handler.commandService.findByChatId(chatId).stream()
                    .sorted(compareBy { it.timestamp })
                    .forEach { handler.executeCommand(it.command) }

            } finally {
                handler.finishRecoveryFromRepository()
            }
        }

        val chatContext = chatContexts[chatId]
        return if (chatContext != null) {
            val result = execute(command, regexStr, commandFormat) { consumer.invoke(it, chatContext) }
            if (isPersistent() && !handler.isRevertMode) {
                chatContext.addCommand(command)
            }
            result
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