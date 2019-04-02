package org.telegram.debtsplitbot.handler.commands

import org.telegram.debtsplitbot.handler.TextMessageHandler


class SetCurrencyCommand(handler: TextMessageHandler) : Command(handler)  {

    override fun execute(command: String): Boolean {
        val currency = "currency"
        return executeInContext(command,
                "${Commands.SET_CURRENCY} (?<$currency>\\p{javaLetter}+)",
                "${Commands.SET_CURRENCY} [currency] | ${Commands.SET_CURRENCY} EUR") { groups, chatContext ->
            chatContext.setCurrency(groups[currency]!!.value)
        }
    }
}