package org.telegram.debtsplitbot.handler

import org.telegram.debtsplitbot.handler.commands.Commands
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.bots.TelegramLongPollingBot

class TextMessageHandler(bot: TelegramLongPollingBot, message: Message) : MessageHandler(bot, message) {

    override fun handle() {
        val command = (message.text ?: message.sticker.emoji).replace("@${bot.botUsername}", "").trim()
        if (message.chat?.title == null) {
            sendMessage("This bot works only in group chats.")
        } else {
            /////////////  debug area
            val prevChatId = chatIds.putIfAbsent(message.chat.title, message.chatId)
            if (prevChatId != null && message.chatId != prevChatId) {
                sendMessage("Chat id has changed!!!")
                return
            }
            if (command == "/logs") {
                sendMessage("Chat contexts\n$chatContexts")
                sendMessage("Chat ids\n$chatIds")
                return
            }
////////////////////// <end of>  debug area

            Commands.values.entries.stream()
                    .filter { command.startsWith(it.key) }
                    .findFirst()
                    .map { it.value }
                    .ifPresent {
                        try {
                            if (it(this).execute(command)) {
                                sendMessage("Accepted.")
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
    }


    companion object {
        val chatContexts: MutableMap<String, ChatContext> = mutableMapOf()
        /// debug area
        val chatIds: MutableMap<String, Long> = mutableMapOf()
        /// <end of>  debug area

        fun shouldBeUsed(message: Message): Boolean {
            return message.hasText()
        }
    }
}
