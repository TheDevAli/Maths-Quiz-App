package com.app.mathquizapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Cartesian
import com.anychart.core.cartesian.series.Column
import com.anychart.enums.Anchor
import com.anychart.enums.HoverMode
import com.anychart.enums.Position
import com.anychart.enums.TooltipPositionMode
import com.app.mathquizapp.model.DatabaseHelper
import com.app.mathquizapp.model.Child

import java.util.ArrayList


class ChartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        displayChart()
    }

    /**
     * Displays a chart showing child their score
     * Colour coded depending on score (red, amber, green)
     *
     * Uses the default implementation of Anychart
     * from their website
     */
    private fun displayChart(){

        val name : String = intent.getStringExtra("childName").toString()
        val marks : Int = intent.getIntExtra("marks", 0)
        findViewById<TextView>(R.id.scoreTextView).text = "Name: $name \nMarks: $marks / 14"

        val db = DatabaseHelper(this)

        db.addChild(Child(name, marks, ""))
        val anyChartView = findViewById<AnyChartView>(R.id.chartview)

        //default implementation of Anychart from their website
        val cartesian: Cartesian = AnyChart.column()
        val data: ArrayList<DataEntry> = ArrayList()
        for(childMarks in db.getMarksListOfChild(name))
            data.add(ValueDataEntry(childMarks.dateOfTest, childMarks.marks.toFloat()))

        val column: Column = cartesian.column(data)
        column.fill("function() {" +
                "            if (this.value <= 5)" +
                "                return 'red';" +
                "            else if(this.value >5 && this.value < 10)" +
                "                return 'yellow';" +
                "            return 'green';" +
                "        }")

        column.tooltip()
            .titleFormat("{%X}")
            .position(Position.CENTER_BOTTOM)
            .anchor(Anchor.CENTER_BOTTOM)
            .offsetX(0.0)
            .offsetY(5.0)
            .format("{%Value}{groupsSeparator: }")

        cartesian.animation(true)
        cartesian.title("Your Previous Marks")
        cartesian.title().fontColor("#4e0d3a")

        cartesian.yScale().minimum(0.0)

        cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }")

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT)
        cartesian.interactivity().hoverMode(HoverMode.BY_X)

        cartesian.xAxis(0).title("Date of Test")
        cartesian.yAxis(0).title("Marks")

        anyChartView.setChart(cartesian)
    }
}