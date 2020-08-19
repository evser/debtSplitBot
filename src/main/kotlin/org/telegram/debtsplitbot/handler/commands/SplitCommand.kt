package org.telegram.debtsplitbot.handler.commands

import org.telegram.debtsplitbot.handler.TextMessageHandler


class SplitCommand(handler: TextMessageHandler) : Command(handler) {

    override fun execute(command: String): Boolean {
        val lender = "lender"
        val amount = "amount"
        val showResult = executeInContext(
            command,
            "${Commands.SPLIT} (?<$lender>\\p{javaLetter}+) (?<$amount>\\d+\\.?\\d{0,2})",
            "${Commands.SPLIT} [lender] [amount] | ${Commands.SPLIT} John 100"
        ) { groups, chatContext ->

            chatContext.getCurrentDebts().lend(
                groups[lender]!!.value,
                groups[amount]!!.value.toBigDecimal() / TextMessageHandler.chatContexts[handler.message.chatId]!!.getParticipantsCount().toBigDecimal()
            )
            chatContext.incrementDebtCounter()
        }
        if (showResult) {
            val resultCommand = ResultCommand(handler)
            resultCommand.execute(Commands.RESULT + " ${TextMessageHandler.chatContexts[handler.message.chatId]!!.currentCurrency}")
        }

        return false
    }


}