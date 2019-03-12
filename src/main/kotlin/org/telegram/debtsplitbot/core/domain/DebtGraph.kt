package org.telegram.debtsplitbot.core.domain

import com.google.common.collect.Sets

class DebtGraph {
    val edges: List<DebtEdge>

    constructor(debtors: Set<String>) {
        Sets.combinations(debtors);
    }
}