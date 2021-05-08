package pl.edu.pja.proj1.activities

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

//https://medium.com/@supahsoftware/custom-android-views-graph-view-and-drawing-on-the-canvas-d03c2ea2b703
class GraphView (context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private val dataSet = mutableListOf<DataPoint>()
    private var xMin = 0
    private var xMax = 0
    private var yMin = 0
    private var yMax = 0

    fun setData(newDataSet: List<DataPoint>, lastDay: Int) {
        xMin = 0
        xMax = lastDay

        val maxBy = newDataSet.maxByOrNull { it.yVal }?.yVal ?: 0
        val minBy = newDataSet.minByOrNull { it.yVal }?.yVal ?: 0

        if (newDataSet.minByOrNull { it.yVal }?.yVal ?: 0 < 0) {
            yMin = ((minBy / 100) - 1) * 100
        } else {
            yMin = ((minBy/ 100) + 1) * 100
        }
        yMax = ((maxBy/ 100) + 1) * 100
        yMax -= yMin

        dataSet.clear()
        dataSet.addAll(newDataSet)
        invalidate()
    }


    private val dataPointLinePaintGreen = Paint().apply {
        color = Color.GREEN
        strokeWidth = 7f
        isAntiAlias = true
    }

    private val dataPointLinePaintRed = Paint().apply {
        color = Color.RED
        strokeWidth = 7f
        isAntiAlias = true
    }

    private val axisLinePaint = Paint().apply {
        color = Color.GRAY
        strokeWidth = 4f
    }

    private val dayNumberPaint = Paint().apply {
        color = Color.BLACK
        textSize = 15f
        strokeWidth = 50f
    }

    private fun Int.toRealX() = toFloat() / xMax * width
    private fun Int.toRealY() = toFloat() / yMax * height

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for(i in 0..(yMax - yMin)/100){
            canvas.drawText( ((-yMax) + i*100).toString(), 20f, (-yMin + i*100).toFloat(), dayNumberPaint)
        }

        dataSet.forEachIndexed { index, currentDataPoint ->
            if (index < dataSet.size - 1) {
                val nextDataPoint = dataSet[index + 1]
                val startX = currentDataPoint.xVal.toRealX()
                val startY = currentDataPoint.yVal.toRealY()
                val endX = nextDataPoint.xVal.toRealX()
                val endY = nextDataPoint.yVal.toRealY()
                if(endY >= yMax - yMin){
                    canvas.drawLine(startX, startY, endX, endY, dataPointLinePaintGreen)
                }else{
                    canvas.drawLine(startX, startY, endX, endY, dataPointLinePaintRed)
                }
                canvas.drawLine(startX, (yMax - yMin).toFloat(), endX, (yMax - yMin).toFloat(), axisLinePaint)
                canvas.drawText( (index + 1).toString(), startX.toFloat(), (yMax - yMin).toFloat(), dayNumberPaint)
            }

        }
        canvas.drawLine(0f, yMin.toFloat(), 0f, height.toFloat(), axisLinePaint)

    }
}


data class DataPoint(
    val xVal: Int,
    val yVal: Int
)