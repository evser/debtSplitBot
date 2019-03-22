package org.telegram.debtsplitbot.handler.commands

import org.telegram.debtsplitbot.handler.ChatContext
import org.telegram.debtsplitbot.handler.TextMessageHandler.Companion.chatContexts


class NewListCommand : Command() {

    override fun execute(command: String): Boolean {
        val currency = "currency"
        val participants = "participants"
        return execute(command,
                "${Commands.NEW_LIST} (?<$currency>\\p{javaLetter}+) (?<$participants>\\p{javaLetter}+(,\\p{javaLetter}+)+)",
                "${Commands.NEW_LIST} [currency] [names] | ${Commands.NEW_LIST} USD John,Peter,Ann") { groups ->
            chatContexts[handler.message.chatId] = ChatContext(groups[currency]!!.value, groups[participants]!!.value.split(",").toSet())
        }
    }


}