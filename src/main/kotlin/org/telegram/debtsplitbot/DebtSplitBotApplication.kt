package org.telegram.debtsplitbot

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling
import org.telegram.telegrambots.ApiContextInitializer

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories("org.telegram.debtsplitbot.repository")
class DebtSplitBotApplication {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            ApiContextInitializer.init()
            SpringApplication.run(DebtSplitBotApplication::class.java, *args)
        }
    }
}