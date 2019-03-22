package org.telegram.debtsplitbot.handler.commands


class StartCommand : Command() {

    override fun execute(command: String): Boolean {
        handler.sendMessage("This bot !")
        return false
    }


}