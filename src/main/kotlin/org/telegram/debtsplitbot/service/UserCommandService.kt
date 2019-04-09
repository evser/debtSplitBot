package org.telegram.debtsplitbot.service

import org.telegram.debtsplitbot.repository.entity.UserCommand

interface UserCommandService {

    fun findByChatId(chatId: Long): List<UserCommand>
    fun save(command: UserCommand)
    fun deleteByChatId(chatId: Long)
}