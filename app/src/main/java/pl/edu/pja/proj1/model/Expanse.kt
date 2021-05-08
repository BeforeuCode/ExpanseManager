package pl.edu.pja.proj1.model

import java.util.*

data class Expanse(
        var id: Long = -1,
        var place: String = "",
        var category: String = "",
        var date: Date? = null,
        var amount: Double = 0.0,
        var isIncome: Boolean = false,
)