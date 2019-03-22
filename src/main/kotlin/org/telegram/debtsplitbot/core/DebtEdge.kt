package org.telegram.debtsplitbot.core

class DebtEdge(val debtor: String, val lender: String, var debt: Double) {

    override fun toString(): String {
        return "Debtor: '$debtor', Lender: '$lender', Debt: $debt)"
    }
}
