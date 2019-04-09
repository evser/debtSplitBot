package org.telegram.debtsplitbot.repository.entity

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class UserCommand(var chatId: Long, var command: String, var timestamp: LocalDateTime) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val encounterId: Long = 0
}
