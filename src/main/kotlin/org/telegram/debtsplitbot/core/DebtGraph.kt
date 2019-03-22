package org.telegram.debtsplitbot.core

import org.apache.commons.collections4.CollectionUtils.union
import kotlin.math.abs
import kotlin.math.min

class DebtGraph(participants: Set<String>) {

    private val participants: MutableSet<String>
    private val debts: MutableList<MutableList<Double>>

    constructor(debtGraph: DebtGraph) : this(debtGraph.participants)

    init {
        this.participants = LinkedHashSet(participants)
        debts = MutableList(participants.size) { MutableList(participants.size) { 0.0 } }
    }

    fun addParticipant(participant: String) {
        debts.forEach { list -> list.add(0.0) }
        debts.add(MutableList(debts.size + 1) { 0.0 })
        participants.add(participant)
    }

    fun lend(lender: String, amount: Double) {
        if (amount <= 0) {
            throw IllegalAccessException("Debt should be greater than 0")
        }
        val lenderIndex = participants.indexOf(lender)
        if (lenderIndex == -1) {
            throw IllegalArgumentException("Lender is not in the list.");
        }
        for ((index) in debts[lenderIndex].withIndex()) {
            if (index != lenderIndex) {
                debts[lenderIndex][index] += amount
            }
        }
    }

    fun lend(lender: String, debtor: String, amount: Double) {
        lend(lender, setOf(debtor), amount)
    }

    fun lend(lender: String, debtors: Set<String>, amount: Double) {
        if (amount <= 0) {
            throw IllegalAccessException("Debt should be greater than 0")
        }
        if (!participants.containsAll(union(setOf(lender), debtors))) {
            throw IllegalArgumentException("A person is not registered.");
        }
        if (debtors.contains(lender)) {
            throw java.lang.IllegalArgumentException("Debtors should not contain a lender");
        }

        val lenderIndex = participants.indexOf(lender)
        for (debtor in debtors) {
            debts[lenderIndex][participants.indexOf(debtor)] += amount
        }
    }

    fun normalize(): Set<DebtEdge> {
        val personalBalances = MutableList(debts.size) { 0.0 } // shows balance adjustments in the end (>0 lender)
        val edges = sortedSetOf(
                compareBy<DebtEdge> { participants.indexOf(it.debtor) }
                        .thenBy { participants.indexOf(it.lender) }
                        .thenBy { it.debt })

        debts.forEachIndexed { lenderIndex, debtors ->
            debtors.forEachIndexed { debtorIndex, debt ->
                if (lenderIndex > debtorIndex) {
                    debts[debtorIndex][lenderIndex] -= debts[lenderIndex][debtorIndex]
                    debts[lenderIndex][debtorIndex] = 0.0
                }
                personalBalances[lenderIndex] += debt
                personalBalances[debtorIndex] -= debt
            }
        }

        while (!personalBalances.all { debt -> debt == 0.0 }) {
            val max = personalBalances.withIndex().maxBy { it.value }!!
            val min = personalBalances.withIndex().minBy { it.value }!!
            val minAbs = min(max.value, abs(min.value))

            edges += DebtEdge(participants.elementAt(min.index), participants.elementAt(max.index), minAbs)

            personalBalances[max.index] = max.value - minAbs
            personalBalances[min.index] = min.value + minAbs
        }
        return edges
    }
}