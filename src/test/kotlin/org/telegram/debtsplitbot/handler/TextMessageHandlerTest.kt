package org.telegram.debtsplitbot.handler

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder
import org.hamcrest.collection.IsIterableContainingInOrder.contains
import org.hamcrest.collection.IsMapContaining.hasKey
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
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
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import kotlin.random.Random

@ExtendWith(MockitoExtension::class)
open class TextMessageHandlerTest {

    @Mock
    lateinit var message: Message

    @Mock
    lateinit var bot: TelegramLongPollingBot

    lateinit var handler: TextMessageHandler

    val chatId = Random.nextLong()
    lateinit var acceptedMessage: SendMessage

    @BeforeEach
    fun setup() {
        given(message.chatId).willReturn(chatId)

        handler = TextMessageHandler(bot, message)
        acceptedMessage = SendMessage(chatId, "Accepted.")
    }

    @Test
    fun shouldCreateNewListPerChat() {
        handleCommand("$NEW_LIST USD John,Ann")
        handleCommand("$DEBT John 4")
        handleCommand(RESULT)
        verify(bot, times(2)).execute(acceptedMessage)
        verifyMessage(
            "USD:\n\n" +
                    "'Ann' owes 'John' 4.00"
        )


        val newChatId = Random.nextLong()
        given(message.chatId).willReturn(newChatId)
        handleCommand("$NEW_LIST EUR Helen,Kate")
        handleCommand("$DEBT Kate 2")
        handleCommand(RESULT)
        verify(bot, times(2)).execute(acceptedMessage)
        verifyMessage(
            "EUR:\n\n" +
                    "'Helen' owes 'Kate' 2.00"
        )

        assertThat(chatContexts.keys, containsInAnyOrder(chatId, newChatId))
    }

    @Test
    fun shouldCalculateDebtsInOneCurrency() {
        handleCommand("$NEW_LIST USD John,Ann,Peter")
        handleCommand("$DEBT Peter John,Ann 10.34")
        handleCommand("$DEBT Peter John 5.17")
        handleCommand("$DEBT Peter John 1.24")
        handleCommand("$DEBT John 4.32")
        handleCommand(RESULT)

        verify(bot, times(5)).execute(acceptedMessage)
        verifyMessage(
            "USD:\n\n" +
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

        verify(bot, times(7)).execute(acceptedMessage)

        verifyMessage(
            "EUR:\n\n" +
                    "'Peter' owes 'Ann' 11.00\n" +
                    "'Helen' owes 'John' 1.00\n" +
                    "'Helen' owes 'Ann' 6.00", 2
        )
        verifyMessage(
            "USD:\n\n" +
                    "'John' owes 'Peter' 5.00\n" +
                    "'Ann' owes 'Peter' 5.00\n" +
                    "'Helen' owes 'Peter' 5.00"
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