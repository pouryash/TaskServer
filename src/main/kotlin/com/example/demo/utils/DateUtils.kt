package com.example.demo.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale

import java.text.DateFormat


object DateUtils {
    private val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy");

    fun convertDateToString(date: Date?): String {
        date?.let {
            return simpleDateFormat.format(date)
        }
        return ""
    }

    fun convertStringToDate(date: String): Date? {
        if (date.isNotEmpty())
            return simpleDateFormat.parse(date)
        return null
    }

}