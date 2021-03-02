package com.example.demo.model


data class FilterModel(
        var fromDate: String = "",
        var toDate: String = "",
        var status: String = "",
        var priority: String = "",
        var userName: String = "",
        var isEnded: Boolean = false

)
