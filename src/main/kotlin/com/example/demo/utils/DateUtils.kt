package com.example.demo.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale

import java.text.DateFormat


object DateUtils {
    private val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy");

    fun convertDateToString(date: Date): String {
        return simpleDateFormat.format(date)
    }

    fun convertStringToDate(date: String): Date {
        return simpleDateFormat.parse(date)
    }

}