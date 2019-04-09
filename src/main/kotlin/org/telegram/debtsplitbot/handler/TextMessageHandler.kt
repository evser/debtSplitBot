package org.telegram.debtsplitbot.handler

import org.telegram.debtsplitbot.handler.commands.Commands
import org.telegram.debtsplitbot.repository.entity.UserCommand
import org.telegram.debtsplitbot.service.UserCommandService
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Message
import java.time.LocalDateTime

class TextMessageHandler(bot: TelegramLongPollingBot, message: Message, val commandService: UserCommandService) : MessageHandler(bot, message) {

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
            executeCommand(command)
        }
    }

    fun executeCommand(command: String) {
        Commands.values.entries.stream()
                .filter { command.startsWith(it.key) }
                .findFirst()
                .ifPresent {
                    try {
                        if (it.value(this).execute(command)) {
                            sendMessage("Accepted.")
                        }
                        if (Commands.isRecordable(it.key)) {
                            commandService.save(UserCommand(message.chatId, command, LocalDateTime.now()))
                        }
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

    companion object {
        val chatContexts: MutableMap<Long, ChatContext> = mutableMapOf()
    }
}
