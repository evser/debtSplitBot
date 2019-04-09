package org.telegram.debtsplitbot.repository

import org.springframework.data.repository.CrudRepository
import org.telegram.debtsplitbot.repository.entity.UserCommand

interface UserCommandRepository : CrudRepository<UserCommand, Long> {

    fun findByChatId(chatId: Long): List<UserCommand>
    fun deleteByChatId(chatId: Long)
}