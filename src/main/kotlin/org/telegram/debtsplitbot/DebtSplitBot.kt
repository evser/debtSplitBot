package org.telegram.debtsplitbot

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.telegram.debtsplitbot.handler.TextMessageHandler
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.TelegramBotsApi
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.exceptions.TelegramApiException

@SpringBootApplication
@EnableScheduling
class DebtSplitBot : TelegramLongPollingBot() {

    override fun getBotUsername(): String {
        return "DebtSplitBot"
    }

    override fun onUpdateReceived(e: Update) {
        val message = e.message ?: return

        val from = message.from
        if (from.bot!!) {
            return
        }

        val messageHandler =
                when {
                    TextMessageHandler.shouldBeUsed(message) -> TextMessageHandler(this, message)
                    else -> null
                }
        messageHandler?.handle()
    }

    override fun getBotToken(): String {
        return System.getenv("BOT_KEY")
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            ApiContextInitializer.init()
            try {
                TelegramBotsApi().registerBot(DebtSplitBot())
            } catch (ex: TelegramApiException) {
                ex.printStackTrace()
            }

            SpringApplication.run(DebtSplitBot::class.java, *args)
        }


    }

}
