package org.telegram.debtsplitbot.handler

import org.apache.commons.collections4.CollectionUtils.union
import org.telegram.debtsplitbot.core.DebtEdge
import org.telegram.debtsplitbot.core.DebtGraph
import java.util.stream.Collectors.toList
import kotlin.streams.toList

class ChatContext(currencyParam: String, participants: Set<String>) {

    private val debtsInCurrency: MutableMap<String, DebtGraph>
    private var currentCurrency: String

    init {
        val currency = currencyParam.toUpperCase()
        this.debtsInCurrency = linkedMapOf(Pair(currency, DebtGraph(participants)))
        this.currentCurrency = currency
    }

    fun getCurrentDebts(): DebtGraph {
        return debtsInCurrency[currentCurrency]!!
    }

    fun addParticipant(participant: String) {
        debtsInCurrency.forEach { _, debts -> debts.addParticipant(participant) }
    }

    fun setCurrency(currencyParam: String) {
        val currency = currencyParam.toUpperCase()
        currentCurrency = currency
        debtsInCurrency.putIfAbsent(currency, DebtGraph(debtsInCurrency.values.first()))
    }

    fun getResults(currencyParam: String?, rates: Map<String, Double>): Map<String, Set<DebtEdge>> {
        val currency = currencyParam?.toUpperCase()
                ?: return debtsInCurrency.entries
                        .associate { it.key to it.value.normalize() }

        if (debtsInCurrency.contains(currency) && rates.isEmpty()) {
            return mapOf(currentCurrency to debtsInCurrency[currency]!!.normalize())
        }

        val notSpecifiedKeys = LinkedHashSet(debtsInCurrency.keys)
        notSpecifiedKeys.removeAll(union(setOf(currency), rates.keys));
        if (notSpecifiedKeys.isNotEmpty()) {
            throw IllegalArgumentException("Please specify currencies and rates from a list: $notSpecifiedKeys")
        }

        val debtsInTargetCurrency = debtsInCurrency.entries.stream()
                .map { (debtCurrency, debt) ->
                    if (debtCurrency == currency) {
                        debt.normalize()
                    } else {
                        debt.normalize().stream()
                                .peek { edge -> edge.debt *= rates.getValue(debtCurrency) }
                                .toList()
                    }
                }
                .flatMap { it.stream() }
                .collect(toList())

        val targetDebts = DebtGraph(getCurrentDebts())
        debtsInTargetCurrency.forEach { edge -> targetDebts.lend(edge.lender, edge.debtor, edge.debt) }

        return mapOf(currency to targetDebts.normalize())
    }

}