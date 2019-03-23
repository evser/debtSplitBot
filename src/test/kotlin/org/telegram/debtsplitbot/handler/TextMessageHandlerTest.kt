package org.telegram.debtsplitbot.handler

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.telegram.debtsplitbot.handler.commands.Commands
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.bots.TelegramLongPollingBot

@ExtendWith(MockitoExtension::class)
open class TextMessageHandlerTest {

    @Mock
    lateinit var message: Message

    @Mock
    lateinit var bot: TelegramLongPollingBot

    lateinit var handler: TextMessageHandler

    val messageId: Int = 1
    val chatId: Long = 2

    @BeforeEach
    fun setup() {
        handler = TextMessageHandler(bot, message)
//        given(message.messageId).willReturn(messageId)
        given(message.chatId).willReturn(chatId)
    }


    @Test
    fun shouldCreateNewList() {
        given(message.text).willReturn("${Commands.NEW_LIST} USD John,Ann")

        handler.handle()

        val sendMessage = SendMessage(message.chatId, "Accepted.")
        verify(bot).execute(sendMessage)

        assertTrue(TextMessageHandler.chatContexts.containsKey(chatId))
    }
}