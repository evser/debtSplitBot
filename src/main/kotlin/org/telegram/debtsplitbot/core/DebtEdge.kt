package org.telegram.debtsplitbot.core

import java.math.BigDecimal
import java.util.Locale.ENGLISH

class DebtEdge(val debtor: String, val lender: String, var debt: BigDecimal) {

    override fun toString(): String {
        return "'$debtor' owes '$lender' ${debt.format(2)}"
    }

    private fun BigDecimal.format(digits: Int) = java.lang.String.format(ENGLISH, "%.${digits}f", this)!!

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DebtEdge

        if (debtor != other.debtor) return false
        if (lender != other.lender) return false
        if (debt != other.debt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = debtor.hashCode()
        result = 31 * result + lender.hashCode()
        result = 31 * result + debt.hashCode()
        return result
    }

}
