package org.telegram.debtsplitbot.handler.commands

import org.telegram.debtsplitbot.handler.TextMessageHandler


class ResultCommand(handler: TextMessageHandler) : Command(handler) {

    override fun execute(command: String): Boolean {
        val rates = "rates"
        val targetCurrency = "targetCurrency"
        executeInContext(command,
                "${Commands.RESULT}( (?<$targetCurrency>\\p{javaLetter}+) ?(?<$rates>(,?(\\p{javaLetter}+):(\\d+\\.?\\d*))*))?",
                "${Commands.RESULT} [target currency*] [rates*] | ${Commands.RESULT} USD EUR:0.86,GBP:0.75") { groups, chatContext ->

            val currency = groups["targetCurrency"]?.value
            val ratesSet = command.split(" ").getOrNull(2)
                    ?.split(",")
                    ?.associate { rateInfo ->
                        val split = rateInfo.split(":")
                        split[0] to split[1].toBigDecimal()
                    }.orEmpty()

            val debtResults = chatContext.getResults(currency, ratesSet)
            debtResults.forEach { debtCurrency, debts ->
                if (debts.isNotEmpty()) {
                    val count = if (ratesSet.isNotEmpty()) "" else " (${chatContext.getDebtCounter(debtCurrency)} transactions)"
                    handler.sendMessage("$debtCurrency$count:\n\n${debts.joinToString("\n")}")
                } else {
                    handler.sendMessage("No debt found in '$debtCurrency'")
                }
            }
        }
        return false
    }
}