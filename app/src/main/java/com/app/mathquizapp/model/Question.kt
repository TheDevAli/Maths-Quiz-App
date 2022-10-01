package com.app.mathquizapp.model


data class Question(val question: String, val answer : String, val category: String, val otherAnswers: Array<String>) {}
