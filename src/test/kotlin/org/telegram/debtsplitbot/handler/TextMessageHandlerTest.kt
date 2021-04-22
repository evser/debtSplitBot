package org.telegram.debtsplitbot.handler

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.collection.IsIterableContainingInOrder.contains
import org.hamcrest.collection.IsMapContaining.hasKey
import org.hamcrest.core.IsCollectionContaining.hasItems
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Answers
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.telegram.debtsplitbot.core.DebtEdge
import org.telegram.debtsplitbot.handler.TextMessageHandler.Companion.chatContexts
import org.telegram.debtsplitbot.handler.commands.Commands.ADD_PARTICIPANT
import org.telegram.debtsplitbot.handler.commands.Commands.DEBT
import org.telegram.debtsplitbot.handler.commands.Commands.NEW_LIST
import org.telegram.debtsplitbot.handler.commands.Commands.RESULT
import org.telegram.debtsplitbot.handler.commands.Commands.SET_CURRENCY
import org.telegram.debtsplitbot.handler.commands.Commands.SPLIT
import org.telegram.debtsplitbot.service.UserCommandService
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import kotlin.random.Random

@ExtendWith(MockitoExtension::class)
open class TextMessageHandlerTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var message: Message

    @Mock
    lateinit var bot: TelegramLongPollingBot

    @Mock
    lateinit var service: UserCommandService

    lateinit var handler: TextMessageHandler

    val chatId = Random.nextLong()
    lateinit var acceptedMessage: SendMessage

    @BeforeEach
    fun setup() {
        given(message.chatId).willReturn(chatId)
        given(message.chat.title).willReturn("title")
        given(message.hasText()).willReturn(true)

        handler = TextMessageHandler(bot, message, service)
        acceptedMessage = SendMessage(chatId, "Accepted.")
    }

    @Test
    fun shouldCreateNewListPerChat() {
        handleCommand("$NEW_LIST USD John,Ann")
        handleCommand("$DEBT John 4")
        verifyMessage(
                "USD (1 transactions):\n\n" +
                    "'Ann' owes 'John' 4.00"
        )
        verify(bot).execute(acceptedMessage)

        val newChatId = Random.nextLong()
        given(message.chatId).willReturn(newChatId)
        handleCommand("$NEW_LIST EUR Helen,Kate")
        handleCommand("$DEBT Kate 2")
        verify(bot).execute(acceptedMessage)
        verifyMessage(
                "EUR (1 transactions):\n\n" +
                    "'Helen' owes 'Kate' 2.00"
        )

        assertThat(chatContexts.keys, hasItems(chatId, newChatId))
    }

    @Test
    fun shouldCalculateDebtsInOneCurrency() {
        handleCommand("$NEW_LIST USD John,Ann,Peter")
        handleCommand("$DEBT Peter John,Ann 10.34")
        handleCommand("$DEBT Peter John 5.17")
        handleCommand("$DEBT Peter John 1.24")
        handleCommand("$SPLIT John 12.96")

        verify(bot).execute(acceptedMessage)
        verifyMessage(
                "USD (4 transactions):\n\n" +
                        "'John' owes 'Peter' 8.11\n" +
                        "'Ann' owes 'Peter' 14.66"

        )

        assertThat(chatContexts, hasKey(chatId))
        assertThat(
                chatContexts[chatId]!!.getCurrentDebts().normalize(),
                contains(
                        DebtEdge("John", "Peter", 8.11.toBigDecimal()),
                        DebtEdge("Ann", "Peter", 14.66.toBigDecimal())
                )
        )
    }


    @Test
    fun shouldCalculateDebtsInDifferentCurrencies() {
        handleCommand("$NEW_LIST EUR John,Ann,Peter")
        handleCommand("$SET_CURRENCY eur")
        handleCommand("$DEBT John 4")
        handleCommand("$ADD_PARTICIPANT Helen")
        handleCommand("$DEBT Ann 7")
        handleCommand("$SET_CURRENCY usd")
        handleCommand("$DEBT Peter 5")
        handleCommand(RESULT)
        handleCommand("$RESULT EUR")
        handleCommand("$RESULT EUR USD:2")

        verify(bot, times(4)).execute(acceptedMessage)

        verifyMessage(
            "EUR (2 transactions):\n\n" +
                    "'Peter' owes 'Ann' 11.00\n" +
                    "'Helen' owes 'John' 1.00\n" +
                    "'Helen' owes 'Ann' 6.00", 3 // the last debt + x2 results
        )
        verifyMessage(
                "USD (1 transactions):\n\n" +
                        "'John' owes 'Peter' 5.00\n" +
                        "'Ann' owes 'Peter' 5.00\n" +
                        "'Helen' owes 'Peter' 5.00", 2
        )

        verifyMessage(
                "EUR:\n\n" +
                        "'John' owes 'Ann' 7.00\n" +
                        "'John' owes 'Peter' 2.00\n" +
                        "'Helen' owes 'Peter' 17.00"
        )
    }

    private fun verifyMessage(text: String, times: Int = 1) {
        verify(bot, times(times)).execute(
                SendMessage(message.chatId, text)
        )
    }

    private fun handleCommand(command: String) {
        given(message.text).willReturn(command)

        handler.handle()
    }
}