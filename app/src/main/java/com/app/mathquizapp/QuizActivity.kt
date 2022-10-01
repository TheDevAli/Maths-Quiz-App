package com.app.mathquizapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.app.mathquizapp.model.Quiz
import java.lang.NullPointerException

//this is a part of controller

class QuizActivity : AppCompatActivity() {

    private lateinit var optsRadioGroup : RadioGroup
    private lateinit var quiz : Quiz  //quiz part of model

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        //initialize required view objects
        optsRadioGroup = findViewById(R.id.optsRadioGroup)

        //create database
        quiz = Quiz(this)
        displayQuestion()
    }

    fun nextQuestion(view: View) {
        if(quiz.quizEnded()){
            findViewById<Button>(R.id.submit_button).visibility = View.VISIBLE
        }
        try{
            quiz.mapTheAnswer(this.checkedRadioTextToString())
            quiz.moveNext()
            displayQuestion()  //show the next question
        }
        catch (e : NullPointerException){
            //radiogroup will throw null pointer exception if we try to get index of unchecked radio Button
            Toast.makeText(this, "Select an option!", Toast.LENGTH_SHORT).show()
        }
    }

    fun prevQuestion(view: View) {
        try{
            quiz.mapTheAnswer(this.checkedRadioTextToString())
        }catch (e : NullPointerException){
            //even if no radio button is marked, we have to go back
        }
        quiz.movePrevious()
        displayQuestion()  //show the previous question due to qNo--
    }

    private fun displayQuestion() {
        //grab question from db
        val thisQuestion = quiz.getQuestion()
        findViewById<TextView>(R.id.questionTextView).setText("Question # ${quiz.index+1}\n$thisQuestion")

        //start from radio group
        optsRadioGroup = findViewById(R.id.optsRadioGroup)
        for(i in 0..2){
            val radBtn : RadioButton = optsRadioGroup.getChildAt(i) as RadioButton
            radBtn.text = quiz.getQuestionObject().otherAnswers[i]
        }
        try {
            val radioToCheck : RadioButton = optsRadioGroup.getChildAt(quiz.getIndexOfMarkedAns()) as RadioButton
            radioToCheck.isChecked = true
        }catch (e :Exception){
            optsRadioGroup.clearCheck()
            //clear the check for next question
        }
    }

    fun submitQuiz(view: View) {
        val childName = intent.getStringExtra("childName")
        val intent = Intent(this, ChartActivity::class.java)
        intent.putExtra("childName", childName)
        intent.putExtra("marks", quiz.checkQuiz())
        intent.putExtra("total", quiz.totalQuestions)
        startActivity(intent)
        this.finish()
    }

    private fun checkedRadioTextToString() : String{
        val checkedRadioBtn : RadioButton = findViewById(optsRadioGroup.checkedRadioButtonId)
        return checkedRadioBtn.text.toString()
    }
}