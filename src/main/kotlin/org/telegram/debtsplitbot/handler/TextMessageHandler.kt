package org.telegram.debtsplitbot.handler

import org.telegram.debtsplitbot.handler.commands.Commands
import org.telegram.debtsplitbot.repository.entity.UserCommand
import org.telegram.debtsplitbot.service.UserCommandService
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Message
import java.time.LocalDateTime

class TextMessageHandler(bot: TelegramLongPollingBot, message: Message, val commandService: UserCommandService) : MessageHandler(bot, message) {

    var isRevertMode: Boolean = false

    override fun handle() {
        if (!message.hasText()) {
            return
        }

        val command = message.text.replace("@${bot.botUsername}", "").trim()
        if (command.contains("@")) {
            return
        }

        if (message.chat?.title == null) {
            sendMessage("This bot works only in group chats.")
        } else {
            try {
                executeCommand(command)
            } catch (ex: IllegalArgumentException) {
                sendMessage(ex.message ?: "Some error occurred.")
                if (ex.message == null) {
                    ex.printStackTrace()
                }
            } catch (ex: Throwable) {
                sendMessage("Some error occurred.")
                ex.printStackTrace()
            }
        }
    }

    fun executeCommand(commandStr: String) {
        Commands.values.entries.stream()
            .filter { commandStr.startsWith(it.key) }
            .findFirst()
            .ifPresent {
                val command = it.value(this)
                if (isRevertMode && !command.isRevertible()) {
                    throw IllegalArgumentException("This command is not revertible: {$commandStr}")
                }

                if (command.execute(commandStr)) {
                    sendMessage("Accepted.")
                }

                if (command.isPersistent() && !isRecoveringFromRepository() && !isRevertMode) {
                    commandService.save(UserCommand(message.chatId, commandStr, LocalDateTime.now()))
                }
            }
    }

    companion object {
        val chatContexts: MutableMap<Long, ChatContext> = mutableMapOf()
    }
}
