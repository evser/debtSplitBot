package org.telegram.debtsplitbot.handler.commands

import org.telegram.debtsplitbot.handler.TextMessageHandler

object Commands {

    const val START = "/start"

    const val NEW_LIST = "/newlist"

    const val DEBT = "/debt"

    const val SPLIT = "/split"

    const val ADD_PARTICIPANT = "/addparticipant"

    const val SET_CURRENCY = "/setcurrency"

    const val RESULT = "/result"

    private val PERSISTABLE_COMMANDS = listOf(NEW_LIST, DEBT, SPLIT, ADD_PARTICIPANT, SET_CURRENCY)

    val values: Map<String, (TextMessageHandler) -> Command> = mapOf(
        START to { handler: TextMessageHandler -> StartCommand(handler) },
        NEW_LIST to { handler: TextMessageHandler -> NewListCommand(handler) },
        DEBT to { handler: TextMessageHandler -> DebtCommand(handler) },
        SPLIT to { handler: TextMessageHandler -> SplitCommand(handler) },
        ADD_PARTICIPANT to { handler: TextMessageHandler -> AddParticipantCommand(handler) },
        SET_CURRENCY to { handler: TextMessageHandler -> SetCurrencyCommand(handler) },
        RESULT to { handler: TextMessageHandler -> ResultCommand(handler) }
    )

    /**
     * @return true if should be logged to DB
     */
    fun isPersistable(command: String): Boolean {
        return PERSISTABLE_COMMANDS.contains(command)
    }

}
