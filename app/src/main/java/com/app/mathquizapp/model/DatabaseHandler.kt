package com.app.mathquizapp.model

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

private val DB_NAME = "QuizDB.db"
private val DB_VERSION = 1

class DatabaseHelper(private val context : Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    private lateinit var sqLiteDatabase : SQLiteDatabase

    // tables headings
    private val ChildTable = "ChildTable"
    private val ChildName = "ChildName"
    private val ChildMarks = "ChildMarks"
    private val DateOfTaken = "DateOfTaken"

    private val QuestionTable = "QuestionTable"
    private val QuestionID = "QuestionID"
    private val Question = "Question"
    private val QuestionCategory = "QuestionCategory"

    private val AnswerTable = "AnswerTable"
    private val AnswerID = "AnswerID"
    private val Answer = "AnswerText"
    private val WhichQuestionID = "WhichQuestionID"
    private val IsCorrect =   "IsCorrect"

    override fun onCreate(db: SQLiteDatabase?) {
        if (db != null) {
            this.sqLiteDatabase = db
        }
        val sqlQuesTableCreateStmt : String = "CREATE TABLE $QuestionTable ( $QuestionID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$Question TEXT, $QuestionCategory TEXT )"

        val sqlAnsTableCreateStmt : String = "CREATE TABLE $AnswerTable ( $AnswerID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$Answer TEXT, $WhichQuestionID INT, $IsCorrect BOOLEAN )"

        val sqlUserTableCreateStmt : String = "CREATE TABLE $ChildTable ( User_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$ChildName TEXT, $ChildMarks INT, $DateOfTaken TEXT )"
        db?.execSQL(sqlQuesTableCreateStmt)
        db?.execSQL(sqlAnsTableCreateStmt)
        db?.execSQL(sqlUserTableCreateStmt)

        //add initial 42 questions for the first time database is created
        createDefaultQuestions()  //this function will be called only first time when database is created
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $QuestionTable")
        db?.execSQL("DROP TABLE IF EXISTS $AnswerTable")
        db?.execSQL("DROP TABLE IF EXISTS $ChildTable")
        onCreate(db)
    }

    fun addChild(child : Child) : Boolean{
        //add user to database
        val writeDB = this.writableDatabase
        val childContentValues = ContentValues()

        childContentValues.put(ChildName, child.name)
        childContentValues.put(DateOfTaken, child.dateOfTest)
        childContentValues.put(ChildMarks, child.marks)

        val result = writeDB.insert(ChildTable, null, childContentValues)
        writeDB.close()
        if(result == -1L)
            return false
        return true
    }

    fun getMarksListOfChild(thisChildName : String) : ArrayList<Child>{

        val readDB = readableDatabase
        val marksList = ArrayList<Child>()
        val cursor = readDB.rawQuery("SELECT * from $ChildTable WHERE $ChildName = '$thisChildName'", null)
        if(cursor.moveToFirst()){
            do {
                val child = Child(thisChildName, cursor.getInt(2), "")
                child.dateOfTest = cursor.getString(3)
                //we are resetting date because date is always initialized with current date
                //but now we want to get date present in database so, resetting
                marksList.add(child)
            }while (cursor.moveToNext())
        }
        cursor.close()
        readDB.close()
        return marksList
    }

    fun addQuestion(question : Question) : Boolean{

        val writeDB = this.writableDatabase
        val quesContentValues  = ContentValues()
        val ansContentValues  = ContentValues()

        //put question in database
        quesContentValues.put(Question, question.question)
        quesContentValues.put(QuestionCategory, question.category)
        val questionID = writeDB.insert(QuestionTable, null, quesContentValues)

        if (questionID == -1L)
            return false

        //question Id is used to put its relative answers to answers table
        for(answer in question.otherAnswers){
            ansContentValues.put(WhichQuestionID, questionID)
            ansContentValues.put(Answer, answer)
            ansContentValues.put(IsCorrect, false)
            writeDB.insert(AnswerTable, null, ansContentValues)
        }
        ansContentValues.put(WhichQuestionID, questionID)
        ansContentValues.put(Answer, question.answer)
        ansContentValues.put(IsCorrect, true)
        writeDB.insert(AnswerTable, null, ansContentValues)

        writeDB.close()
        return true
    }

    // create default questions to database for the first time only after app installation
    private fun createDefaultQuestions(){
        val defaultQuestions = arrayOf(
            Question("Write 7 in words : ", "seven", Category.NUMBER_AND_PLACE_VALUE.toString(), arrayOf("seventy", "one seven", "seventeen", "seventy one")),
            Question("Write 19 in words : ", "nineteen", Category.NUMBER_AND_PLACE_VALUE.toString(), arrayOf("ninety", "ninety one", "nine", "one nine")),
            Question("Count stars:  * * * * * * * * *", "9", Category.NUMBER_AND_PLACE_VALUE.toString(), arrayOf("6", "8", "10", "11")),
            Question("Count 'A' from  [ BBABAOOPPAA ] : ", "4", Category.NUMBER_AND_PLACE_VALUE.toString(), arrayOf("8", "2", "5", "0")),
            Question("What is missing from [ ?, 1, 2, 3, 4, 5] : ", "0", Category.NUMBER_AND_PLACE_VALUE.toString(), arrayOf("11", "20", "10", "6")),
            Question("What is missing from [ 10, 11, ?,  13, 14, 15] : ", "12", Category.NUMBER_AND_PLACE_VALUE.toString(), arrayOf("8", "2", "5", "0")),

            Question("7 + ? = 18 : ", "11", Category.ADDITION_AND_SUBTRACTION.toString(), arrayOf("7", "5", "16", "12")),
            Question("2 + ? = 7 : ", "5", Category.ADDITION_AND_SUBTRACTION.toString(), arrayOf("15", "2", "6", "9")),
            Question("15 - ? = 2 : ", "13", Category.ADDITION_AND_SUBTRACTION.toString(), arrayOf("7", "5", "16", "12")),
            Question("9 - ? = 8 : ", "1", Category.ADDITION_AND_SUBTRACTION.toString(), arrayOf("7", "5", "2", "9")),
            Question("10 - 0 = ? : ", "10", Category.ADDITION_AND_SUBTRACTION.toString(), arrayOf("1", "0", "9", "20")),
            Question("20 + 0 = ? : ", "20", Category.ADDITION_AND_SUBTRACTION.toString(), arrayOf("200", "2", "0", "19")),

            Question("2 X 3 = ?", "6", Category.MULTIPLICATION_AND_DIVISION.toString(), arrayOf("16", "8", "10", "20")),
            Question("7 X 2 = ?", "14", Category.MULTIPLICATION_AND_DIVISION.toString(), arrayOf("16", "8", "7", "21")),
            Question("18 / 6 = ?", "3", Category.MULTIPLICATION_AND_DIVISION.toString(), arrayOf("16", "8", "4", "6")),
            Question("20 / 10 = ?", "2", Category.MULTIPLICATION_AND_DIVISION.toString(), arrayOf("10", "20", "0", "1")),
            Question("'X' sign is called", "Multiplication", Category.MULTIPLICATION_AND_DIVISION.toString(), arrayOf("Division", "Addition", "Subtraction", "none of these")),
            Question("Where is division happening?", "12/4", Category.MULTIPLICATION_AND_DIVISION.toString(), arrayOf("12 = 3", "12 + 3", "12 - 4", "12 X 4")),

            Question("Which is the fraction's correct form? ", "p/q where q is not 0", Category.FRACTIONS.toString(), arrayOf("p/q where q is 0", "p/q, p & q both are zero", "p+q", "p-q")),
            Question("What is the numerator of 12 / 7 ", "12", Category.FRACTIONS.toString(), arrayOf("7", "/", "p/q", "none of these")),
            Question("What is the denominator of 13 / 20 ", "20", Category.FRACTIONS.toString(), arrayOf("1", "3", "2", "0")),
            Question("Which is proper fraction? ", "1 / 4", Category.FRACTIONS.toString(), arrayOf("4 / 1", "2 / 2" ," 2 / 1", "12 / 4")),
            Question("Which is improper fraction? ", "12 / 3", Category.FRACTIONS.toString(), arrayOf("2 / 6", "1 / 2, 1 / 11", "4 / 12")),
            Question("How to write a half as a fraction? ", "1 / 2", Category.FRACTIONS.toString(), arrayOf("3 / 2", " 4 / 2", "1 / 4", "2 / 1")),

            Question("Kg is the unit of ___", "mass or weight", Category.MEASUREMENT.toString(), arrayOf("length or distance", "none of these", "light", "time")),
            Question("We measure time in?", "seconds", Category.MEASUREMENT.toString(), arrayOf("meters", "kgs", "meter per second", "none of these")),
            Question("What time is 2:30 ?", "half past two", Category.MEASUREMENT.toString(), arrayOf("half past twelve", "none of these", "2'o clock", "3'o clock")),
            Question("How many grams are in a kilogram?", "1000", Category.MEASUREMENT.toString(), arrayOf("1", "10", "100", "500")),
            Question("Measurement is done to know ___ of object?", "quantity", Category.MEASUREMENT.toString(), arrayOf("quality", "none of these", "purity", "shape")),
            Question("How many eggs are there, in a half dozen of eggs?", "6", Category.MEASUREMENT.toString(), arrayOf("12", "24", "8", "3")),

            Question("What shape is a Dice? ", "cube", Category.PROPERTIES_OF_SHAPES.toString(), arrayOf("sphere", " circle", "square", "cylinder")),
            Question("Which one is a 3D object? ", "cylinder", Category.PROPERTIES_OF_SHAPES.toString(), arrayOf("square", " rectangle", "circle", "triangle")),
            Question("What does 'D' mean in 2D and 3D? ", "Dimensions", Category.PROPERTIES_OF_SHAPES.toString(), arrayOf("Diameter", " Distance", "Destination", "Diagram")),
            Question("How many faces does a cube have? ", "6", Category.PROPERTIES_OF_SHAPES.toString(), arrayOf("4", " 5", "2", "8")),
            Question("A triangle is formed by ___ angles? ", "3", Category.PROPERTIES_OF_SHAPES.toString(), arrayOf("1", " 4", "6", "20")),
            Question("A proper triangle must have ___ degree angles?", "90", Category.PROPERTIES_OF_SHAPES.toString(), arrayOf("120", " 30", "60", "45")),

            Question("Complete the directions: north, east, south, ?", "west", Category.POSITION_AND_DIRECTION.toString(), arrayOf("south", "right", "under", "down")),
            Question("What is the position of the sun?", "above the earth", Category.POSITION_AND_DIRECTION.toString(), arrayOf("under the earth", "inside of earth", "nowhere", "down the earth")),
            Question("Which direction is missing? up, down, left, ___", "right", Category.POSITION_AND_DIRECTION.toString(), arrayOf("write", "nothing", "inside", "under")),
            Question("A ball is thrown up then what is the direction of the ball after reaching its highest position?", "down", Category.POSITION_AND_DIRECTION.toString(), arrayOf("up", "right", "left", "in")),
            Question("What is the direction of a pull force?", "towards the source", Category.POSITION_AND_DIRECTION.toString(), arrayOf("away from source", "above the source", "under the source", "down")),
            Question("What is the direction of a push force?", "away from source", Category.POSITION_AND_DIRECTION.toString(), arrayOf("towards the source", "above the source", "under the source", "down")),
        )//end of default questions

        //content values object is required to add question to database
        val quesContentValues  = ContentValues()
        val ansContentValues  = ContentValues()

        for(question in defaultQuestions){
            quesContentValues.put(Question, question.question)
            quesContentValues.put(QuestionCategory, question.category)
            val thisQuestionID = sqLiteDatabase.insert(QuestionTable, null, quesContentValues)

            //add wrong answers to database
            for(ans in question.otherAnswers){
                ansContentValues.put(WhichQuestionID, thisQuestionID)
                ansContentValues.put(Answer, ans)
                ansContentValues.put(IsCorrect, false)
                sqLiteDatabase.insert(AnswerTable, null, ansContentValues)
            }
            //add the correct answer to database
            ansContentValues.put(WhichQuestionID, thisQuestionID)
            ansContentValues.put(Answer, question.answer)
            ansContentValues.put(IsCorrect, true)
            sqLiteDatabase.insert(AnswerTable, null, ansContentValues)
        }
    }

    //get a list of random questions from database
    fun generateQuestionsList(): ArrayList<Question>{
        val list = ArrayList<Question>()
        val readDB: SQLiteDatabase = this.readableDatabase
        for(category in Category.values()){
            val cursorToQuestion : Cursor = readDB.rawQuery("SELECT * FROM $QuestionTable " +
                    "WHERE $QuestionCategory = '${category.toString()}' ORDER BY random() LIMIT 2", null)
            if(cursorToQuestion.moveToFirst()){
                do {
                    val thisQuestionID = cursorToQuestion.getInt(0)
                    val answerOptions = Array<String>(3, { i->""})

                    var cursorToAnswer : Cursor = readDB.rawQuery("SELECT * FROM $AnswerTable WHERE $WhichQuestionID = '$thisQuestionID' " +
                            "AND $IsCorrect = 0 ORDER BY random() LIMIT 2", null)
                    var count = 0
                    var trueAnswer = ""
                    if(cursorToAnswer.moveToFirst()){
                        do {
                            answerOptions[count++] =(cursorToAnswer.getString(1))
                        }while (cursorToAnswer.moveToNext())
                    }
                    cursorToAnswer  = readDB.rawQuery("SELECT * FROM $AnswerTable WHERE $WhichQuestionID = '$thisQuestionID' " +
                            "AND $IsCorrect = 1", null)
                    if(cursorToAnswer.moveToFirst())
                        trueAnswer = cursorToAnswer.getString(1)
                    answerOptions[count++] = trueAnswer
                    answerOptions.shuffle()
                    list.add(Question(cursorToQuestion.getString(1), trueAnswer, "", answerOptions))
                    cursorToAnswer.close()
                }while (cursorToQuestion.moveToNext())
            }
            cursorToQuestion.close()
        }
        readDB.close()
        return list
    }
}