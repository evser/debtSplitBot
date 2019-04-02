package org.telegram.debtsplitbot.handler.commands

import org.telegram.debtsplitbot.handler.TextMessageHandler


class AddParticipantCommand (handler: TextMessageHandler) : Command(handler)  {

    override fun execute(command: String): Boolean {
        val participant = "participant"
        return executeInContext(command,
                "${Commands.ADD_PARTICIPANT} (?<$participant>\\p{javaLetter}+)",
                "${Commands.ADD_PARTICIPANT} [participant] | ${Commands.ADD_PARTICIPANT} Robert") { groups, chatContext -> chatContext.addParticipant(groups[participant]!!.value) }
    }


}