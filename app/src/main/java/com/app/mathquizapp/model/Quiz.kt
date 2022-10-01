package com.app.mathquizapp.model

import android.content.Context

class Quiz(val context: Context){

    var index = 0  //index of current Question
    private val dbHelper : DatabaseHelper = DatabaseHelper(context)
    private val answersMap = HashMap<Question, String>()  //map the question to selected answers
    private val questionList: ArrayList<Question> = dbHelper.generateQuestionsList()
    val totalQuestions = questionList.size // number of total questions

    //get question text in a string
    fun getQuestion() : String{
        return questionList[index].question
    }

    //get the question object
    fun getQuestionObject() : Question{
        return questionList[index]
    }

    //next question
    fun moveNext(){
        if(index < totalQuestions-1)
            index++
    }

    //previous question
    fun movePrevious(){
        if(index > 0)
            index--
    }

    //return overall mark
    fun checkQuiz(): Int{
        var marks = 0
        for(ques in questionList){
            if(ques.answer == answersMap[ques])
            marks++
        }
        return marks
    }

    //map the selected answer so it can be used
    fun mapTheAnswer(selectedAnswer : String){
        answersMap[getQuestionObject()] = selectedAnswer
    }

    fun getIndexOfMarkedAns() : Int{
        return getQuestionObject().otherAnswers.indexOf(answersMap[getQuestionObject()])
    }

    fun quizEnded() : Boolean{
        return index == totalQuestions-1
    }
}
