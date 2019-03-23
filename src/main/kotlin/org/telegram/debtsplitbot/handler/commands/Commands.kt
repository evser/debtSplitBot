package org.telegram.debtsplitbot.handler.commands

object Commands {

    const val START = "/start"

    const val NEW_LIST = "/newlist"

    const val DEBT = "/debt"

    const val ADD_PARTICIPANT = "/addparticipant"

    const val SET_CURRENCY = "/setcurrency"

    const val RESULT = "/result"

    val values: Map<String, Command> = mapOf(
            START to StartCommand(),
            NEW_LIST to NewListCommand(),
            DEBT to DebtCommand(),
            ADD_PARTICIPANT to AddParticipantCommand(),
            SET_CURRENCY to SetCurrencyCommand(),
            RESULT to ResultCommand()
    )


}
