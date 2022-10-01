package com.app.mathquizapp.model

import java.text.SimpleDateFormat
import java.util.*

data class Child(val name: String, val marks : Int, var dateOfTest : String){
    init {
        //initialize date of taken with current date
        dateOfTest = getDateTime()

    }

    //Gets today's date (date test is taken) and formats it
    private fun getDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }
}
