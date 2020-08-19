package org.telegram.debtsplitbot.core

import org.apache.commons.collections4.CollectionUtils.union
import java.math.BigDecimal
import java.math.BigDecimal.ZERO

class DebtGraph(participants: Set<String>) {

    private val participants: MutableSet<String>
    private val debts: MutableList<MutableList<BigDecimal>>

    constructor(debtGraph: DebtGraph) : this(debtGraph.participants)

    init {
        this.participants = LinkedHashSet(participants)
        debts = MutableList(participants.size) { MutableList(participants.size) { ZERO } }
    }

    fun addParticipant(participant: String) {
        debts.forEach { list -> list.add(ZERO) }
        debts.add(MutableList(debts.size + 1) { ZERO })
        participants.add(participant)
    }

    fun getParticipantsCount(): Int {
        return participants.size
    }

    fun lend(lender: String, amount: BigDecimal) {
        if (amount <= ZERO) {
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

    fun lend(lender: String, debtor: String, amount: BigDecimal) {
        lend(lender, setOf(debtor), amount)
    }

    fun lend(lender: String, debtors: Set<String>, amount: BigDecimal) {
        if (amount <= ZERO) {
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
        val personalBalances =
            MutableList(debts.size) { ZERO } // shows balance adjustments in the end (>0 lender)
        val edges = sortedSetOf(
                compareBy<DebtEdge> { participants.indexOf(it.debtor) }
                        .thenBy { participants.indexOf(it.lender) }
                        .thenBy { it.debt })

        debts.forEachIndexed { lenderIndex, debtors ->
            debtors.forEachIndexed { debtorIndex, debt ->
                if (lenderIndex > debtorIndex) {
                    debts[debtorIndex][lenderIndex] -= debts[lenderIndex][debtorIndex]
                    debts[lenderIndex][debtorIndex] = ZERO
                }
                personalBalances[lenderIndex] += debt
                personalBalances[debtorIndex] -= debt
            }
        }

        while (!personalBalances.all { debt -> debt.compareTo(ZERO) == 0 }) {
            val max = personalBalances.withIndex().maxBy { it.value }!!
            val min = personalBalances.withIndex().minBy { it.value }!!
            val minAbs = max.value.min(min.value.abs())

            edges += DebtEdge(participants.elementAt(min.index), participants.elementAt(max.index), minAbs)

            personalBalances[max.index] = max.value - minAbs
            personalBalances[min.index] = min.value + minAbs
        }
        return edges
    }

    override fun toString(): String {
        return "DebtGraph(participants=$participants, debts=$debts)"
    }


}