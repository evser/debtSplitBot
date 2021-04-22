package org.telegram.debtsplitbot.handler.commands

import org.telegram.debtsplitbot.handler.TextMessageHandler


class RevertCommand(handler: TextMessageHandler) : Command(handler) {

    override fun isPersistent(): Boolean {
        return true
    }

    override fun execute(command: String): Boolean {
        executeInContext(
            command,
            Commands.REVERT,
            Commands.REVERT
        ) { _, chatContext ->
            val lastCommand = chatContext.commands.last()

            handler.isRevertMode = true;
            handler.executeCommand(lastCommand)
            handler.isRevertMode = false;
        }
        return false
    }

}