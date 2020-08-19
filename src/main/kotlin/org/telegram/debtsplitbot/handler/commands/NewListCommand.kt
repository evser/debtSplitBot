package org.telegram.debtsplitbot.handler.commands

import org.telegram.debtsplitbot.handler.ChatContext
import org.telegram.debtsplitbot.handler.TextMessageHandler
import org.telegram.debtsplitbot.handler.TextMessageHandler.Companion.chatContexts


class NewListCommand(handler: TextMessageHandler) : Command(handler) {

    override fun execute(command: String): Boolean {
        val currency = "currency"
        val participants = "participants"
        return execute(
            command,
            "${Commands.NEW_LIST} (?<$currency>\\p{javaLetter}+) (?<$participants>\\p{javaLetter}+(,\\p{javaLetter}+)+)",
            "${Commands.NEW_LIST} [currency] [names] | ${Commands.NEW_LIST} USD John,Peter,Ann"
        ) { groups ->
            val chatId = handler.message.chatId
            handler.commandService.deleteByChatId(chatId)
            chatContexts[chatId] = ChatContext(
                groups[currency]!!.value,
                groups[participants]!!.value.split(",").toSet(),
                handler.message.chat.title
            )
        }
    }


}