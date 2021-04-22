package org.telegram.debtsplitbot.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.telegram.debtsplitbot.repository.UserCommandRepository
import org.telegram.debtsplitbot.repository.entity.UserCommand

@Service
class UserCommandServiceImpl(private val repository: UserCommandRepository) : UserCommandService {
    @Transactional(readOnly = true)
    override fun findByChatId(chatId: Long): List<UserCommand> {
        return repository.findByChatId(chatId)
    }

    @Transactional
    override fun save(command: UserCommand) {
        repository.save(command)
    }

    @Transactional
    override fun deleteByChatId(chatId: Long) {
        repository.deleteByChatId(chatId)
    }

}
