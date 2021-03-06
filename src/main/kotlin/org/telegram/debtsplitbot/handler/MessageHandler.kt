package org.telegram.debtsplitbot.handler

import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.util.concurrent.atomic.AtomicBoolean

abstract class MessageHandler(val bot: TelegramLongPollingBot, val message: Message) {

    abstract fun handle()

    var muted: AtomicBoolean = AtomicBoolean()

    fun sendMessage(text: String) {
        if (isRecoveringFromRepository()) {
            return
        }

        val sendMessage = SendMessage(message.chatId, text)
        try {
            bot.execute(sendMessage)
        } catch (ex: TelegramApiException) {
            ex.printStackTrace()
        }
    }

    fun isRecoveringFromRepository(): Boolean {
        return muted.get()
    }

    fun startRecoveryFromRepository() {
        muted.set(true)
    }

    fun finishRecoveryFromRepository() {
        muted.set(false)
    }

}
