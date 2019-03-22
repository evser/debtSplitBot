package org.telegram.debtsplitbot.handler

import org.telegram.debtsplitbot.handler.commands.Commands
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import java.util.concurrent.ConcurrentHashMap

class TextMessageHandler(bot: TelegramLongPollingBot, message: Message) : MessageHandler(bot, message) {

    override fun handle() {
        val command = (message.text ?: message.sticker.emoji).replace("@${bot.botUsername}", "").trim()

        Commands.values.entries.stream()
                .filter { command.startsWith(it.key) }
                .findFirst()
                .map { it.value }
                .ifPresent {
                    it.init(this)
                    try {
                        if (it.execute(command)) {
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


    companion object {

        val chatContexts: MutableMap<Long, ChatContext> = ConcurrentHashMap()

        fun shouldBeUsed(message: Message): Boolean {
            return message.hasText()
        }
    }
}
