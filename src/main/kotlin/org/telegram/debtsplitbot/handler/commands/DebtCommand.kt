package org.telegram.debtsplitbot.handler.commands


class DebtCommand : Command() {

    override fun execute(command: String): Boolean {
        val lender = "lender"
        val debtors = "debtors"
        val amount = "amount"
        return executeInContext(command,
            "${Commands.DEBT} (?<$lender>\\p{javaLetter}+)( (?<$debtors>\\p{javaLetter}+(,\\p{javaLetter}+)*))? (?<$amount>\\d+\\.?\\d{0,2})",
            "${Commands.DEBT} [lender] [debtors*] [amount] | ${Commands.DEBT} John 21.33 | ${Commands.DEBT} John Peter,Ann 10.52"
        ) { groups, chatContext ->
            if (groups[debtors] == null) {
                chatContext.getCurrentDebts().lend(groups[lender]!!.value, groups[amount]!!.value.toBigDecimal())
            } else {
                chatContext.getCurrentDebts().lend(
                    groups[lender]!!.value,
                    groups[debtors]!!.value.split(",").toSet(),
                    groups[amount]!!.value.toBigDecimal()
                )
            }
        }
    }


}