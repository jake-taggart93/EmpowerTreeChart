package com.example.empowertreechart

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.AttributeSet
import android.view.View

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = TreeChartView(context = this, items = arrayOf(1, 2, 6, 2, 3 , 6, 9, 8, 8, 2))
        setContentView(view)
    }
}

class TreeChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    private val items: Array<Int> = emptyArray()
) : View(context, attrs, defStyle) {

    override fun onDraw(canvas: Canvas) {
        if (items.isEmpty()) {
            super.onDraw(canvas)
        } else {
            renderTreeChart(items, canvas)
        }
    }

    private fun renderTreeChart(items: Array<Int>, canvas: Canvas) {
        if (items.size > 1) {
            // Initialize arrays to split into two
            var firstHalf = emptyArray<Int>()
            var secondHalf = emptyArray<Int>()

            // Splitting initial array into equal or near equal arrays
            items.sortedArrayDescending().forEach { i ->
                if (firstHalf.sum() + i <= (items.sum() / 2)) {
                    firstHalf = firstHalf.plus(i)
                } else {
                    secondHalf = secondHalf.plus(i)
                }
            }

            // Print arrays to test
            val firstHalfText = "1st Half: " + firstHalf.toIntArray().contentToString() + "\n"
            val secondHalfText = "2nd Half: " + secondHalf.toIntArray().contentToString()
            println(firstHalfText + secondHalfText)

            // Stroke for lines
            val paint = Paint()
            paint.style = Paint.Style.STROKE;
            paint.setColor(Color.BLACK)

            // Finding the right spot to put the first divider line (vertical) based on the sum of both arrays
            val xCoordinateOverallVertical =
                (firstHalf.sum().toFloat() / items.sum().toFloat()) * canvas.width

            // Vertical Overall
            canvas.drawLine(
                xCoordinateOverallVertical,
                0f,
                xCoordinateOverallVertical,
                canvas.height.toFloat(),
                paint
            )

            // Deal with one of the arrays split initially
            if (firstHalf.size > 1) {
                var quadrantOne = emptyArray<Int>()
                var quadrantTwo = emptyArray<Int>()

                // Split into 2 lists
                firstHalf.forEach { i ->
                    if (quadrantOne.sum() + i <= (firstHalf.sum() / 2)) {
                        quadrantOne = quadrantOne.plus(i)
                    } else {
                        quadrantTwo = quadrantTwo.plus(i)
                    }
                }

                // Print to compare results
                println("1st Half Quadrant 1: " + quadrantOne.toIntArray().contentToString())
                println("1st Half Quadrant 2: " + quadrantTwo.toIntArray().contentToString())

                // Divide Quadrants
                val quadrantDividerY =
                    (quadrantOne.sum().toFloat() / firstHalf.sum().toFloat()) * canvas.height
                canvas.drawLine(
                    0f, quadrantDividerY, xCoordinateOverallVertical,
                    quadrantDividerY, paint
                )

                // Variable for tracking what x position to add to when calculating necessary lines
                var quadrantOneStartX = 0f
                quadrantOne.forEachIndexed { index, it ->
                    // We want one less line that the size so that each space divided by lines represents the element
                    if (index < quadrantOne.size - 1) {
                        // Calculate the percent of space to take up
                        val area = it.toFloat() / quadrantOne.sum().toFloat()
                        // Calculate the actual size of space horizontally
                        val sizeX = area * xCoordinateOverallVertical
                        // Calculate where the x coordinate will be for the vertical line to be drawn using the dynamic variable and calculated size/width
                        val xCoordinate = sizeX + quadrantOneStartX
                        // Draw line
                        canvas.drawLine(xCoordinate, quadrantDividerY, xCoordinate, 0f, paint)
                        // Update the new starting point for any further necessary calculations in the loop
                        quadrantOneStartX += sizeX
                    }
                }

                // Same as above but for the bottom quadrant
                var quadrantTwoStartX = 0f
                quadrantTwo.forEachIndexed { index, it ->
                    if (index < quadrantTwo.size - 1) {
                        val area = it.toFloat() / quadrantTwo.sum().toFloat()
                        val sizeX = area * xCoordinateOverallVertical
                        val xCoordinate = sizeX + quadrantTwoStartX
                        canvas.drawLine(
                            xCoordinate,
                            quadrantDividerY,
                            xCoordinate,
                            canvas.height.toFloat(),
                            paint
                        )
                        quadrantTwoStartX += sizeX
                    }
                }
            }

            // Deal with the second array split initially; this handles the same as above but knowing that it's on the right side of the screen being the difference
            if (secondHalf.size > 1) {
                var quadrantOne = emptyArray<Int>()
                var quadrantTwo = emptyArray<Int>()

                // Divide quadrants
                secondHalf.forEach { i ->
                    if (quadrantOne.sum() + i <= (secondHalf.sum() / 2)) {
                        quadrantOne = quadrantOne.plus(i)
                    } else {
                        quadrantTwo = quadrantTwo.plus(i)
                    }
                }

                // This is for testing/comparison purposes
                println("Second Half Quadrant 1: " + quadrantOne.toIntArray().contentToString())
                println("Second Half Quadrant 2: " + quadrantTwo.toIntArray().contentToString())

                // Calculate where to place the yCoordinate for the horizontal divider between quadrants
                val quadrantDividerY =
                    (quadrantOne.sum().toFloat() / secondHalf.sum().toFloat()) * canvas.height
                canvas.drawLine(
                    xCoordinateOverallVertical, quadrantDividerY, canvas.width.toFloat(),
                    quadrantDividerY, paint
                )

                var quadrantOneStartX =
                    xCoordinateOverallVertical + (quadrantOne.first().div(quadrantOne.sum())
                        .toFloat())
                quadrantOne.forEachIndexed { index, it ->
                    if (index < quadrantOne.size - 1) {
                        val area = it.toFloat() / quadrantOne.sum().toFloat()
                        val sizeX = area * xCoordinateOverallVertical
                        val xCoordinate = sizeX + quadrantOneStartX
                        canvas.drawLine(xCoordinate, 0f, xCoordinate, quadrantDividerY, paint)
                        quadrantOneStartX += sizeX
                    }
                }

                var quadrantTwoStartX =
                    xCoordinateOverallVertical + (quadrantTwo.first().div(quadrantOne.sum())
                        .toFloat())
                quadrantTwo.forEachIndexed { index, it ->
                    if (index < quadrantTwo.size - 1) {
                        val area = it.toFloat() / quadrantTwo.sum().toFloat()
                        val sizeX = area * xCoordinateOverallVertical
                        val xCoordinate = sizeX + quadrantTwoStartX
                        canvas.drawLine(
                            xCoordinate,
                            quadrantDividerY,
                            xCoordinate,
                            canvas.height.toFloat(),
                            paint
                        )
                        quadrantTwoStartX += sizeX
                    }
                }
            }
        }
    }
}
