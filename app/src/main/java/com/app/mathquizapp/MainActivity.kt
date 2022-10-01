package com.app.mathquizapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun startClick(view: View) {
        //Getting name from user
        val enterName = findViewById<EditText>(R.id.enterName)
        val childName = enterName.text.toString()

        //validation - user must enter a name else throw an error
        if(enterName.text.isEmpty()){
            Toast.makeText(this, "Enter name!", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, QuizActivity::class.java).apply {
            putExtra("childName", childName)
        }
        startActivity(intent)
    }

    //if user clicks Admin button - redirect to AddQuestionActivity
    fun adminClick(view: View) {
        startActivity(Intent(this, AddQuestionActivity::class.java))
    }
}