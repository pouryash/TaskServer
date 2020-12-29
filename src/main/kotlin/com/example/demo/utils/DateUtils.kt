package com.example.demo.utils

import java.text.SimpleDateFormat
import java.util.*


object DateUtils {

    fun getCurrentDate(): String{
        return SimpleDateFormat("dd/MM/yyyy").format(Date())
    }

}