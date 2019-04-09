package org.telegram.debtsplitbot

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.telegram.debtsplitbot.handler.TextMessageHandler
import org.telegram.debtsplitbot.service.UserCommandService
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import javax.annotation.PostConstruct

@Component
class DebtSplitBot(private val commandService: UserCommandService) : TelegramLongPollingBot() {

    @Autowired
    private lateinit var botBean: DebtSplitBot

    @PostConstruct
    fun init() {
        try {
            TelegramBotsApi().registerBot(botBean)
        } catch (ex: TelegramApiException) {
            ex.printStackTrace()
        }
    }

    override fun getBotUsername(): String {
        return "DebtSplitBot"
    }

    override fun onUpdateReceived(e: Update) {
        val message = e.message ?: return
        val from = message.from
        if (from.bot!!) {
            return
        }

        TextMessageHandler(this, message, commandService).handle()
    }

    override fun getBotToken(): String {
        return System.getenv("BOT_KEY")
    }

}
