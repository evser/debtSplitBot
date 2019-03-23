package org.telegram.debtsplitbot.handler.commands


class ResultCommand : Command() {

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
                        split[0] to split[1].toDouble()
                    }.orEmpty()

            val debtResults = chatContext.getResults(currency, ratesSet)
            debtResults.forEach { debtCurrency, debts ->
                if (debts.isNotEmpty()) {
                    handler.sendMessage("$debtCurrency:\n\n${debts.joinToString("\n")}")
                } else {
                    handler.sendMessage("No debt found in '$debtCurrency'")
                }
            }
        }
        return false
    }
}