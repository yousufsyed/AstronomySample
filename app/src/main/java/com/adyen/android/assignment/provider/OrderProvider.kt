package com.adyen.android.assignment.provider

import com.adyen.android.assignment.data.AstronomyInfo.PlanetaryData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

interface OrderProvider {
    val orderFlow: Flow<Order>
    fun updateOrder(order: Order)
}

// TODO save the selection to dataStore so as to persist the selection across app launches.
class DefaultOrderProvider @Inject constructor() : OrderProvider {

    private val _orderFlow = MutableStateFlow(Order.NONE)

    override val orderFlow: StateFlow<Order> = _orderFlow

    override fun updateOrder(order: Order) {
        _orderFlow.update { order }
    }
}

enum class Order {
    ORDER_BY_TITLE,
    ORDER_BY_DATE,
    NONE
}

val Order.toComparator: Comparator<PlanetaryData>?
    get() = when (this) {
        Order.ORDER_BY_TITLE -> TITLE_COMPARATOR
        Order.ORDER_BY_DATE -> DATE_COMPARATOR
        else -> null
    }

val TITLE_COMPARATOR: Comparator<PlanetaryData>
    get() = Comparator { p0, pi ->
        if (p0.title > pi.title) {
            1
        } else if (p0.title == pi.title) {
            0
        } else {
            -1
        }
    }

val DATE_COMPARATOR: Comparator<PlanetaryData>
    get() = Comparator { p0, pi ->
        if (p0.date > pi.date) {
            -1
        } else if (p0.date == pi.date) {
            0
        } else {
            1
        }
    }
