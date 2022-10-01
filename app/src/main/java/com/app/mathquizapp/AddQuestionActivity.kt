package com.app.mathquizapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.app.mathquizapp.model.Category
import com.app.mathquizapp.model.DatabaseHelper
import com.app.mathquizapp.model.Question

class AddQuestionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_question)

        val categories = Category.values()
        val categorySpinner : Spinner = findViewById(R.id.categorySpinner)
        val categoryList = arrayListOf<String>()
        for(category in categories){  //add all constant categories to list to be shown in spinner
            categoryList.add(category.toString())
        }
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categoryList)
        categorySpinner.adapter = categoryAdapter
    }

    fun addQuestionClicked(view: android.view.View) {
        val question  = findViewById<EditText>(R.id.enterQuestion).text.toString()
        val answer  = findViewById<EditText>(R.id.answer).text.toString()
        val answer1  = findViewById<EditText>(R.id.enterAnswer1).text.toString()
        val answer2  = findViewById<EditText>(R.id.enterAnswer2).text.toString()
        val answer3  = findViewById<EditText>(R.id.enterAnswer3).text.toString()
        val answer4  = findViewById<EditText>(R.id.enterAnswer4).text.toString()
        val category = findViewById<Spinner>(R.id.categorySpinner).selectedItem.toString()

        //validation - if any answer input is empty, throw an error
        if(question.isBlank() || answer.isBlank() || answer1.isBlank()
            || answer2.isBlank() || answer3.isBlank() || answer4.isBlank()){
            Toast.makeText(this, "Enter all fields!", Toast.LENGTH_SHORT).show()
            return
        }

        val newQuestion = Question(question, answer, category, arrayOf(answer1, answer2, answer3, answer4))

        val db = DatabaseHelper(this)
        if(db.addQuestion(newQuestion))
            Toast.makeText(this, "Question Added to Database!", Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(this, "Failed to Add Question!", Toast.LENGTH_SHORT).show()

        //Clear Inputs
        clearInputs()
    }

    private fun clearInputs(){
        findViewById<EditText>(R.id.enterQuestion).setText("")
        findViewById<EditText>(R.id.answer).setText("")
        findViewById<EditText>(R.id.enterAnswer1).setText("")
        findViewById<EditText>(R.id.enterAnswer2).setText("")
        findViewById<EditText>(R.id.enterAnswer3).setText("")
        findViewById<EditText>(R.id.enterAnswer4).setText("")
    }
}