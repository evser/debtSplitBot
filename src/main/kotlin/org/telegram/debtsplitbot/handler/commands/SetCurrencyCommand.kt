package org.telegram.debtsplitbot.handler.commands


class SetCurrencyCommand : Command() {

    override fun execute(command: String): Boolean {
        val currency = "currency"
        return executeInContext(command,
                "${Commands.SET_CURRENCY} (?<$currency>\\p{javaLetter}+)",
                "${Commands.SET_CURRENCY} [currency] | ${Commands.SET_CURRENCY} EUR") { groups, chatContext ->
            chatContext.setCurrency(groups[currency]!!.value)
        }
    }
}