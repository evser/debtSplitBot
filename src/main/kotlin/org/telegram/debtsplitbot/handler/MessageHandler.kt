package org.telegram.debtsplitbot.handler

import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.exceptions.TelegramApiException

abstract class MessageHandler(val bot: TelegramLongPollingBot, val message: Message) {

    abstract fun handle()

    fun sendMessage(text: String): Message? {
        val sendMessage = SendMessage(message.chatId, text)
        try {
            return bot.execute(sendMessage)
        } catch (ex: TelegramApiException) {
            ex.printStackTrace()
        }
        return Message()
    }

}
